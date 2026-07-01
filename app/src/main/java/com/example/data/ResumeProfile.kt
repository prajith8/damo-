package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resume_profiles")
data class ResumeProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileName: String = "My Resume",
    val photoUri: String? = null,
    val fullName: String = "",
    val gender: String = "",
    val customGender: String? = null,
    val fathersName: String = "",
    val mothersName: String = "",
    val email: String = "",
    val phone: String = "",
    val dob: String = "",
    val maritalStatus: String = "",
    val nationality: String = "",
    val religion: String = "",
    val bloodGroup: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val address: String = "",
    val careerObjective: String = "",
    val qualification: String = "",
    val experience: String = "",
    val languages: String = "",
    val skills: String = "",
    val declaration: String = "",
    val selectedTheme: String = "blue", // blue, black, gold, green
    val updatedAt: Long = System.currentTimeMillis()
)
