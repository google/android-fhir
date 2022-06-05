1. Go to `build.gradle` file in the `app` module and add the SDC library and the AndroidX Fragment
   library dependencies and sync project
2. Go to `assets` directory under `src/main` and observe the sample questionnaire
3. Modify the layout file of the main activity `activity_main.xml` by adding
   a `FragmentContainerView` and removing the `TextView`. The container view is where the
   questionnaire will be displayed.
4. Go to `MainActivity.kt` to create a questionnaire fragment that uses the questionnaire JSON file
   to display the questionnaire.
5. Run the application.