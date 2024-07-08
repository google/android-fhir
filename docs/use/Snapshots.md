# Snapshots

You can test the latest Android FHIR SDK libraries using the snapshot versions published on GitHub Packages. 

They are unreleased versions of the library built from the `HEAD` of the main branch and have the `-SNAPSHOT` suffix in their version numbers.

They can be found [here](https://github.com/orgs/google/packages?repo_name=android-fhir).

> :warning: The snapshots are for testing and development purposes only. They are not QA tested and not production ready. Do **NOT** use them in production.

# How to use SNAPSHOT artifacts

## Configure GitHub maven repositories in `build.gradle.kts`

Since these artifacts are deployed on GitHub Packages, a `username`/`GitHub token` pair is required as explained in [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages). The token needs at least the `read:packages` scope.

This can be securely managed by placing the credentials in the `local.properties` file and loading them with `gradleLocalProperties`. With this approach, the file `build.gradle.kts` will look like:

```kotlin
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  ...
}

android {
  ...
  repositories{
    maven {
      url = uri("https://maven.pkg.github.com/google/android-fhir")
      credentials {
        username = gradleLocalProperties(rootDir).getProperty("gpr.user") ?: System.getenv("GPR_USER")
        password = gradleLocalProperties(rootDir).getProperty("gpr.key") ?: System.getenv("GPR_KEY")
      }
    }
  }
}

dependencies {
}
```

Notice the environment variables `GPR_USER`/`GPR_KEY` used in this file.

Then, the file `local.properties` will need to be created in the project root folder:

```dotenv
sdk.dir=<path to Android SDK>
gpr.user=<Your GitHub Account>
gpr.key=<A GitHub token>
```

## Declare dependencies

To include the snapshots in the dependencies of your app, modify `build.gradle.kts` in your app:

```kotlin
dependencies {
  ...
  implementation("com.google.android.fhir:engine:<engine-version>-SNAPSHOT")
  implementation("com.google.android.fhir:data-capture:<dc-version>-SNAPSHOT")
}
```

The versions `<...-version>` can be found in https://github.com/google?tab=packages&repo_name=android-fhir

## How SNAPSHOT versions are managed by Gradle

The complete documentation can be found in the section [Declaring a changing version](https://docs.gradle.org/current/userguide/dynamic_versions.html#sub:declaring_dependency_with_changing_version).

To summarize:

* By default, Gradle caches changing versions of dependencies for **24 hours**
* Dependency caching can be [controlled programmatically](https://docs.gradle.org/current/userguide/dynamic_versions.html#sec:controlling_dependency_caching_programmatically)
* The `--refresh-dependencies` option in command line tells Gradle to ignore all cached versions


