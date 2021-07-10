package com.indialone.forkifyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indialone.forkifyapp.R
import com.indialone.forkifyapp.databinding.RecipeItemLayoutBinding
import com.indialone.forkifyapp.model.search.RecipesItem

class RecipeItemAdapter(
    private val list: ArrayList<RecipesItem>
) : RecyclerView.Adapter<RecipeItemAdapter.RecipeItemViewHolder>() {
    class RecipeItemViewHolder(itemView: RecipeItemLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val tvTitle = itemView.tvTitle
        private val tvPublisher = itemView.tvPublisher
        private val tvSocialRank = itemView.tvSocialRank
        private val ivDish = itemView.ivDish
        val ivNext = itemView.ivNext

        fun bind(item: RecipesItem) {
            tvTitle.text = item.title
            tvPublisher.text = "Publisher: ${item.publisher}"
            tvSocialRank.text = "Social Rank: ${item.social_rank}"

            Glide.with(itemView.context)
                .load(item.image_url)
                .into(ivDish)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeItemViewHolder {
        val view =
            RecipeItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeItemViewHolder, position: Int) {
        holder.bind(list[position])
        holder.ivNext.setOnClickListener {
            val bundle = bundleOf(
                "recipeId" to list[position].recipe_id
            )

            it.findNavController()
                .navigate(R.id.action_forkListFragment_to_forkRecipeDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}