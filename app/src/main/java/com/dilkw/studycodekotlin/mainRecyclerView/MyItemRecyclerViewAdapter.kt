package com.dilkw.studycodekotlin.mainRecyclerView

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dilkw.studycodekotlin.ItemEnum
import com.dilkw.studycodekotlin.databinding.FragmentItemBinding

class MyItemRecyclerViewAdapter(
    private val values: List<com.dilkw.studycodekotlin.ItemEnum>,
    private val itemClickListener: ItemClickListener?
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.contentView.text = item.itemName
        holder.contentView.setOnClickListener {
            itemClickListener?.itemOnClick(values[position])
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    interface ItemClickListener {
        fun itemOnClick(itemEnum: com.dilkw.studycodekotlin.ItemEnum)
    }

}