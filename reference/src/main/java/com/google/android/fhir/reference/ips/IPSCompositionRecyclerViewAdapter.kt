package com.google.android.fhir.reference.ips

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.hl7.fhir.r4.model.Composition

class IPSCompositionRecyclerViewAdapter(val ipsCompositionList: Array<Composition>) :
    RecyclerView.Adapter<IPSCompositionRecyclerViewAdapter.IPSCompositionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): IPSCompositionRecyclerViewAdapter.IPSCompositionViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: IPSCompositionRecyclerViewAdapter.IPSCompositionViewHolder,
        position: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}