package com.guyron.mishloha.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val owner: Owner,
    val stargazersCount: Int,
    val language: String?,
    val forksCount: Int,
    val createdAt: Date,
    val htmlUrl: String,
    val isFavorite: Boolean = false
) : Parcelable

@Parcelize
data class Owner(
    val id: Long,
    val login: String,
    val avatarUrl: String?
) : Parcelable
