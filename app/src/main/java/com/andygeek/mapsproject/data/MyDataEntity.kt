package com.andygeek.mapsproject.data

data class MyDataEntity(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<Result>,
    val status: String
)