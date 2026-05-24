# Public Administration Document Processing

Spring Batch application for electronic processing of public administration files. **Dimensional data** (procedures, input documents, credit owners, banking entities) is loaded from SQL seed data. The only **incoming CSV file** from external systems is `blocked_operations.csv`, which is validated and stored in `credit_operation_blocked`. A batch step can still run electronic processing (signature, notification) on validated input documents to produce **output documents**.

## Domain model

| Table | Role |
|-------|------|
| `procedure` | Administrative procedure (permit, certificate, etc.) |
| `document` | Shared metadata for any file in a procedure |
| `input_document` | Citizen submission linked to a procedure |
| `output_document` | Processed result delivered back to the citizen |
| `credit_owners_information` | Credit holders linked to an input document (name, national ID, contact, ownership %) |
| `banking_entity` | Financial institution issuing a credit block |
| `credit_operation_blocked` | Blocked credit operation for an owner at a banking entity |

Relationships:

- A **procedure** has many **documents**.
- An **input_document** references one **document** and one **procedure**.
- An **output_document** is produced from an **input_document** and has its own **document** row (signed content reference).
- **credit_owners_information** rows belong to an **input_document** (one submission may list several owners).

### credit_owners_information columns

| Column | Description |
|--------|-------------|
| `name` | Full legal name |
| `national_id` | National identification number (unique per input document) |
| `email`, `phone`, `address` | Contact details |
| `ownership_percentage` | Share of credit ownership (optional) |
| `role` | `PRIMARY`, `CO_OWNER`, or `GUARANTOR` |

### credit_operation_blocked columns

| Column | Description |
|--------|-------------|
| `operation_id` | Unique business identifier of the credit operation |
| `owner_id` | FK to `credit_owners_information` |
| `banking_entity_id` | FK to `banking_entity` |
| `blocked_amount`, `currency` | Amount and currency (e.g. EUR) |
| `block_reason` | Reason for the block |
| `block_status` | `ACTIVE`, `RELEASED`, or `PENDING` |
| `blocked_at`, `released_at` | Block lifecycle timestamps |
| `block_month` | Accounting period (e.g. `2026-01`) |
| `external_reference` | Reference from the banking system |

## Incoming file: `blocked_operations.csv`

The file watcher and upload API only accept **`blocked_operations.csv`** (see `app.blocked-operations-filename`).

```csv
operation_id,owner_id,entity_id,block_month,blocked_amount,currency,block_reason,block_status,external_reference
```

| CSV column | Validated against |
|------------|-------------------|
| `owner_id` | `credit_owners_information.national_id` |
| `entity_id` | `banking_entity.external_code` |
| `operation_id` | unique in `credit_operation_blocked` |

Sample: `data/incoming/blocked_operations.csv`

## Batch pipeline (internal)

1. **importCsvStep** — optional; not used for external file intake.
2. **electronicProcessingStep** — for each validated `input_document`, calls signature and notification services, creates `output_document`.

## Running the application

Requirements: Java 17+

```bash
./gradlew bootRun
```

### Ingest options

1. **Drop folder** — copy `blocked_operations.csv` into `./data/incoming/` (polled every 15s).
2. **REST upload** — `POST /api/files/upload` with multipart field `file` (must be named `blocked_operations.csv`).

### Query API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/procedures` | List procedures |
| GET | `/api/procedures/{code}` | Procedure by external code |
| GET | `/api/documents/inputs/{id}` | Input document with outputs |
| GET | `/api/documents/inputs/by-code/{documentCode}` | Input by document code |
| GET | `/api/documents/inputs/{inputId}/outputs` | Output documents for an input |
| GET | `/api/documents/outputs/{id}` | Single output document |
| GET | `/api/credit-owners/{id}` | Credit owner by id |
| GET | `/api/credit-owners/by-input/{inputDocumentId}` | Credit owners for an input document |
| GET | `/api/banking-entities` | List banking entities |
| GET | `/api/credit-operations/blocked/{id}` | Blocked operation by id |
| GET | `/api/credit-operations/blocked/by-operation/{operationId}` | Blocked operation by business id |
| GET | `/api/credit-operations/blocked/by-owner/{ownerId}` | Blocks for a credit owner |
| GET | `/api/credit-operations/blocked?status=ACTIVE` | List blocks (optional status filter) |

H2 console ( **`local`** profile only): http://localhost:8080/h2-console — JDBC URL `jdbc:h2:file:./data/admin-processing`.

### Spring profiles

| Profile | Database | Typical use |
|---------|----------|-------------|
| `local` | H2 file + dimensional SQL seed | Local development (default when no profile is set) |
| `test` | **Oracle** | Deployed TEST environment |
| `pre` | **Oracle** | Deployed PRE environment |

JUnit tests always use in-memory H2 via `src/test/resources/application.yml` (they do **not** activate the `test` Spring profile).

**Oracle (`test` / `pre`)** — set credentials and connection, then run:

```bash
export SPRING_PROFILES_ACTIVE=test
export ORACLE_USER=app_user
export ORACLE_PASSWORD=secret
# optional for test profile:
export ORACLE_HOST=oracle-test.example
export ORACLE_PORT=1521
export ORACLE_SERVICE=ORCLPDB1

./gradlew bootRun
```

