# Demo App

The *Workflow Demo* app demonstrates the capabilities of CarePlan Generation API and the Activity Flow API. The app has a Patient card in the top showing the details and a carousel in the bottom with each card representing a particular phase of the activity flow.

To run this app in Android Studio, [create a run/debug configuration](https://developer.android.com/studio/run/rundebugconfig) for the `workflow_demo` module using the [Android App](https://developer.android.com/studio/run/rundebugconfig#android-application) template and run the app using the configuration.

Alternatively, run the following command to build and install the debug APK on your device/emulator:

```shell
./gradlew :workflow_demo:installDebug
```

## Instructions
1. Click on the **Initialize** button to install the required dependencies for an activity flow. The dependencies are already bundled in the assets folder of the workflow demo app. After the dependencies are successfully installed, **Start** Button becomes enabled in the _Proposal_ card.
2. Now, click on the **Start** to generate a CarePlan which intern has a _Proposal_ Resource. This resource is then used by the app to create a new Activity Flow and the _Proposal_ card now shows the details of the resource with the **Start** button disabled now. The carousel auto moves to the next Phase Card i.e. _Plan_.
3. Repeat step 2 to move forward through the phases.
4. To restart the Activity click **Restart** Flow that removes all the resources related to the flow and moves the app back to step 2.
5. The overflow menu on the action bar may be used to switch between various Activities supported in the demo app.

![Workflow Demo](workflow_demo_app.gif)
