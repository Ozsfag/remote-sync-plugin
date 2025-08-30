rootProject.name = "remote-sync-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories { mavenCentral() }
    versionCatalogs {

    }
}
include("git-diff-service")
include("shared-dto")
include("ssh-sync-service")
include("ui")
