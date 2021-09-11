package com.indialone.forkifyapp.repository

import androidx.annotation.WorkerThread
import com.indialone.forkifyapp.api.ApiService
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val apiService: ApiService) {
    //
    @WorkerThread
    suspend fun searchRecipes(query: String) = apiService.searchRecipes(query)


    @WorkerThread
    suspend fun getRecipeDetails(rId: String) = apiService.getRecipeDetails(rId)

}