package com.indialone.forkifyapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.indialone.forkifyapp.api.ApiService
import com.indialone.forkifyapp.model.search.RecipesItem
import retrofit2.HttpException
import java.io.IOException

private const val RECIPE_STARTING_PAGE_INDEX = 1

class RecipePagingSource(
    private val service: ApiService,
    private val query: String
) : PagingSource<Int, RecipesItem>() {
    override fun getRefreshKey(state: PagingState<Int, RecipesItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition = anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition = anchorPosition)?.prevKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipesItem> {
        val position = params.key ?: RECIPE_STARTING_PAGE_INDEX
        val apiQuery = query
        return try {
            val response = service.searchRecipes(apiQuery)
            val recipes = response.recipes
            val nextKey = if (recipes!!.isEmpty()) {
                null
            } else {
                position + (params.loadSize / 30)
            }
            LoadResult.Page(
                data = recipes,
                prevKey = if (position == RECIPE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}