package com.guyron.mishloha.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RepositoryDto(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val description: String?,
    val owner: OwnerDto,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    val language: String?,
    @SerializedName("forks_count")
    val forksCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("html_url")
    val htmlUrl: String
)

data class OwnerDto(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)
