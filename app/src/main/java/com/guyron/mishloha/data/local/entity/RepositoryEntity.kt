package com.guyron.mishloha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val ownerId: Long,
    val ownerLogin: String,
    val ownerAvatarUrl: String?,
    val stargazersCount: Int,
    val language: String?,
    val forksCount: Int,
    val createdAt: Date,
    val htmlUrl: String,
    val isFavorite: Boolean = false,
    val addedToFavoritesAt: Date? = null
)
