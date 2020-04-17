package com.google.fhirengine.example;

import android.annotation.SuppressLint;
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

import org.hl7.fhir.r4.model.Library;
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
  public static final String LIBRARY_ID = "Library/library-ANCDummy";

  FhirEngine fhirEngine;
  EditText cqlLibraryUrlInput;
  TextView evaluationResultTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cqlLibraryUrlInput = findViewById(R.id.cql_text_input);
    evaluationResultTextView = findViewById(R.id.evaluate_result);

    final Button button = findViewById(R.id.load_cql_lib_button);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        new DownloadAncLibrary().execute(cqlLibraryUrlInput.getText().toString());
      }
    });

    final Button evaluateButton = findViewById(R.id.evaluate_button);
    evaluateButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        new EvaluateAncLibrary().execute(LIBRARY_ID);
      }
    });


    // Gets FHIR Engine using Dagger component.
    fhirEngine = DaggerFhirEngineComponent.builder()
        .context(this).build().getFhirEngine();
  }

  private class DownloadAncLibrary extends AsyncTask<String, String, Void> {
    @Override
    protected Void doInBackground(String... strings) {
      String result = "";
      InputStream stream = null;
      try {
        stream = (InputStream) new URL(strings[0]).getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        while (line != null) {
          result += line;
          line = reader.readLine();
        }

        FhirContext fhirContext = FhirContext.forR4();
        Library library = (Library) fhirContext.newJsonParser().parseResource(result);
        fhirEngine.save(library);
        Snackbar.make(cqlLibraryUrlInput, "Library loaded " + library.getId(), Snackbar.LENGTH_SHORT)
            .show();
      } catch (IOException e) {
        e.printStackTrace();
        Snackbar.make(cqlLibraryUrlInput, "Something went wrong...", Snackbar.LENGTH_SHORT).show();
      } catch (ResourceAlreadyExistsException e) {
        e.printStackTrace();
        Snackbar.make(cqlLibraryUrlInput, "The library was already loaded...", Snackbar.LENGTH_SHORT)
            .show();
      }
      return null;
    }
  }

  private class EvaluateAncLibrary extends AsyncTask<String, String, EvaluationResult> {
    @SuppressLint("WrongThread")
    @Override
    protected EvaluationResult doInBackground(String... strings) {
      return fhirEngine.evaluateCql("ANCFHIRDummy");
    }

    @Override
    protected void onPostExecute(EvaluationResult result) {
      StringBuilder stringBuilder = new StringBuilder();
      for (LibraryResult libraryResult : result.libraryResults.values()) {
        for (Map.Entry<String, Object> entry : libraryResult.expressionResults.entrySet()) {
          stringBuilder.append(entry.getKey());
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
          }
        }
      }
      evaluationResultTextView.setText(stringBuilder.toString());
    }
  }
}
