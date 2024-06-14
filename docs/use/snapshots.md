# SNAPSHOT versions

The latest version of the Android FHIR SDK libraries can be utilized during the development stages of mobile applications by using SNAPSHOT versions. 

These SNAPSHOT versions are updated with each commit to the main branch and are considered unstable.


**So, please use them for test purposes and in development stages and Do NOT use them in production**


These SNAPSHOT Artifacts are pushed on GitHub packages: https://github.com/google?tab=packages&repo_name=android-fhir

# How to use SNAPSHOT artifacts

## Configure GitHub maven repositories in `build.gradle.kts`

Since these artifacts are deployed on GitHub packages, a `username`/`GitHub token` pair is required as explained in [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages) from GitHub Docs. The token need at least the `read:packages` scope.

This can be securely managed by placing the credentials in the `local.properties` file and loading them with `gradleLocalProperties`. Following to this approach, the file `build.gradle.kts` will look like:

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
In this snippet, the environment variables `GPR_USER`/`GPR_KEY` can be used as well.

Then the file `local.properties` will need to be created a root folder:

```dotenv
sdk.dir=<path to Android SDK>
gpr.user=<Your GitHub Account>
gpr.key=<A GitHub token>
```





## Declare dependencies

the file `build.gradle.kts` can be completed with the SNAPSHOT dependencies:

```kotlin
dependencies {
  ....
  implementation("com.google.android.fhir:engine:<engine-version>-SNAPSHOT")
  implementation("com.google.android.fhir:data-capture:<dc-version>-SNAPSHOT")
}
```

The versions `<...-version>` can be found in https://github.com/google?tab=packages&repo_name=android-fhir


## How SNAPSHOT versions are managed by Gradle

The complete documentation can be found in the section  [Declaring a changing version](https://docs.gradle.org/current/userguide/dynamic_versions.html#sub:declaring_dependency_with_changing_version) from Gradle.

Main points are:
- By default, Gradle caches changing versions of dependencies for **24 hours**
- [Dependency caching can be controlled programmatically](https://docs.gradle.org/current/userguide/dynamic_versions.html#sec:controlling_dependency_caching_programmatically)
- From command line: the `--refresh-dependencies` option tells Gradle to ignore all cached


