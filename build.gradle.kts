plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.7.0"
}

group = "org.blacksoil.remotesync"
version = "1.1.2"

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
    // (не обязательно, но удобно централизовать мета‑инфо)
    pluginConfiguration {
        name.set("Remote Sync")
        description.set("Syncs local changes to a remote server over SSH")
        version.set(project.version.toString())
        vendor {
            name.set("BlackSoil")
            email.set("emeliangaiday@gmail.com")
        }
    }

    // ✅ Публикация на Marketplace — подпишется сервером автоматически
    publishing {
        // передаётся через переменную окружения JB_PUBLISH_TOKEN
        token.set(providers.environmentVariable("JB_PUBLISH_TOKEN"))
        // channels.set(listOf("default")) // при необходимости
    }

    // Если когда‑нибудь решишь подписывать локально — раскомментируй:
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
