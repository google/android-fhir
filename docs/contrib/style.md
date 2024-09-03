# Code Style

## Spotless

We use Spotless to maintain the Java/Kotlin coding style in the codebase. Run the following command to check the codebase:

```sh
./gradlew spotlessCheck
```

and run the following command to apply fixes to the violations:

```sh
./gradlew spotlessApply
```

## License Headers

Spotless maintains the license headers for Kotlin files. Use addlicense to maintain license headers in other files:

```sh
addlicense -c "Google LLC" -l apache .
```

## Kotlin style

The codebase follows [google-java-format](https://github.com/google/google-java-format) instead of the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) because google-java-format is strict and deterministic, and therefore removes formatting as a concern for developers altogether.

If you would like Android Studio to help format your code, follow these steps to set up your Android Studio:

1. Install and configure the [ktfmt plugin](https://github.com/facebookincubator/ktfmt) in Android Studio by following these steps:
    1. Go to Android Studio's `Settings` (or `Preferences`), select the `Plugins` category, click the `Marketplace` tab, search for the `ktfmt` plugin, and click the `Install` button
    1. In Android Studio's `Settings` (or `Preferences`), go to `Editor` → `ktfmt Settings`, tick `Enable ktfmt`, change the `Code style` to `Google (Internal)`, and click `OK`
1. Indent 2 spaces. In Android Studio's `Settings` (or `Preferences`), go to `Editor` → `Code Style` → `Kotlin` → `Tabs and Indents`, set `Tab size`, `Indent` and `Continuation indent` to `2`, and click `OK`.
1. Use single name import sorted lexigraphically. In Android Studio's `Settings` (or `Preferences`), go to `Editor` → `Code Style` → `Kotlin` → `Imports`, in `Top-level Symbols` and `Java statics and Enum Members` sections select `Use single name import` option, remove all the rules in `Packages to Use Imports with '*'` and `Import Layout` sections and click `OK`.

Now you can go to `Code` → `Reformat code`, or press `Ctrl+Alt+L` (`⌘+⌥+L` for Mac) to automatically format code in Android Studio.

Note that you don't have to do any of these. You could rely on spotless to format any code you want to push. For details see below.

## XML style

We use [prettier](https://prettier.io/)'s [XML plugin](https://github.com/prettier/plugin-xml) to format the XML code. At the moment we have not discovered an Android Studio style configuration that would produce the same result. As a result, please run `./gradlew spotlessApply` to format the XML files.
