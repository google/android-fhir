package com.google.fhirengine.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.fhirengine.DaggerFhirEngineComponent;
import com.google.fhirengine.FhirEngine;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets FHIR Engine using Dagger component.
        FhirEngine fhirEngine = DaggerFhirEngineComponent.builder()
            .context(this).build().getFhirEngine();
    }
}
