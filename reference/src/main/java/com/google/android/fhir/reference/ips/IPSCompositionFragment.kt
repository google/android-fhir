package com.google.android.fhir.reference.ips

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.fhir.reference.databinding.IpsCompositionListItemBinding
import org.hl7.fhir.r4.model.Composition

class IPSListFragment : Fragment() {
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