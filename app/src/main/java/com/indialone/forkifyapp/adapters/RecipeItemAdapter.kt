package com.indialone.forkifyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indialone.forkifyapp.R
import com.indialone.forkifyapp.databinding.RecipeItemLayoutBinding
import com.indialone.forkifyapp.model.search.RecipesItem
import com.indialone.forkifyapp.viewmodel.RecipeViewModel
import com.indialone.forkifyapp.viewmodel.UiModel

//    private val list: ArrayList<RecipesItem>

class RecipeItemAdapter :
    PagingDataAdapter<UiModel, RecipeItemAdapter.RecipeItemViewHolder>(UIMODEL_COMPARATOR) {
    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.RecipeItem && newItem is UiModel.RecipeItem &&
                        oldItem.recipesItem.title == newItem.recipesItem.title) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }


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
        val uiModel = getItem(position)
        uiModel?.let {
            when (uiModel) {
                is UiModel.RecipeItem ->{
                    (holder as RecipeItemViewHolder).bind(uiModel.recipesItem)
                    holder.itemView.setOnClickListener {
                        val bundle = bundleOf(
                            "recipeId" to uiModel.recipesItem.recipe_id
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
            }
        }
//        holder.bind(list[position])

    }

//    override fun getItemCount(): Int {
//        return list.size
//    }
}