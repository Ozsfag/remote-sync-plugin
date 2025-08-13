plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.intellij.platform)
}

group = "org.blacksoil.remotesync"
version = "1.1.3"

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
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
