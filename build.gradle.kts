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
    implementation(libs.org.json)

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

tasks.named<org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask>("runIde") {
    val githubToken = System.getenv("REMOTE_SYNC_GH_TOKEN")
    val repoOwner = System.getenv("REMOTE_SYNC_REPO_OWNER")
    val repoName = System.getenv("REMOTE_SYNC_REPO_NAME")

    if (githubToken != null && repoOwner != null && repoName != null) {
        println("✅ Injecting GitHub secrets into JVM")
        this.jvmArgs = listOf(
            "-DGITHUB_TOKEN=$githubToken", "-DGITHUB_REPO_OWNER=$repoOwner", "-DGITHUB_REPO_NAME=$repoName"
        )
    } else {
        println("⚠️ GitHub secrets not found — bug reporting may fail")
    }
}
