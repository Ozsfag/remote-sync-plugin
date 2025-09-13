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
    versionCatalogs {  }
}
include(":git-diff-service")
include(":shared-dto")
include(":ssh-sync-service")
include(":api-gateway")
include(":ui")
include(":ui:plugin-toolbar-service")
include(":ui:plugin-toolbar-service:toolbar-view")
include(":ui:plugin-toolbar-service:toolbar-app")
include(":ui:welcome-page-service")
include(":ui:error-handler-service")
