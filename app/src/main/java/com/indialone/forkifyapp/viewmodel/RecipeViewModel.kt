package com.indialone.forkifyapp.viewmodel

import androidx.lifecycle.*
import com.indialone.forkifyapp.model.details.RecipeResponse
import com.indialone.forkifyapp.model.search.SearchResponse
import com.indialone.forkifyapp.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private var searchRecipes = MutableLiveData<SearchResponse>()
    private var recipeDetails = MutableLiveData<RecipeResponse>()

    init {
        fetchSearchRecipes("")
        fetchRecipeDetails("")
    }

    fun fetchRecipeDetails(rId: String) {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val details = async {
                        repository.getRecipeDetails(rId)
                    }
                    recipeDetails.postValue(details.await())
                }
            } catch (e: Exception) {
            }
        }
    }

    fun fetchSearchRecipes(query : String) {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val recipes = async {
                        repository.searchRecipes(query)
                    }
                    searchRecipes.postValue(recipes.await())
                }
            } catch (e: Exception) {
            }
        }
    }

    fun getSearchRecipes(): LiveData<SearchResponse> = searchRecipes
    fun getRecipeDetials(): LiveData<RecipeResponse> = recipeDetails

}