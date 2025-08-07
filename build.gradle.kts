plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.7.0"
}

group = "org.blacksoil.remotesync"
version = "1.0.0"

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

intellijPlatform {
    pluginConfiguration {
        name.set("Remote Sync")
        description.set(
            """
            Sync your Git changes to a remote server over SSH with ease.<br/>
            • Uploads new and modified files<br/>
            • Deletes removed files on the remote<br/>
            • Works directly from your Git working directory
            """.trimIndent()
        )
        version.set("1.0.1")
        vendor {
            name.set("BlackSoil")
            email.set("emeliangaiday@gmail.com")
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Важно! Чтобы ресурсы подтягивались:
sourceSets {
    main {
        resources.srcDirs("src/main/resources")
    }
}
