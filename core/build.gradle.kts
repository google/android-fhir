plugins {
    id(deps.Plugins.androidLib)
    id(deps.Plugins.kotlinAndroid)
    id(deps.Plugins.kotlinKapt)
}

//apply(plugin =  "com.android.library")
//apply plugin =  'kotlin-android'
//apply plugin =  'kotlin-kapt'

val packageName = "com.google.android.fhir.datacapture"
val pkg = "package"

android {
    compileSdkVersion(versions.Sdk.compileSdk)
    defaultConfig {
        minSdkVersion(versions.Sdk.minSdk)
        targetSdkVersion(versions.Sdk.targetSdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner(deps.TestDependencies.standardRunner)
        // need to specify this to prevent junit runner from going deep into our dependencies
        testInstrumentationRunnerArguments(mapOf(pkg to packageName))
        // Required when setting minSdkVersion to 20 or lower
        // See https = //developer.android.com/studio/write/java8-support
        multiDexEnabled = true
    }
//    sourceSets {
//        // shared code between junit and instrumentation tests
//        androidTest.java.srcDirs += "src/test-common/java"
//        test.java.srcDirs += "src/test-common/java"
//        test {
//            resources.srcDirs += ["sampledata"]
//        }
//        androidTest {
//            resources.srcDirs += ["sampledata"]
//        }
//    }
//    sourceSets {
//        // shared code between junit and instrumentation tests
//        val androidTest by getting
//        androidTest.java.srcDirs.add("src/test-common/java")
//        test.java.srcDirs.add("src/test-common/java")
//        test {
//            resources.srcDirs.add("sampledata")
//        }
//        androidTest {
//            resources.srcDirs.add("sampledata")
//        }
//    }
    // experimental, modify it
    sourceSets {
        map { it.java.srcDir("src/${it.name}/kotlin") }
    }

//    sourceSets["androidTest"].apply {
//        // shared code between junit and instrumentation tests
//        java.srcDir("src/test-common/java")
//        test.java.srcDirs += "src/test-common/java"
//        test {
//            resources.srcDirs += ["sampledata"]
//        }
//        androidTest {
//            resources.srcDirs += ["sampledata"]
//        }
//    }

//sourceSets.getByName("androidTest")

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        // See https = //developer.android.com/studio/write/java8-support
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 8
        // See https = //developer.android.com/studio/write/java8-support
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude("META-INF/ASL-2.0.txt")
        exclude("META-INF/LGPL-3.0.txt")
    }
    // See https = //developer.android.com/studio/write/java8-support
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

configurations {
    all {
        exclude (module = "json")
        exclude (module = "xpp3")
        exclude (module = "hamcrest-all")
        exclude (module = "jaxb-impl")
        exclude (module = "jaxb-core")
        exclude (module = "jakarta.activation-api")
        exclude (module = "javax.activation")
        exclude (module = "jakarta.xml.bind-api")
        // TODO =  the following line can be removed from the next CQL engine release.
        exclude (module = "hapi-fhir-jpaserver-base")
    }
}

dependencies {
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.core)
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.extJunit)
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.junit)
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.extJunitKtx)
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.runner)
    androidTestImplementation(deps.TestDependencies.truth)
//    androidTestImplementation (deps.atsl.core)
//    androidTestImplementation (deps.atsl.ext_junit_ktx)
//    androidTestImplementation (deps.atsl.runner)
//    androidTestImplementation (deps.junit)
//    androidTestImplementation (deps.truth)

    api(deps.AppDependencies.CoreDeps.Cql.cqlEngineCore)
    api(deps.AppDependencies.CoreDeps.Cql.hapiR4) {
        exclude(module = "junit")
    }
//    api(deps.cql_engine.core)
//    api (deps.hapi_r4) {
//        exclude module =  'junit'
//    }

    coreLibraryDesugaring(deps.AppDependencies.CoreDeps.desugar)
//    coreLibraryDesugaring deps.desugar

    implementation(deps.AppDependencies.Kotlin.kotlin)

    // Needed for use of HAPI's FHIRPathEngine.
    // See https = //github.com/hapifhir/hapi-fhir/issues/2444.
    implementation(deps.AppDependencies.CoreDeps.Room.runtime)
    implementation(deps.AppDependencies.CoreDeps.Room.ktx)
    implementation(deps.AppDependencies.CoreDeps.work)
    implementation(deps.AppDependencies.Externals.jsonTools)
    implementation(deps.AppDependencies.Externals.guava)
    implementation(deps.AppDependencies.Externals.caffeine)

    kapt(deps.AppDependencies.CoreDeps.Room.compiler)

//    implementation deps.caffeine
//    implementation deps.cql_engine.fhir
//    implementation deps.guava
//    implementation deps.kotlin.stdlib
//    implementation deps.room.ktx
//    implementation deps.room.runtime
//    implementation deps.work.runtime
//    implementation deps.json_tools.json_patch
//
//    kapt deps.room.compiler

    testImplementation(deps.TestDependencies.CoreTestDeps.core)
    testImplementation(deps.TestDependencies.CoreTestDeps.extJunit)
    testImplementation(deps.TestDependencies.truth)
    testImplementation(deps.TestDependencies.roboelectric)

//    testImplementation deps.atsl.core
//    testImplementation deps.junit
//    testImplementation deps.robolectric
//    testImplementation deps.truth
}
