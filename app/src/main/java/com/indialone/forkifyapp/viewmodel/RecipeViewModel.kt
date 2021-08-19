package com.indialone.forkifyapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.indialone.forkifyapp.model.details.RecipeResponse
import com.indialone.forkifyapp.model.search.RecipesItem
import com.indialone.forkifyapp.model.search.SearchResponse
import com.indialone.forkifyapp.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val repository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<UiState>
    private var searchRecipes = MutableLiveData<SearchResponse>()
    private var recipeDetails = MutableLiveData<RecipeResponse>()

    init {
        fetchSearchRecipes("")
        fetchRecipeDetails("")

        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        state = searches
            .flatMapLatest { search ->
                combine(
                    queriesScrolled,
                    fetchSearchRecipes(query = search.query),
                    ::Pair
                )
                    // Each unique PagingData should be submitted once, take the latest from
                    // queriesScrolled
                    .distinctUntilChangedBy { it.second }
                    .map { (scroll, pagingData) ->
                        UiState(
                            query = search.query,
                            pagingData = pagingData as PagingData<RecipesItem>,
                            lastQueryScrolled = scroll.currentQuery,
                            // If the search query matches the scroll query, the user has scrolled
                            hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )
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

    fun fetchSearchRecipes(query: String): Flow<PagingData<UiModel>> {
        var currentSearchResult: Flow<PagingData<UiModel>>? = null
        val lastResult: Flow<PagingData<UiModel>>? = currentSearchResult
        var currentQueryValue: String? = null
        if (query == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = query
        val newResult = repository.searchRecipes(query = query)
            .map { pagingData -> pagingData.map { UiModel.RecipeItem(it) } }
            .map {
                it.insertSeparators<UiModel.RecipeItem, UiModel>{ before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                    }

                    if (before.roundedStarCount > after.roundedStarCount ) {
                        if (after.roundedStarCount >= 1) {
                            UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        } else {
                            UiModel.SeparatorItem("< 10.000+ stars")
                        }
                    } else {
                        null
                    }

                }
            }.cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
//        viewModelScope.launch {
//            try {
//                coroutineScope {
//                    val recipes = async {
//                        repository.searchRecipes(query)
//                    }
//                    searchRecipes.postValue(recipes.await())
//                }
//            } catch (e: Exception) {
//            }
//        }
    }

    fun getSearchRecipes(): LiveData<SearchResponse> = searchRecipes
    fun getRecipeDetials(): LiveData<RecipeResponse> = recipeDetails

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

}

sealed class UiModel {
    data class RecipeItem(val recipesItem: RecipesItem) : UiModel()
    data class SeparatorItem(val description: String) : UiModel()
}

private val UiModel.RecipeItem.roundedStarCount: Int
    get() = this.recipesItem.social_rank!!.toInt()/ 10_000


sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(
        val currentQuery: String
//        val visibleItemCount: Int,
//        val lastVisibleItemPosition: Int,
//        val totalItemCount: Int
    ) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val pagingData: PagingData<RecipesItem> = PagingData.empty()
)

private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Android"
private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"