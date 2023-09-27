rootProject.name = "koru"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io" )
    }
}

include(":benchmark")
include(":lib")
include(":sys")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")