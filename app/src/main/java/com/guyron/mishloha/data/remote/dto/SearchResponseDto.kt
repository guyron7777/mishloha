package com.guyron.mishloha.data.remote.dto

data class SearchResponseDto(
    val totalCount: Int,
    val items: List<RepositoryDto>
)
