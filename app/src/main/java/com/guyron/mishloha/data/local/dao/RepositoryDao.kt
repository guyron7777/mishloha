package com.guyron.mishloha.data.local.dao

import androidx.room.*
import com.guyron.mishloha.data.local.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RepositoryDao {
    @Query("SELECT * FROM repositories WHERE isFavorite = 1 ORDER BY addedToFavoritesAt DESC")
    fun getFavoriteRepositories(): Flow<List<RepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE isFavorite = 1 AND (name LIKE '%' || :query || '%' OR fullName LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY addedToFavoritesAt DESC")
    suspend fun searchFavorites(query: String): List<RepositoryEntity>

    @Query("SELECT isFavorite FROM repositories WHERE id = :repositoryId")
    suspend fun isFavorite(repositoryId: Long): Boolean?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repository: RepositoryEntity)

    @Update
    suspend fun updateRepository(repository: RepositoryEntity)

    @Query("UPDATE repositories SET isFavorite = 1, addedToFavoritesAt = :timestamp WHERE id = :repositoryId")
    suspend fun addToFavorites(repositoryId: Long, timestamp: Date)

    @Query("UPDATE repositories SET isFavorite = 0, addedToFavoritesAt = NULL WHERE id = :repositoryId")
    suspend fun removeFromFavorites(repositoryId: Long)

    @Query("DELETE FROM repositories WHERE id = :repositoryId")
    suspend fun deleteRepository(repositoryId: Long)

    @Query("DELETE FROM repositories WHERE isFavorite = 0")
    suspend fun clearNonFavorites()
}
