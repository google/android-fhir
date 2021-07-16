package com.google.android.fhir.reference.ips

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.R
import org.hl7.fhir.r4.model.Composition

class IPSCompositionListRecyclerViewAdapter(val ipsCompositionList: ArrayList<Composition>, val context: Context) :
    RecyclerView.Adapter<IPSCompositionListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): IPSCompositionListViewHolder {
        return IPSCompositionListViewHolder(LayoutInflater.from(context).inflate(R.layout.ips_composition_list_item,parent,false))
    }

    override fun onBindViewHolder(
        holder: IPSCompositionListViewHolder,
        position: Int,
    ) {
        val toShow = ipsCompositionList.get(position)
        holder.title.text  = toShow.title
        holder.description.text = toShow.date.toString()
    }

    override fun getItemCount(): Int {
        return ipsCompositionList.size
    }
}