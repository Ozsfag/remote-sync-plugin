plugins {
    id("java")
}

group = "org.blacksoil.shareddto"

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}