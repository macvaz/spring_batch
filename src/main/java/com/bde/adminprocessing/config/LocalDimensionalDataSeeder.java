package com.bde.adminprocessing.config;

import com.bde.adminprocessing.repository.BankingEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Seeds dimensional reference data once on an empty local H2 database.
 * Avoids duplicate-key errors on restart when using a file-based H2 store.
 */
@Component
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class LocalDimensionalDataSeeder implements ApplicationRunner {

    private final BankingEntityRepository bankingEntityRepository;
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        if (bankingEntityRepository.count() > 0) {
            log.debug("Dimensional data already present; skipping local seed");
            return;
        }
        log.info("Seeding dimensional data (local profile, empty database)");
        var populator = new ResourceDatabasePopulator(false, false, null,
                new ClassPathResource("data/dimensional-data.sql"));
        populator.execute(dataSource);
    }
}
