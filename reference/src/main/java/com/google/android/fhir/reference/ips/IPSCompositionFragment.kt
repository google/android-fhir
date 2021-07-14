package com.google.android.fhir.reference.ips

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.fhir.reference.R
import com.google.android.fhir.reference.databinding.IpsCompositionListItemBinding
import com.google.android.fhir.reference.databinding.FragmentPatientListBinding

class IPSListFragment : Fragment() {
    //dummy data movies of nicolas cage
    private val mNicolasCageMovies = listOf(
        Movie("Raising Arizona", 1987),
        Movie("Vampire's Kiss", 1988),
        Movie("Con Air", 1997),
        Movie("Gone in 60 Seconds", 1997),
        Movie("National Treasure", 2004),
        Movie("The Wicker Man", 2006),
        Movie("Ghost Rider", 2007),
        Movie("Knowing", 2009))
    private var _binding: IpsCompositionListItemBinding? = null
    private val binding
    get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflate for this fragment
        _binding = IpsCompositionListItemBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}