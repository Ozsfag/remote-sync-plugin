plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.7.0"
}

group = "org.blacksoil.remotesync"
version = "1.1.3"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.1.4")
    }
    implementation("com.jcraft:jsch:0.1.55")
}

// Java 21
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
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

    // Маркетплейс сам подпишет и опубликует по токену
    publishing {
        token.set(providers.environmentVariable("JB_PUBLISH_TOKEN"))
        // channels.set(listOf("default"))
    }

    // Если когда‑нибудь решишь подписывать локально:
    // signing {
    //     certificateChain.set(layout.projectDirectory.file("certs/chain.crt").asFile.readText())
    //     privateKey.set(layout.projectDirectory.file("certs/private_key.pem").asFile.readText())
    //     password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    // }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
    main {
        resources.srcDirs("src/main/resources")
    }
}

// Удобный агрегирующий таск для CI/локально
tasks.register("publishRelease") {
    dependsOn("buildPlugin", "signPlugin", "publishPlugin")
}
