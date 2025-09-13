plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.intellij.platform)
}

group = "org.blacksoil.ui.toolbar"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellij.idea.get())
        bundledPlugins("com.intellij.java")
    }

    api(project(":shared-dto"))
    implementation(libs.json)

    implementation(project(":ui:error-handler-service"))

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

tasks.test { useJUnitPlatform() }
