package com.indialone.forkifyapp.repository

import androidx.annotation.WorkerThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.indialone.forkifyapp.api.ApiService
import com.indialone.forkifyapp.api.RetrofitBuilder
import com.indialone.forkifyapp.model.search.RecipesItem
import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val service: ApiService
) {
    //
    @WorkerThread
    fun searchRecipes(query: String): Flow<PagingData<RecipesItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RecipePagingSource(service = service, query = query)
            }
//                    RetrofitBuilder
//                    . apiService
//                    . searchRecipes (query)
        ).flow
    }


    @WorkerThread
    suspend fun getRecipeDetails(rId: String) = RetrofitBuilder
        .apiService
        .getRecipeDetails(rId)

}