| Variable | `test` profile | `pre` profile |
|----------|----------------|---------------|
| `ORACLE_USER` | required | required |
| `ORACLE_PASSWORD` | required | required |
| `ORACLE_HOST` | optional (default `localhost`) | required |
| `ORACLE_SERVICE` | optional (default `ORCLPDB1`) | required |
| `ORACLE_PORT` | optional (default `1521`) | optional (default `1521`) |
| `SPRING_SQL_INIT_MODE` | optional (`never` default; set `always` once to load `dimensional-data-oracle.sql` on an empty schema) | not used (`never`) |

Profile behaviour:

- **`test`**: `ddl-auto: update`, Spring Batch schema initialized, H2 console disabled.
- **`pre`**: `ddl-auto: validate` (schema owned by migrations/DBA), no SQL seed, H2 console disabled.

## External integrations

By default, mock clients simulate:

- Digital signature service → `signatureReference`, signed content path
- Notification service → `notificationReference`, delivery channel

To call real HTTP services, set in `application.yml`:

```yaml
integration:
  signature:
    mock: false
    base-url: https://signature-service.example
  notification:
    mock: false
    base-url: https://notification-service.example
```

Expected endpoints:

- `POST {signature.base-url}/api/v1/sign`
- `POST {notification.base-url}/api/v1/notifications`

## Example flow

```bash
# Start app, then upload sample CSV
curl -F "file=@data/incoming/blocked_operations.csv" http://localhost:8080/api/files/upload

# Fetch output for a submission
curl http://localhost:8080/api/documents/inputs/by-code/DOC-2026-0001
```

## Tests

```bash
./gradlew test
```

Each executed test is printed on its own line (`PASSED` / `FAILED` / `SKIPPED`), then a summary block with counts and duration.

```text
  PASSED  com.bde.adminprocessing.integration.LoadSeedDataIntoDatabaseIntegrationTest.dimensionalDataIsLoadedFromSql()
  PASSED  com.bde.adminprocessing.integration.MockingExternalServicesIntegrationTest.processesSeededInputDocumentsThroughElectronicProcessingStep()
  PASSED  com.bde.adminprocessing.integration.BlockedOperationsIntegrationTests.importsValidBlockedOperations()

Test summary: SUCCESS
  2 tests run
  2 passed
  ...
```

Useful variants:

| Command | Purpose |
|---------|---------|
| `./gradlew test --rerun` | Force re-run (skip up-to-date cache) |
| `./gradlew test --console=plain` | Plain console (no Gradle progress bar) |
| `./gradlew test --tests "com.bde.adminprocessing.integration.*"` | Integration tests only |
| `./gradlew test --tests "com.bde.adminprocessing.unit.*"` | Unit tests only |
| `./gradlew test --info` | Verbose Gradle + test logging |

Reports:

- HTML: `build/reports/tests/test/index.html`
- XML (CI/tools): `build/test-results/test/TEST-*.xml`

Avoid `-q` / `--quiet` — it hides the per-test lines and summary.

### Test layout

Tests are split into **`unit`** (fast, mocked) and **`integration`** (full Spring context + H2).

```
src/test/java/com/bde/adminprocessing/
├── unit/
│   └── config/
│       └── IncomingFileWatcherTest
└── integration/                             # @SpringBootTest + H2
    ├── BlockedOperationsIntegrationTests
    ├── LoadSeedDataIntoDatabaseIntegrationTest
    └── MockingExternalServicesIntegrationTest

src/test/resources/
├── application.yml                          # Test profile (H2, SQL seed, mocks)
├── blocked_operations_valid.csv
├── blocked_operations_invalid.csv
└── blocked_operations_duplicate.csv
```

| Class | What it exercises |
|-------|-------------------|
| `IncomingFileWatcherTest` | File watcher routes only `blocked_operations.csv` |
| `LoadSeedDataIntoDatabaseIntegrationTest` | Dimensional SQL seed (banks, owners, input documents) |
| `MockingExternalServicesIntegrationTest` | Batch electronic-processing step → output documents (mocked services) |
| `BlockedOperationsIntegrationTests` | CSV read/import, validation errors, missing file |

### Dimensional data (H2 SQL seed)

Integration tests use an in-memory H2 database pre-loaded from:

`src/main/resources/data/dimensional-data.sql`

| Seeded table | Sample keys |
|--------------|-------------|
| `banking_entity` | `BANK-001`, `BANK-002`, `BANK-003` |
| `input_document` / `document` | `DOC-T-001`, `DOC-T-002` |
| `credit_owners_information` | `12345678A`, `87654321B`, `87654322C` |

`credit_operation_blocked` is **not** seeded by SQL — it is loaded from `blocked_operations.csv` in `BlockedOperationsIntegrationTests`.

Spring runs `dimensional-data.sql` after Hibernate creates the schema (`defer-datasource-initialization: true`).

**Local H2** uses profile `local` (active by default via `spring.profiles.default`):

```bash
./gradlew bootRun
# or explicitly:
./gradlew bootRun --args='--spring.profiles.active=local'
```

H2 console: http://localhost:8080/h2-console — JDBC URL `jdbc:h2:file:./data/admin-processing`.

`MockingExternalServicesIntegrationTest` runs `electronicProcessingStep` on input documents seeded by `dimensional-data.sql` (no submission CSV).

Blocked-operation CSV fixtures (`src/test/resources/`):

| File | Purpose |
|------|---------|
| `blocked_operations_valid.csv` | Valid import |
| `blocked_operations_invalid.csv` | Validation error path |
| `blocked_operations_duplicate.csv` | Duplicate `operation_id` |
