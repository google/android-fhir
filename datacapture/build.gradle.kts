plugins {
    id(deps.Plugins.androidLib)
    id(deps.Plugins.kotlinAndroid)
    id(deps.Plugins.mavenPublish)
}
val group = "com.google.android.fhir"
val version = "0.1.0-alpha01"

//tasks.register("sourceJar", Jar::class) {
//    archiveClassifier.set("sources")
//    from(android.sourceSets.getByName("main").java.srcDirs)
//}

// just playing around with tasks
val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").java.srcDirs)
}

afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {
                artifact(sourcesJar)
                from (components["java"])
                artifactId = "data-capture"
                // Also publish source code for developers" convenience
                pom {
                    name.set("Android FHIR Structured Data Capture Library")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }
}

val packageName = "com.google.android.fhir.datacapture"
val pkg = "package"

android {
    compileSdkVersion(versions.Sdk.compileSdk)
    buildToolsVersion(versions.Plugins.buildTools)

    defaultConfig {
        minSdkVersion(versions.Sdk.minSdk)
        targetSdkVersion(versions.Sdk.targetSdk)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner ("androidx.test.runner.AndroidJUnitRunner")
        // Need to specify this to prevent junit runner from going deep into our dependencies
        testInstrumentationRunnerArguments(mapOf(pkg to packageName))
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        // See https://developer.android.com/studio/write/java8-support
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 8
        // See https://developer.android.com/studio/write/java8-support
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        // See https://developer.android.com/studio/write/java8-support
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

configurations {
    all {
        exclude (module = "xpp3")
    }
}

dependencies {

    androidTestImplementation(deps.TestDependencies.CoreTestDeps.core)
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.extJunit)
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.extJunitKtx)
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.runner)
    androidTestImplementation(deps.TestDependencies.truth)

    api(deps.AppDependencies.CoreDeps.Cql.hapiR4)

    coreLibraryDesugaring(deps.AppDependencies.CoreDeps.desugar)

    implementation(deps.AppDependencies.CoreDeps.appCompat)
    implementation(deps.AppDependencies.Kotlin.androidxCoreKtx)
    implementation(deps.AppDependencies.CoreDeps.fragment)
    implementation(deps.AppDependencies.Kotlin.kotlin)
    implementation(deps.AppDependencies.Kotlin.kotlinTesting)
    implementation(deps.AppDependencies.Lifecycle.viewModelKtx)
    implementation(deps.AppDependencies.CoreDeps.materialDesign)

    testImplementation(deps.TestDependencies.CoreTestDeps.core)
    testImplementation(deps.TestDependencies.CoreTestDeps.extJunit)
    testImplementation(deps.TestDependencies.roboelectric)
    testImplementation(deps.TestDependencies.truth)
}