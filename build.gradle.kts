plugins {
    id("org.springframework.boot") version libs.versions.spring.boot.get() apply false
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get() apply false
}

allprojects {
    group = "org.blacksoil"
    version = providers.gradleProperty("version").get()

    repositories {
        mavenCentral()
    }
}
