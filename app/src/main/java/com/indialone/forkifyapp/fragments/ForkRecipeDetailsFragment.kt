package com.indialone.forkifyapp.fragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.indialone.forkifyapp.databinding.FragmentForkRecipeDetailsBinding
import com.indialone.forkifyapp.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForkRecipeDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentForkRecipeDetailsBinding

    @Inject
    lateinit var recipeViewModel: RecipeViewModel

    private var recipeId: String? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            recipeId = arguments?.getString("recipeId")!!
        }

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.slide_bottom
        )

        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentForkRecipeDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeId?.let {
            recipeViewModel.fetchRecipeDetails(it)
            recipeViewModel.getRecipeDetials().observe(viewLifecycleOwner) { response ->
                val recipe = response.recipe

                mBinding.tvTitle.text = recipe!!.title
                mBinding.tvPublisher.text = recipe.publisher
                mBinding.tvSocialRank.text = recipe.social_rank
                var ingredients = ""
                recipe.ingredients!!.forEach { ingredient ->
                    ingredients = "$ingredients-> $ingredient\n"
                }
                mBinding.tvIngredients.text = ingredients

                Glide.with(mBinding.root.context)
                    .load(recipe.image_url)
                    .into(mBinding.ivDish)

            }
        }


    }

}