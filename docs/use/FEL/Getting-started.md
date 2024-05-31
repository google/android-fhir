# Getting Started

This page describes how to configure an Android Studio project to use the FHIR Engine Library, and simple examples of how to use the library.

This guide is intended for developers who are familiar with basic Android development with Kotlin, and proficient in working with [FHIR](http://hl7.org/fhir/) concepts and resources.

## Dependencies

The FHIR Engine Library is available through [Google's Maven repository](https://maven.google.com/web/index.html), which should be already configured for Android projects created in Android Studio 3.0 and higher.

Add the following dependency to your module's app-level `build.gradle` file, typically `app/build.gradle`:

```kotlin
dependencies {
  implementation("com.google.android.fhir:engine:0.1.0-beta02")
}
```

In the same file, add the following APK packaging options:

```kotlin
android {
  // ...
  packagingOptions {
    resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"))
  }
}
```

The minimum API level supported is 24. The library also requires [Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin)
version 4.0.0 or later for [Java 8+ API desugaring support](https://developer.android.com/studio/write/java8-support#library-desugaring).

### Initialize and get a FHIR Engine

To use a FHIR Engine instance, first call `FhirEngineProvider.init()` with a `FhirEngineConfiguration`. This must be done only once; we recommend doing this in the `onCreate()` function of your `Application` class.

```kotlin
class MyApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        RECREATE_AT_OPEN
      )
    )
  }
}
```

When you need to use a `FhirEngine`, make sure to call the method `FhirEngineProvider.getInstance(Context)`:

```kotlin
val fhirEngine = FhirEngineProvider.getInstance(this)
```

See [FhirApplication](https://github.com/google/android-fhir/blob/master/demo/src/main/java/com/google/android/fhir/demo/FhirApplication.kt) in the demo app for a sample implementation.

## Next steps

* [Manage FHIR resources Locally](Manage-FHIR-resources-locally.md)
* [Search FHIR resources](Search-FHIR-resources.md)
* [Sync data with a FHIR server](Sync-data-with-FHIR-server.md)
