plugins {
    alias(libs.plugins.java)
}

group = "org.blacksoil.ui.toolbar.app"

repositories { mavenCentral() }

dependencies {
    implementation(project(":shared-dto"))
    implementation(libs.json)

    implementation(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}"))
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework:spring-context")

    configurations.all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-core")
    }

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }
tasks.test { useJUnitPlatform() }
