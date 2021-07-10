package com.indialone.forkifyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.indialone.forkifyapp.repository.RecipeRepository
import java.lang.IllegalArgumentException

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(RecipeRepository()) as T
        }
        throw IllegalArgumentException("Unknown View Model Found")
    }
}