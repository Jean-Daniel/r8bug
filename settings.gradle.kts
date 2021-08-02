pluginManagement {
    val kotlinVersion: String by settings
    val gradlePluginVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm").version(kotlinVersion)
        id("org.jetbrains.kotlin.kapt").version(kotlinVersion)

        id("com.android.application").version(gradlePluginVersion)
        kotlin("android") version kotlinVersion
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application" -> useModule("com.android.tools.build:gradle:$gradlePluginVersion")

                "dagger.hilt.android.plugin" -> useModule("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
            }
        }
    }

    repositories {
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

include("jetpack")
