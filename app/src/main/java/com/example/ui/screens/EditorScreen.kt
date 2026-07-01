package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.ResumeProfile
import com.example.ui.ResumeViewModel
import com.example.ui.Screen
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: ResumeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.editingProfile.collectAsStateWithLifecycle()
    val step by viewModel.editorStep.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Save state helper
    LaunchedEffect(step) {
        scrollState.animateScrollTo(0)
    }

    if (profile == null) {
        onBack()
        return
    }

    val activeProfile = profile!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Resume Info", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Save and go back
                        viewModel.saveCurrentProfile { onBack() }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentProfile {
                                viewModel.navigateTo(Screen.Preview)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = "Live Preview", tint = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(
                        onClick = {
                            viewModel.saveCurrentProfile {
                                onBack()
                            }
                        }
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    if (step > 0) {
                        OutlinedButton(
                            onClick = { viewModel.setEditorStep(step - 1) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.saveCurrentProfile { onBack() } },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text("Cancel")
                        }
                    }

                    // Step Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..4) {
                            Box(
                                modifier = Modifier
                                    .size(if (i == step) 10.dp else 6.dp)
                                    .background(
                                        color = if (i == step) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    // Next/Save button
                    if (step < 4) {
                        Button(
                            onClick = { viewModel.setEditorStep(step + 1) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.saveCurrentProfile {
                                    viewModel.navigateTo(Screen.Preview)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Finish & Preview")
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Screen Header indicating steps
            val stepTitle = when (step) {
                0 -> "Step 1: Basic Information"
                1 -> "Step 2: Personal & Contact Details"
                2 -> "Step 3: Address & Career Objective"
                3 -> "Step 4: Qualifications & Experience"
                4 -> "Step 5: Themes, Languages & Skills"
                else -> ""
            }

            Text(
                text = stepTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            when (step) {
                0 -> StepBasicInfo(activeProfile, viewModel, context)
                1 -> StepContactInfo(activeProfile, viewModel)
                2 -> StepAddressObjective(activeProfile, viewModel)
                3 -> StepQualExperience(activeProfile, viewModel)
                4 -> StepThemeSkills(activeProfile, viewModel)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Save photo to internal cache helper so it retains permissions permanently
fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun StepBasicInfo(
    profile: ResumeProfile,
    viewModel: ResumeViewModel,
    context: Context
) {
    var genderExpanded by remember { mutableStateOf(false) }
    var maritalExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val localPath = copyUriToInternalStorage(context, uri)
            if (localPath != null) {
                viewModel.updateProfile { it.copy(photoUri = localPath) }
            } else {
                viewModel.updateProfile { it.copy(photoUri = uri.toString()) }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Basic Info", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            // Photo picker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                if (!profile.photoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = profile.photoUri,
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Add Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(if (profile.photoUri.isNullOrEmpty()) "Upload Photo" else "Change Photo", fontSize = 13.sp)
                    }
                    if (!profile.photoUri.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = { viewModel.updateProfile { it.copy(photoUri = null) } }
                        ) {
                            Text("Remove Photo", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Resume Label
            OutlinedTextField(
                value = profile.profileName,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(profileName = newValue) } },
                label = { Text("Profile/Resume Name") },
                placeholder = { Text("e.g., Software Engineer Resume") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Full Name
            OutlinedTextField(
                value = profile.fullName,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(fullName = newValue) } },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Gender select
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = profile.gender,
                    onValueChange = {},
                    label = { Text("Gender") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { genderExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { genderExpanded = true }
                )
                DropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    val genders = listOf("Male", "Female", "Transgender", "Non-Binary", "Prefer Not to Say", "Other")
                    genders.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateProfile { it.copy(gender = option) }
                                genderExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Custom Gender (if Other)
            AnimatedVisibility(visible = profile.gender == "Other") {
                OutlinedTextField(
                    value = profile.customGender ?: "",
                    onValueChange = { newValue -> viewModel.updateProfile { it.copy(customGender = newValue) } },
                    label = { Text("Enter custom gender") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true
                )
            }

            // Father's name
            OutlinedTextField(
                value = profile.fathersName,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(fathersName = newValue) } },
                label = { Text("Father's Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Mother's name
            OutlinedTextField(
                value = profile.mothersName,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(mothersName = newValue) } },
                label = { Text("Mother's Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Date of birth
            OutlinedTextField(
                value = profile.dob,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(dob = newValue) } },
                label = { Text("Date of Birth") },
                placeholder = { Text("YYYY-MM-DD or DD/MM/YYYY") },
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Marital status select
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = profile.maritalStatus,
                    onValueChange = {},
                    label = { Text("Marital Status") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { maritalExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { maritalExpanded = true }
                )
                DropdownMenu(
                    expanded = maritalExpanded,
                    onDismissRequest = { maritalExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    val statuses = listOf("Single", "Married", "Divorced")
                    statuses.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateProfile { it.copy(maritalStatus = option) }
                                maritalExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepContactInfo(
    profile: ResumeProfile,
    viewModel: ResumeViewModel
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Personal & Contact Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = profile.email,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(email = newValue) } },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Phone
            OutlinedTextField(
                value = profile.phone,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(phone = newValue) } },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Nationality
            OutlinedTextField(
                value = profile.nationality,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(nationality = newValue) } },
                label = { Text("Nationality") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Religion
            OutlinedTextField(
                value = profile.religion,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(religion = newValue) } },
                label = { Text("Religion") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Blood Group
            OutlinedTextField(
                value = profile.bloodGroup,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(bloodGroup = newValue) } },
                label = { Text("Blood Group") },
                placeholder = { Text("e.g., O+ve, A-ve") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun StepAddressObjective(
    profile: ResumeProfile,
    viewModel: ResumeViewModel
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Address & Objective", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            // House/Street
            OutlinedTextField(
                value = profile.street,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(street = newValue) } },
                label = { Text("House / Street") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // City
            OutlinedTextField(
                value = profile.city,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(city = newValue) } },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // State
            OutlinedTextField(
                value = profile.state,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(state = newValue) } },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Pincode
            OutlinedTextField(
                value = profile.pincode,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(pincode = newValue) } },
                label = { Text("Pincode / Zipcode") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Full Address
            OutlinedTextField(
                value = profile.address,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(address = newValue) } },
                label = { Text("Full Permanent Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Career Objective
            OutlinedTextField(
                value = profile.careerObjective,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(careerObjective = newValue) } },
                label = { Text("Career Objective") },
                placeholder = { Text("To secure a challenging role where I can utilize my qualifications...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}

@Composable
fun StepQualExperience(
    profile: ResumeProfile,
    viewModel: ResumeViewModel
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Qualifications & Experience", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Tip: Write each item on a new line (press Enter) to automatically format them as premium bullet points in the preview.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Qualifications
            OutlinedTextField(
                value = profile.qualification,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(qualification = newValue) } },
                label = { Text("Qualifications") },
                placeholder = { Text("Bachelor of Computer Applications\nPlus Two (Science)\nSSLC") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6,
                maxLines = 10
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Experience
            OutlinedTextField(
                value = profile.experience,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(experience = newValue) } },
                label = { Text("Work Experience") },
                placeholder = { Text("Senior Developer – ABC Tech – 3 Years\nJunior Web Designer – XYZ Corp – 1 Year") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6,
                maxLines = 10
            )
        }
    }
}

@Composable
fun StepThemeSkills(
    profile: ResumeProfile,
    viewModel: ResumeViewModel
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Skills, Languages & Theme", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            // Theme selection
            Text("Active Theme Accent", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val themes = listOf(
                    Triple("blue", Color(0xFF0057FF), "Blue"),
                    Triple("black", Color(0xFF111111), "Black"),
                    Triple("gold", Color(0xFFC49B00), "Gold"),
                    Triple("green", Color(0xFF009944), "Green")
                )

                themes.forEach { (id, color, name) ->
                    val isSelected = profile.selectedTheme.lowercase() == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) color.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) color else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.changeTheme(id) }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(color, CircleShape)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) color else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Skills
            OutlinedTextField(
                value = profile.skills,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(skills = newValue) } },
                label = { Text("Skills") },
                placeholder = { Text("HTML, CSS, JavaScript, Kotlin, Jetpack Compose, UI Design") },
                supportingText = { Text("Enter comma-separated items", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Languages
            OutlinedTextField(
                value = profile.languages,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(languages = newValue) } },
                label = { Text("Languages") },
                placeholder = { Text("English, Malayalam, Hindi") },
                supportingText = { Text("Enter comma-separated items", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Declaration
            OutlinedTextField(
                value = profile.declaration,
                onValueChange = { newValue -> viewModel.updateProfile { it.copy(declaration = newValue) } },
                label = { Text("Declaration Statement") },
                placeholder = { Text("I hereby declare that the above information is true to the best of my knowledge...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}
