package com.google.fhirengine;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Gets FHIR Engine using Dagger component.
    FhirEngine fhirEngine = DaggerFhirEngineComponent.builder()
        .context(this).build().getFhirEngine();
  }
}
