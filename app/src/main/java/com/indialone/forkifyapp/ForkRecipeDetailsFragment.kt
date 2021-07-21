package com.indialone.forkifyapp

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import com.bumptech.glide.Glide
import com.indialone.forkifyapp.databinding.FragmentForkRecipeDetailsBinding
import com.indialone.forkifyapp.viewmodel.RecipeViewModel
import com.indialone.forkifyapp.viewmodel.ViewModelFactory
import java.nio.file.Files.move

class ForkRecipeDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentForkRecipeDetailsBinding
    private lateinit var recipeViewModel: RecipeViewModel
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

        recipeViewModel =
            ViewModelProvider(this, ViewModelFactory()).get(RecipeViewModel::class.java)


        recipeId?.let {
            recipeViewModel.fetchRecipeDetails(recipeId!!)
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