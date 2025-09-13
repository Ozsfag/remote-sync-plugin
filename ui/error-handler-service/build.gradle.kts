plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.intellij.platform)
}

group = "org.blacksoil.ui.error"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellij.idea.get())
    }
    implementation(libs.org.json)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

tasks.test { useJUnitPlatform() }
