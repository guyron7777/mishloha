package com.guyron.mishloha.data.mapper


import com.guyron.mishloha.data.local.entity.RepositoryEntity
import com.guyron.mishloha.data.remote.dto.OwnerDto
import com.guyron.mishloha.data.remote.dto.RepositoryDto
import com.guyron.mishloha.domain.models.Owner
import com.guyron.mishloha.domain.models.Repository
import java.text.SimpleDateFormat
import java.util.*

fun RepositoryDto.toDomain(): Repository {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description ?: "No description available",
        owner = owner.toDomain(),
        stargazersCount = stargazersCount,
        language = language,
        forksCount = forksCount,
        createdAt = try {
            dateFormat.parse(createdAt) ?: Date()
        } catch (e: Exception) {
            Date()
        },
        htmlUrl = htmlUrl
    )
}

fun OwnerDto.toDomain(): Owner {
    return Owner(
        id = id,
        login = login,
        avatarUrl = avatarUrl
    )
}

fun Repository.toEntity(): RepositoryEntity {
    return RepositoryEntity(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        ownerId = owner.id,
        ownerLogin = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        stargazersCount = stargazersCount,
        language = language,
        forksCount = forksCount,
        createdAt = createdAt,
        htmlUrl = htmlUrl,
        isFavorite = isFavorite
    )
}

fun RepositoryEntity.toDomain(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        owner = Owner(
            id = ownerId,
            login = ownerLogin,
            avatarUrl = ownerAvatarUrl
        ),
        stargazersCount = stargazersCount,
        language = language,
        forksCount = forksCount,
        createdAt = createdAt,
        htmlUrl = htmlUrl,
        isFavorite = isFavorite
    )
}
