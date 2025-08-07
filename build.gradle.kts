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
    // Подключаем IntelliJ Community Edition 2025.1.4 как платформу
    intellijPlatform {
        intellijIdeaCommunity("2025.1.4")
    }

    implementation("com.jcraft:jsch:0.1.55")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

intellijPlatform {
    pluginConfiguration {
        name.set("Remote Sync")
        description.set("Syncs local changes to a remote server over SSH")
        version.set("1.0.0")
        vendor {
            name.set("blacksoil.org")
        }
    }
}

