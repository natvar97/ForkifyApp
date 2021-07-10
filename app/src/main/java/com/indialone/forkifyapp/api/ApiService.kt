package com.indialone.forkifyapp.api

import com.indialone.forkifyapp.model.details.RecipeResponse
import com.indialone.forkifyapp.model.search.SearchResponse
import com.indialone.forkifyapp.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(Constants.SEARCH_END_POINT)
    suspend fun searchRecipes(@Query("q") q : String) : SearchResponse

    @GET(Constants.GET_END_POINT)
    suspend fun getRecipeDetails(@Query("rId") rId : String) : RecipeResponse

}