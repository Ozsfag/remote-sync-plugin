plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "org.blacksoil.remotesync.backend"
version = "0.1.0"

repositories { mavenCentral() }

dependencies {
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.jackson.databind)
    implementation(libs.caffeine)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.spring.configuration.processor)

    testImplementation(libs.spring.boot.starter.test)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

tasks.test {
    useJUnitPlatform()
}
