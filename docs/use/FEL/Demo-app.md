# Demo App

To demonstrate the capabilities of the FHIR Engine Library, we have created the *FHIR Engine Demo* app.

To run this app in Android Studio, [create a run/debug configuration](https://developer.android.com/studio/run/rundebugconfig) for the `demo` module using the [Android App](https://developer.android.com/studio/run/rundebugconfig#android-application) template and run the app using the configuration.

Alternatively, run the following command to build and install the debug APK on your device/emulator:

```shell
./gradlew :demo:installDebug
```

The Demo app uses the [HAPI FHIR demo server](https://hapi.fhir.org/baseR4/) as the server to sync to and all data is visible to anyone on the internet. Do not use real data, or any data you would not want publicly visible.

The app filters for patients with `Patient.address.city = NAIROBI`. If creating a new patient in the app, set the **City** field to `NAIROBI` for it to show up in the **Registered Patients** list.
