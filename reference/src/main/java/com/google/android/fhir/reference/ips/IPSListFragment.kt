package com.google.android.fhir.reference.ips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.fhir.reference.R

class IPSListFragment: Fragment() {
    private var _binding: IPSListFragment? =null
    private val binding
    get() = _binding
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        //inflate for this fragment
        return inflater.inflate(R.layout.fragment_ips_list,container,false)
    }
}