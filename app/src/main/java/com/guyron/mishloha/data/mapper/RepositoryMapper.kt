package com.guyron.mishloha.data.mapper


import com.guyron.mishloha.data.local.entity.RepositoryEntity
import com.guyron.mishloha.data.remote.dto.OwnerDto
import com.guyron.mishloha.data.remote.dto.RepositoryDto
import com.guyron.mishloha.domain.models.Owner
import com.guyron.mishloha.domain.models.Repository
import java.text.SimpleDateFormat
import java.util.*
import com.guyron.mishloha.data.Constants

fun RepositoryDto.toDomain(): Repository {
    val dateFormat = SimpleDateFormat(Constants.API_DATE_FORMAT, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone(Constants.UTC_TIMEZONE)
    
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description ?: Constants.DEFAULT_DESCRIPTION,
        owner = owner.toDomain(),
        stargazersCount = stargazersCount,
        language = language,
        forksCount = forksCount,
        createdAt = try {
            dateFormat.parse(createdAt) ?: Date()
        } catch (_: Exception) {
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
