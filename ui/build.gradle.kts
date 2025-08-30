plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.intellij.platform)
    id("idea")
}

group = "org.blacksoil.ui"
version = "1.1.6"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellij.idea.get())
    }

    implementation(project(":shared-dto"))
    implementation(libs.org.json)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.junit4)
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

idea {
    module {
        // исключаем кеши и сборку
        excludeDirs.add(file("$projectDir/.gradle"))
        excludeDirs.add(file("$projectDir/build"))

    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
        resources.setSrcDirs(listOf("src/main/resources"))
    }
    test {
        java.setSrcDirs(listOf("src/test/java"))
        resources.setSrcDirs(listOf("src/test/resources"))
    }
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
