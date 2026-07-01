package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resume_profiles ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<ResumeProfile>>

    @Query("SELECT * FROM resume_profiles WHERE id = :id")
    fun getProfileById(id: Int): Flow<ResumeProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ResumeProfile): Long

    @Update
    suspend fun updateProfile(profile: ResumeProfile)

    @Delete
    suspend fun deleteProfile(profile: ResumeProfile)

    @Query("DELETE FROM resume_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Int)
}
