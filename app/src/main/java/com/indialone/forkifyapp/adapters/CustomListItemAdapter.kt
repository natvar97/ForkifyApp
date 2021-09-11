package com.indialone.forkifyapp.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.indialone.forkifyapp.fragments.ForkListFragment
import com.indialone.forkifyapp.databinding.ItemCustomListLayoutBinding
import javax.inject.Inject

class CustomListItemAdapter @Inject constructor() :
    RecyclerView.Adapter<CustomListItemAdapter.CustomListItemViewHolder>() {

    private var mFragment: Fragment = Fragment()
    private val itemsList = ArrayList<String>()

    class CustomListItemViewHolder(
        itemView: ItemCustomListLayoutBinding
    ) : RecyclerView.ViewHolder(itemView.root) {
        val tvText = itemView.tvText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomListItemViewHolder {
        val binding: ItemCustomListLayoutBinding = ItemCustomListLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomListItemViewHolder, position: Int) {
        holder.tvText.text = itemsList[position]

        holder.itemView.setOnClickListener {

            (mFragment as ForkListFragment).filterSelection(itemsList[position])

        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun addFragment(fragment: Fragment) {
        mFragment = fragment
    }

    fun addItems(list: List<String>) {
        itemsList.clear()
        itemsList.addAll(list)
        notifyDataSetChanged()
    }

}