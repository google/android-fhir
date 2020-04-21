// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.example;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.fhirengine.DaggerFhirEngineComponent;
import com.google.fhirengine.FhirEngine;
import com.google.fhirengine.ResourceAlreadyExistsException;

import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.execution.EvaluationResult;
import org.opencds.cqf.cql.execution.LibraryResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.context.FhirContext;

public class MainActivity extends AppCompatActivity {
  FhirEngine fhirEngine;
  EditText cqlLibraryUrlInput;
  EditText fhirResourceUrlInput;
  EditText libraryInput;
  EditText contextInput;
  EditText expressionInput;
  EditText evaluationUrlInput;
  TextView evaluationResultTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cqlLibraryUrlInput = findViewById(R.id.cql_text_input);
    fhirResourceUrlInput = findViewById(R.id.fhir_resource_url_input);
    libraryInput = findViewById(R.id.library_input);
    contextInput = findViewById(R.id.context_input);
    expressionInput = findViewById(R.id.expression_input);
    evaluationResultTextView = findViewById(R.id.evaluate_result);

    final Button button = findViewById(R.id.load_cql_lib_button);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        new DownloadFhirResource().execute(cqlLibraryUrlInput.getText().toString());
      }
    });

    final Button downloadFhirResourceButton = findViewById(R.id.download_fhir_resource_button);
    downloadFhirResourceButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        new DownloadFhirResource().execute(fhirResourceUrlInput.getText().toString());
      }
    });

    final Button evaluateButton = findViewById(R.id.evaluate_button);
    evaluateButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        new EvaluateAncLibrary().execute(
            new String[]{libraryInput.getText().toString(), contextInput.getText().toString(),
                expressionInput.getText().toString()});
      }
    });


    // Gets FHIR Engine using Dagger component.
    fhirEngine = DaggerFhirEngineComponent.builder()
        .context(this).build().getFhirEngine();
  }

  private class DownloadFhirResource extends AsyncTask<String, String, Void> {
    @Override
    protected Void doInBackground(String... strings) {
      String result = "";
      InputStream stream = null;
      Resource resource = null;
      try {
        stream = (InputStream) new URL(strings[0]).getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        while (line != null) {
          result += line;
          line = reader.readLine();
        }

        FhirContext fhirContext = FhirContext.forR4();
        resource = (Resource) fhirContext.newJsonParser().parseResource(result);
        fhirEngine.save(resource);
        Snackbar.make(cqlLibraryUrlInput,
            "Loaded " + resource.getResourceType().name() + " with ID " + resource.getId(),
            Snackbar.LENGTH_SHORT)
            .show();
      } catch (IOException e) {
        e.printStackTrace();
        Snackbar.make(cqlLibraryUrlInput, "Something went wrong...", Snackbar.LENGTH_SHORT).show();
      } catch (ResourceAlreadyExistsException e) {
        e.printStackTrace();
        if (resource != null) {
          Snackbar.make(cqlLibraryUrlInput,
              "The " + resource.getResourceType().name() + " with ID " + resource.getId() +
                  " was already loaded...", Snackbar.LENGTH_SHORT)
              .show();
        }
      }
      return null;
    }
  }

  private class EvaluateAncLibrary extends AsyncTask<String, String, EvaluationResult> {
    @Override
    protected EvaluationResult doInBackground(String... strings) {
      return fhirEngine.evaluateCql(strings[0], strings[1], strings[2]);
    }


    @Override
    protected void onPostExecute(EvaluationResult result) {
      StringBuilder stringBuilder = new StringBuilder();
      for (LibraryResult libraryResult : result.libraryResults.values()) {
        for (Map.Entry<String, Object> entry : libraryResult.expressionResults.entrySet()) {
          stringBuilder.append(entry.getKey() + " -> ");
          Object value = entry.getValue();
          if (value == null) {
            stringBuilder.append("null");
          } else if (List.class.isAssignableFrom(value.getClass())) {
            for (Object listItem : (List) value) {
              stringBuilder
                  .append(FhirContext.forR4().newJsonParser()
                      .encodeResourceToString((Resource) listItem));
            }
          } else if (Resource.class.isAssignableFrom(value.getClass())) {
            stringBuilder
                .append(FhirContext.forR4().newJsonParser()
                    .encodeResourceToString((Resource) value));
          } else {
            stringBuilder.append(value.toString());
          }
        }
      }
      evaluationResultTextView.setText(stringBuilder.toString());
    }
  }
}
