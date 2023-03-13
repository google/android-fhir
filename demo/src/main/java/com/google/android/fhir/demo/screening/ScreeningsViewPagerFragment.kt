package com.google.android.fhir.demo.screening

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.fhir.demo.PatientDetailsFragmentArgs
import com.google.android.fhir.demo.PatientDetailsFragmentDirections
import com.google.android.fhir.demo.R
import com.google.android.fhir.demo.databinding.FragmentScreeningsViewPagerBinding
import com.google.android.fhir.demo.databinding.PatientDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.hl7.fhir.r4.model.Task

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScreeningsViewPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScreeningsViewPagerFragment : Fragment(), ScreeningClickHandler {
    private val viewModel: ScreeningsViewPagerViewModel by viewModels()
    private val args: ScreeningsViewPagerFragmentArgs by navArgs()
    private var _binding: FragmentScreeningsViewPagerBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screenings_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = ScreeningsViewPagerAdapter(this)
        TabLayoutMediator(binding.tasksTabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "PENDING"
                else -> "COMPLETED"
            }
        }.attach()
    }

    override fun onClick(task: Task) {
        viewModel.fetchQuestionnaireString(task).observe(viewLifecycleOwner) {
            findNavController()
                .navigate(
                    ScreeningsViewPagerFragmentDirections.actionPatientDetailsToScreenEncounterFragment(
                        args.patientId, it
                    )
                )
        }
    }
    class ScreeningsViewPagerAdapter(private val fragment: Fragment): FragmentStateAdapter(fragment) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            val listScreeningsFragment = ListScreeningsFragment((fragment as ScreeningClickHandler)).apply {
                arguments = bundleOf(
                    ListScreeningsFragment.TAB_POSITION to position,
                    ListScreeningsFragment.PATIENT_ID_KEY to (fragment as ScreeningsViewPagerFragment).args.patientId)
            }
            return listScreeningsFragment
        }
    }
}