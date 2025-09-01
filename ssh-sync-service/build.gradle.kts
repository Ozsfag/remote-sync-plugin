plugins {
    id("java")
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
}
group = "org.blacksoil.sshsync"
version = "1.1.6"

dependencies {
    implementation(libs.spring.boot.web)
    implementation(libs.jsch)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.test)
}

tasks.test {
    useJUnitPlatform()
}