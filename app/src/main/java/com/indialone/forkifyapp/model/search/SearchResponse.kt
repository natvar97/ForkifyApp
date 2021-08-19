package com.indialone.forkifyapp.model.search

data class SearchResponse(
    var recipes: List<RecipesItem>? = null,

    var count: Int? = null
)
