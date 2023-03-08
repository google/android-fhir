package com.google.android.fhir.demo.care.listscreening


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.fhir.demo.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ScreeningsPagerFragment: Fragment() {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var screeningsPagerAdapter: ScreeningsPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screenings_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        screeningsPagerAdapter = ScreeningsPagerAdapter(this)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = screeningsPagerAdapter
        val tabLayout = view.findViewById(R.id.tasks_tab_layout) as TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if(position == 1) "COMPLETED" else "PENDING"
        }.attach()
    }
}