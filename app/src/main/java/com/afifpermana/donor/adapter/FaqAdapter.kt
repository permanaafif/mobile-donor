package com.afifpermana.donor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Faq

class FaqAdapter(private val itemsList : List<Faq>) :
    RecyclerView.Adapter<FaqAdapter.ViewHolder>(){


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val faqTitle : TextView  = itemView.findViewById(R.id.faqtitle)
        val faqDesc  : TextView  = itemView.findViewById(R.id.faqdesc)
        val faqImg   : ImageView = itemView.findViewById(R.id.faqimage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_faq, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemsList[position]

        holder.faqTitle.text = currentItem.title
        holder.faqDesc.text = currentItem.desc

        holder.faqImg.setOnClickListener{
            if (holder.faqDesc.visibility == View.GONE){
                holder.faqDesc.visibility = View.VISIBLE
                holder.faqImg.setImageResource(R.drawable.baseline_horizontal_rule_24)
            }else{
                holder.faqDesc.visibility = View.GONE
                holder.faqImg.setImageResource(R.drawable.baseline_add_24)
            }
        }
    }
}