package com.example.data

import kotlinx.coroutines.flow.Flow

class ResumeRepository(private val resumeDao: ResumeDao) {
    val allProfiles: Flow<List<ResumeProfile>> = resumeDao.getAllProfiles()

    fun getProfileById(id: Int): Flow<ResumeProfile?> = resumeDao.getProfileById(id)

    suspend fun insertProfile(profile: ResumeProfile): Long = resumeDao.insertProfile(profile)

    suspend fun updateProfile(profile: ResumeProfile) = resumeDao.updateProfile(profile)

    suspend fun deleteProfile(profile: ResumeProfile) = resumeDao.deleteProfile(profile)

    suspend fun deleteProfileById(id: Int) = resumeDao.deleteProfileById(id)
}
