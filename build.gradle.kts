plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.bde"
version = "1.0.0-SNAPSHOT"
description = "Spring Batch application for electronic processing of public administration files"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.oracle.database.jdbc:ojdbc11")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("failed", "skipped")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(desc: TestDescriptor, result: TestResult) {
            if (desc.className != null) {
                val status = when (result.resultType) {
                    TestResult.ResultType.SUCCESS -> "PASSED"
                    TestResult.ResultType.FAILURE -> "FAILED"
                    TestResult.ResultType.SKIPPED -> "SKIPPED"
                }
                println("  $status  ${desc.className}.${desc.name} (${result.endTime - result.startTime} ms)")
            }
        }
        override fun afterSuite(desc: TestDescriptor, result: TestResult) {
            if (desc.parent == null) {
                val outcome = when {
                    result.failedTestCount > 0 -> "FAILED"
                    result.skippedTestCount > 0 -> "COMPLETED WITH SKIPS"
                    else -> "SUCCESS"
                }
                println(
                    """
                    |
                    |Test summary: $outcome
                    |  ${result.testCount} tests run
                    |  ${result.successfulTestCount} passed
                    |  ${result.failedTestCount} failed
                    |  ${result.skippedTestCount} skipped
                    |  ${result.resultType} in ${result.endTime - result.startTime} ms
                    |Report: build/reports/tests/test/index.html
                    """.trimMargin()
                )
            }
        }
    })
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveBaseName.set("admin-document-processing")
}
