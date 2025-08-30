plugins {
    id("java")
}

group = "org.blacksoil.shareddto"
version = "1.1.6"

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}