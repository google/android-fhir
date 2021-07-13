package com.google.android.fhir.reference.ips

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.fhir.reference.R
import com.google.android.fhir.reference.databinding.FragmentIpsListBinding
import com.google.android.fhir.reference.databinding.FragmentPatientListBinding

class IPSListFragment : Fragment() {
    private var _binding: FragmentIpsListBinding? = null
    private val binding
    get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        //inflate for this fragment
        _binding = FragmentIpsListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}