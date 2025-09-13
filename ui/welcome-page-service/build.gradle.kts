plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.intellij.platform)
}

group = "org.blacksoil.ui.welcome"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellij.idea.get())
        bundledPlugins("com.intellij.java")
    }

    implementation(project(":ui:error-handler-service"))
    compileOnly(project(":ui:plugin-toolbar-service"))

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

tasks.test { useJUnitPlatform() }
