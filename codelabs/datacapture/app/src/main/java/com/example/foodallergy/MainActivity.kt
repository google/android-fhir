package com.example.foodallergy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 4.1 Create the QuestionnaireFragment
        // val fragment = QuestionnaireFragment()

        // 4.2 Load the questionnaire JSON from asset file
        // val questionnaireJsonString =
        //     application.assets.open("Questionnaire-food-allergy-questionnaire.json")
        //         .bufferedReader()
        //         .use { it.readText() }

        // 4.3 Set the questionnaire for the fragment
        // fragment.arguments = bundleOf(
        //     QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJsonString
        // )

        // 4.4 Add the questionnaire to the activity
        // supportFragmentManager.commit {
        //     add(R.id.fragment_container_view, fragment)
        // }
    }
}