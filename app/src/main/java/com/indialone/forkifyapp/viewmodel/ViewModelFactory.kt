package com.indialone.forkifyapp.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.indialone.forkifyapp.repository.RecipeRepository
import java.lang.IllegalArgumentException

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: RecipeRepository
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(repository = repository, handle) as T
        }
        throw IllegalArgumentException("Unknown View Model Found")
    }
}