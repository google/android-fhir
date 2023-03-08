package com.google.android.fhir.demo.care.listscreening

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ScreeningsPagerAdapter(private val fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        val patientId = fragment.requireActivity().intent.getStringExtra("patientId")!!
        val listScreeningsFragment = ListScreeningsFragment().apply {
            arguments = bundleOf("position" to position, "patientId" to patientId)
        }
        return listScreeningsFragment
    }
}