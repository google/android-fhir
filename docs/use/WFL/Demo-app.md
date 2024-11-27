# Demo App

To demonstrate the capabilities of the Activity Flow api, we have created the *Workflow Demo* app. The app has a Patient card in the top showing the details and a carousel in the bottom with each card representing a particular phase of the activity flow.

To run this app in Android Studio, [create a run/debug configuration](https://developer.android.com/studio/run/rundebugconfig) for the `workflow_demo` module using the [Android App](https://developer.android.com/studio/run/rundebugconfig#android-application) template and run the app using the configuration.

Alternatively, run the following command to build and install the debug APK on your device/emulator:

```shell
./gradlew :workflow_demo:installDebug
```

## Steps
1. Click on the Initialize button to install the required dependencies for an activity flow. The dependencies are already bundled in the assets folder of the workflow demo app. After the dependencies are successfully installed, Start Button becomes enabled in the Proposal card.
2. Now, click on the Start to generate a CarePlan which intern has a Proposal Resource. This resource is then used by the app to create a new Activity Flow and the Proposal card now shows the details of the resource with the Start button disabled now. The carousel auto moves to the next Phase Card i.e. Plan.
3. Follow the same step as above [2.] to move to the next phases.
4. To restart the Activity click Restart Flow that removes all the resources related to the flow and moves the app back to step [2.].
5. The overflow menu on the action bar may be used to switch between various Activities supported in the demo app.

[!Video](workflow_demo_app.webm)
