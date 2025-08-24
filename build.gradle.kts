plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.intellij.platform)
}

group = "org.blacksoil.remotesync"
version = "1.1.5"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellij.idea.get())
    }

    implementation(libs.jsch)

    // Lombok (если нужен в коде плагина)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // тесты (если нужны) — JUnit:
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.junit4) // ← для совместимости с IntelliJ
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

intellijPlatform {
    pluginConfiguration {
        name.set("Remote Sync")
        version.set(project.version.toString())
        vendor {
            name.set("BlackSoil")
            email.set("emeliangaiday@gmail.com")
        }
    }
    publishing {
        token.set(providers.environmentVariable("JB_PUBLISH_TOKEN"))
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
    main { resources.srcDirs("src/main/resources") }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("publishRelease", fun Task.() {
    dependsOn("buildPlugin", "signPlugin", "publishPlugin")
})
