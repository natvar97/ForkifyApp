package com.indialone.forkifyapp.model

import com.indialone.forkifyapp.model.search.RecipesItem
import java.lang.Exception

sealed class RecipesSearchResult {
    data class Success(val data: List<RecipesItem>): RecipesSearchResult()
    data class Error(val error: Exception): RecipesSearchResult()
}
