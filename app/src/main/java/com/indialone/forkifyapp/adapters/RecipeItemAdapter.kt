package com.indialone.forkifyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indialone.forkifyapp.R
import com.indialone.forkifyapp.databinding.RecipeItemLayoutBinding
import com.indialone.forkifyapp.model.search.RecipesItem
import javax.inject.Inject

class RecipeItemAdapter @Inject constructor() :
    RecyclerView.Adapter<RecipeItemAdapter.RecipeItemViewHolder>() {

    private val recipes = ArrayList<RecipesItem>()

    class RecipeItemViewHolder(itemView: RecipeItemLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val tvTitle = itemView.tvTitle
        val tvPublisher = itemView.tvPublisher
        val tvSocialRank = itemView.tvSocialRank
        val ivDish = itemView.ivDish
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
        holder.bind(recipes[position])
        holder.itemView.setOnClickListener {
            val bundle = bundleOf(
                "recipeId" to recipes[position].recipe_id
            )

            val extras = FragmentNavigatorExtras(
                holder.ivDish to "recipe_image",
                holder.tvTitle to "recipe_title",
                holder.tvPublisher to "recipe_publisher",
                holder.tvSocialRank to "recipe_rank"
            )

            it.findNavController()
                .navigate(
                    R.id.action_forkListFragment_to_forkRecipeDetailsFragment,
                    bundle,
                    null,
                    extras
                )
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun addData(list: List<RecipesItem>) {
        recipes.clear()
        recipes.addAll(list)
        notifyDataSetChanged()
    }

}