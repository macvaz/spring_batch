-- Dimensional / reference data (banking entities, credit owners, blocked operations)
-- and minimal input documents required for FK integrity.
-- Loaded on test startup (and optional dev profile).

INSERT INTO "banking_entity" ("id", "external_code", "name", "swift_code", "country", "created_at") VALUES
  (1, 'BANK-001', 'Caixa Public Administration', 'CAIXESBBXXX', 'ES', TIMESTAMP '2026-01-01 00:00:00'),
  (2, 'BANK-002', 'National Credit Registry', 'NCRGESMMXXX', 'ES', TIMESTAMP '2026-01-01 00:00:00'),
  (3, 'BANK-003', 'Regional Development Bank', 'RDBKESM2XXX', 'ES', TIMESTAMP '2026-01-01 00:00:00');

INSERT INTO "procedure" ("id", "external_code", "name", "administration_unit", "status", "description", "created_at", "updated_at") VALUES
  (1, 'PROC-TEST-001', 'Test procedure', 'Test Unit', 'IN_PROGRESS', 'Seeded administrative procedure', TIMESTAMP '2026-01-01 00:00:00', TIMESTAMP '2026-01-01 00:00:00');

INSERT INTO "document" ("id", "procedure_id", "document_code", "title", "mime_type", "content_reference", "checksum", "created_at") VALUES
  (1, 1, 'DOC-T-001', 'Test document', 'application/pdf', 's3://test/doc1.pdf', 'sha256:test1', TIMESTAMP '2026-01-01 00:00:00'),
  (2, 1, 'DOC-T-002', 'Another document', 'application/pdf', 's3://test/doc2.pdf', 'sha256:test2', TIMESTAMP '2026-01-01 00:00:00');

INSERT INTO "input_document" ("id", "document_id", "procedure_id", "citizen_id", "submission_channel", "source_file", "status", "received_at", "processed_at", "error_message") VALUES
  (1, 1, 1, 'CIT-001', 'TEST', 'dimensional-data.sql', 'VALIDATED', TIMESTAMP '2026-01-01 00:00:00', NULL, NULL),
  (2, 2, 1, 'CIT-002', 'TEST', 'dimensional-data.sql', 'VALIDATED', TIMESTAMP '2026-01-01 00:00:00', NULL, NULL);

INSERT INTO "credit_owners_information" ("id", "input_document_id", "name", "national_id", "email", "phone", "address", "ownership_percentage", "role", "created_at", "updated_at") VALUES
  (1, 1, 'Ana Garcia Lopez', '12345678A', 'ana.garcia@example.com', '+34600111222', 'Calle Mayor 1 Madrid', 100.00, 'PRIMARY', TIMESTAMP '2026-01-01 00:00:00', TIMESTAMP '2026-01-01 00:00:00'),
  (2, 2, 'Carlos Ruiz Martin', '87654321B', 'carlos.ruiz@example.com', '+34600333444', 'Av. Libertad 10 Barcelona', 50.00, 'CO_OWNER', TIMESTAMP '2026-01-01 00:00:00', TIMESTAMP '2026-01-01 00:00:00'),
  (3, 2, 'Maria Ruiz Martin', '87654322C', 'maria.ruiz@example.com', '+34600555666', 'Av. Libertad 10 Barcelona', 50.00, 'CO_OWNER', TIMESTAMP '2026-01-01 00:00:00', TIMESTAMP '2026-01-01 00:00:00');

ALTER TABLE "banking_entity" ALTER COLUMN "id" RESTART WITH 10;
ALTER TABLE "procedure" ALTER COLUMN "id" RESTART WITH 10;
ALTER TABLE "document" ALTER COLUMN "id" RESTART WITH 10;
ALTER TABLE "input_document" ALTER COLUMN "id" RESTART WITH 10;
ALTER TABLE "credit_owners_information" ALTER COLUMN "id" RESTART WITH 10;
