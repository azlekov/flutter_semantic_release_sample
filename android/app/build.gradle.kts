import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

// Task to generate snapshot version
tasks.register<Exec>("generateSnapshotVersion") {
    description = "Generate snapshot version and update local.properties"
    workingDir = rootProject.projectDir.parentFile
    commandLine("bash", "scripts/generate_snapshot_version.sh")
}

// Make sure the task runs before any build variant tasks
tasks.whenTaskAdded {
    if (name.startsWith("preBuild") || name == "prepareKotlinBuildScriptModel") {
        dependsOn("generateSnapshotVersion")
    }
}

android {
    namespace = "com.example.flutter_semantic_release_sample"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.example.flutter_semantic_release_sample"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        
        // Load local.properties to get snapshot versions
        // The generateSnapshotVersion task will have updated these values
        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties()
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        
        // Use snapshot version from local.properties if available, otherwise fallback to flutter defaults
        // This allows local development builds to have unique version identifiers
        versionCode = localProperties.getProperty("snapshot.versionCode")?.toIntOrNull() ?: flutter.versionCode
        versionName = localProperties.getProperty("snapshot.versionName") ?: flutter.versionName
    }

    buildTypes {
        release {
            // TODO: Add your own signing config for the release build.
            // Signing with the debug keys for now, so `flutter run --release` works.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

flutter {
    source = "../.."
}
