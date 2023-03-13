package com.google.android.fhir.demo.screening

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.demo.R

class ListScreeningsFragment(screeningClickHandler: ScreeningClickHandler): Fragment(R.layout.list_screenings) {
    private val viewModel by viewModels<ListScreeningsViewModel>()
    private val screeningLandingAdapter = ScreeningLandingAdapter(screeningClickHandler)
    private lateinit var args: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = requireArguments()
        viewModel.getTasksForPatient(
            args.getString(PATIENT_ID_KEY)!!,
            args.getInt(TAB_POSITION)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val recyclerView = view?.findViewById(R.id.listScreenings) as RecyclerView

        observeAndSetItemList()

        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        recyclerView.adapter = screeningLandingAdapter

        return view
    }

    private fun observeAndSetItemList() {
        viewModel.taskList.observe(viewLifecycleOwner) { screeningLandingAdapter.setItemsList(it) }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    if (data.getBooleanExtra(ScreenerFragment.TASK_STATUS_MODIFIED, false)) {
                        viewModel.setUpdatedTask()
                    }
                }
            }
        }

    companion object{
        const val PATIENT_ID_KEY = "patient_id"
        const val TAB_POSITION = "tab_position"
    }
}