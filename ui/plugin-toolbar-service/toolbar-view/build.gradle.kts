plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.intellij.platform)
}

group = "org.blacksoil.ui.toolbar.view.view"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellij.idea.get())
        bundledPlugins("com.intellij.java")
    }
    implementation(project(":ui:plugin-toolbar-service:toolbar-app"))
    implementation(project(":ui:error-handler-service"))

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

tasks.test { useJUnitPlatform() }
