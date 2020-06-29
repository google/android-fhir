package com.google.fhirengine.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.fhirengine.example.dummy.DummyContent

/**
 * A fragment representing a single SamplePatient detail screen.
 * This fragment is either contained in a [SamplePatientListActivity]
 * in two-pane mode (on tablets) or a [SamplePatientDetailActivity]
 * on handsets.
 */
class SamplePatientDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: DummyContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = item
                        ?.content
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.samplepatient_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.findViewById<TextView>(R.id.samplepatient_detail).text = it.details
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}