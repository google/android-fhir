package com.google.android.fhir.reference.ips

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.fhir.reference.databinding.IpsCompositionListItemBinding
import com.google.android.fhir.reference.databinding.IpsCompositionListViewBinding
import org.hl7.fhir.r4.model.Composition
import java.util.*
import kotlin.collections.ArrayList

class IPSCompositionListFragment : Fragment() {
    private var _binding: IpsCompositionListViewBinding? = null
    private val binding
    get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = IpsCompositionListViewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val compositionList = ArrayList<Composition>()
        var obj = Composition()
        obj.setTitle("OBJECT1")
        obj.setDate(Date())

        var obj2 = Composition()
        obj2.setTitle("OBJECT2")
        obj2.setDate(Date())

        compositionList.add(obj)
        compositionList.add(obj2)

        val recyclerView = binding.ipsCompositionListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = IPSCompositionListRecyclerViewAdapter(compositionList,requireContext())



    }


}