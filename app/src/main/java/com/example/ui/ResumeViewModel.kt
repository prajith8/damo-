package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ResumeDatabase
import com.example.data.ResumeProfile
import com.example.data.ResumeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    Dashboard,
    Editor,
    Preview
}

class ResumeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ResumeRepository

    val allProfiles: StateFlow<List<ResumeProfile>>

    private val _currentScreen = MutableStateFlow(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _editingProfile = MutableStateFlow<ResumeProfile?>(null)
    val editingProfile: StateFlow<ResumeProfile?> = _editingProfile.asStateFlow()

    private val _editorStep = MutableStateFlow(0)
    val editorStep: StateFlow<Int> = _editorStep.asStateFlow()

    init {
        val database = ResumeDatabase.getDatabase(application)
        repository = ResumeRepository(database.resumeDao())
        allProfiles = repository.allProfiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun startNewProfile() {
        _editingProfile.value = ResumeProfile(
            profileName = "My Resume ${allProfiles.value.size + 1}"
        )
        _editorStep.value = 0
        _currentScreen.value = Screen.Editor
    }

    fun startEditing(profile: ResumeProfile) {
        _editingProfile.value = profile
        _editorStep.value = 0
        _currentScreen.value = Screen.Editor
    }

    fun startPreview(profile: ResumeProfile) {
        _editingProfile.value = profile
        _currentScreen.value = Screen.Preview
    }

    fun setEditorStep(step: Int) {
        _editorStep.value = step.coerceIn(0, 4)
    }

    fun updateProfile(update: (ResumeProfile) -> ResumeProfile) {
        _editingProfile.value?.let { current ->
            _editingProfile.value = update(current).copy(updatedAt = System.currentTimeMillis())
        }
    }

    fun saveCurrentProfile(onComplete: (() -> Unit)? = null) {
        val profile = _editingProfile.value ?: return
        viewModelScope.launch {
            if (profile.id == 0) {
                val newId = repository.insertProfile(profile)
                _editingProfile.value = profile.copy(id = newId.toInt())
            } else {
                repository.updateProfile(profile)
            }
            onComplete?.invoke()
        }
    }

    fun deleteProfile(profile: ResumeProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
            if (_editingProfile.value?.id == profile.id) {
                _editingProfile.value = null
                _currentScreen.value = Screen.Dashboard
            }
        }
    }

    fun changeTheme(theme: String) {
        updateProfile { it.copy(selectedTheme = theme) }
        saveCurrentProfile()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ResumeViewModel::class.java)) {
                return ResumeViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
