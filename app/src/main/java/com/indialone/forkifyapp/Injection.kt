package com.indialone.forkifyapp

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.indialone.forkifyapp.api.ApiService
import com.indialone.forkifyapp.api.RetrofitBuilder
import com.indialone.forkifyapp.repository.RecipeRepository
import com.indialone.forkifyapp.viewmodel.ViewModelFactory

object Injection {
    private fun provideRecipesRepository(context: Context): RecipeRepository {
        return RecipeRepository(RetrofitBuilder.apiService)
    }

    fun provideViewModelFactory(
        context: Context,
        owner: SavedStateRegistryOwner
    ): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideRecipesRepository(context))
    }

}