plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")

    id("dagger.hilt.android.plugin")
}

android {
    buildToolsVersion = "30.0.3"
    compileSdkVersion = "android-30"

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.5"
        languageVersion = "1.5"
    }

    defaultConfig {
        applicationId = "com.xooloo.android.messenger"

        minSdk = 21
        targetSdk = 30

        // See root build.gradle for version definition
        versionCode = 1
        versionName = "1.0.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "dagger.hilt.android.useFragmentGetContextFix" to "true",
                    "room.schemaLocation" to file("schemas").absolutePath,
                )
            }
        }

        resValue("string", "url_api", "https://dev-server/api/v1/")
        resValue("string", "url_websocket", "ws://dev-server:9001")

        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    val coroutinesVers = "1.5.1"
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVers")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVers")

    // ===== Dependency Injection
    val hiltVers = "2.38.1"
    api("com.google.dagger:hilt-android:$hiltVers")
    kapt("com.google.dagger:hilt-compiler:$hiltVers")

    // Jetpack Components support
    val hiltWorkVers = "1.0.0"
    api("androidx.hilt:hilt-work:$hiltWorkVers")
    kapt("androidx.hilt:hilt-compiler:$hiltWorkVers")

    // ===== Basics
    // Java 8 Date Time Support
    api("com.jakewharton.timber:timber:4.7.1")
    api("com.jakewharton.threetenabp:threetenabp:1.3.1")


    // =====================
    //      Android X
    // =====================
    api("androidx.core:core-ktx:1.6.0")
    api("androidx.appcompat:appcompat:1.3.1")
    api("androidx.fragment:fragment-ktx:1.3.6")
    api("androidx.recyclerview:recyclerview:1.2.1")

    val roomVers = "2.3.0"
    api("androidx.room:room-runtime:$roomVers")
    api("androidx.room:room-ktx:$roomVers")
    // optional - Kotlin Extensions and Coroutines support for Room

    kapt("androidx.room:room-compiler:$roomVers")

    // ---- Work Manager
    api("androidx.work:work-runtime-ktx:2.5.0")

    // =====================
    //     Third Party
    // =====================
    val okhttpVers = "4.9.1"
    api("com.squareup.okhttp3:okhttp:$okhttpVers")

    val moshiVers = "1.12.0"
    api("com.squareup.moshi:moshi:$moshiVers")
    api("com.squareup.moshi:moshi-adapters:$moshiVers")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVers")

    val retrofitVers = "2.9.0"
    api("com.squareup.retrofit2:retrofit:$retrofitVers")
    api("com.squareup.retrofit2:converter-moshi:$retrofitVers")
}
