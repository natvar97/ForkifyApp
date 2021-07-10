package com.indialone.forkifyapp.repository

import androidx.annotation.WorkerThread
import com.indialone.forkifyapp.api.RetrofitBuilder

class RecipeRepository {
//
    @WorkerThread
    suspend fun searchRecipes(query: String) = RetrofitBuilder
        .apiService
        .searchRecipes(query)


    @WorkerThread
    suspend fun getRecipeDetails(rId : String) = RetrofitBuilder
        .apiService
        .getRecipeDetails(rId)

}