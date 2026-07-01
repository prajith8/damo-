package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.ResumeProfile
import com.example.ui.ResumeViewModel
import com.example.ui.utils.PrintUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel: ResumeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.editingProfile.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    if (profile == null) {
        onBack()
        return
    }

    val activeProfile = profile!!
    val activeThemeId = activeProfile.selectedTheme.lowercase()

    val themeColor = when (activeThemeId) {
        "blue" -> Color(0xFF0057FF)
        "black" -> Color(0xFF111111)
        "gold" -> Color(0xFFC49B00)
        "green" -> Color(0xFF009944)
        else -> Color(0xFF0057FF)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resume Preview", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Quick edit button
                    TextButton(onClick = { viewModel.startEditing(activeProfile) }) {
                        Text("Edit Details")
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
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Direct Theme Switch in bottom bar for easy testing
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "blue" to Color(0xFF0057FF),
                            "black" to Color(0xFF111111),
                            "gold" to Color(0xFFC49B00),
                            "green" to Color(0xFF009944)
                        ).forEach { (id, color) ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (activeThemeId == id) 2.5.dp else 0.dp,
                                        color = if (activeThemeId == id) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = if (activeThemeId == id) 1.5.dp else 0.dp,
                                        color = if (activeThemeId == id) Color.Gray else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.changeTheme(id) }
                            )
                        }
                    }

                    // Print / Save PDF
                    Button(
                        onClick = { PrintUtils.printResume(context, activeProfile) },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        modifier = Modifier.weight(1.5f),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Print / Save PDF", fontWeight = FontWeight.Bold)
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
                .background(Color(0xFFE2E8F0))
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Paper-Like Canvas Card
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // --- HEADER SEGMENT ---
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        // Image
                        if (!activeProfile.photoUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = activeProfile.photoUri,
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Header Text
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeProfile.fullName.ifEmpty { "YOUR NAME" },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                if (activeProfile.email.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF64748B))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(activeProfile.email, fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                }
                                if (activeProfile.phone.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF64748B))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(activeProfile.phone, fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                }
                            }

                            // Dynamic details line
                            val detailsList = mutableListOf<String>()
                            if (activeProfile.gender.isNotEmpty()) {
                                val gen = if (activeProfile.gender == "Other") activeProfile.customGender ?: "" else activeProfile.gender
                                if (gen.isNotEmpty()) detailsList.add("Gender: $gen")
                            }
                            if (activeProfile.dob.isNotEmpty()) detailsList.add("DOB: ${activeProfile.dob}")
                            if (activeProfile.maritalStatus.isNotEmpty()) detailsList.add("Status: ${activeProfile.maritalStatus}")

                            if (detailsList.isNotEmpty()) {
                                Text(
                                    text = detailsList.joinToString("  |  "),
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }

                    // Thick Primary Border line matching the theme
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(themeColor)
                            .padding(bottom = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // --- CAREER OBJECTIVE ---
                    PreviewSection(title = "Career Objective", color = themeColor) {
                        Text(
                            text = activeProfile.careerObjective.ifEmpty { "To secure a responsible position and contribute my skills." },
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }

                    // --- PERSONAL DETAILS ---
                    if (activeProfile.nationality.isNotEmpty() || activeProfile.religion.isNotEmpty() || activeProfile.bloodGroup.isNotEmpty()) {
                        PreviewSection(title = "Personal Details", color = themeColor) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (activeProfile.nationality.isNotEmpty()) {
                                    PreviewDetailRow("Nationality", activeProfile.nationality)
                                }
                                if (activeProfile.religion.isNotEmpty()) {
                                    PreviewDetailRow("Religion", activeProfile.religion)
                                }
                                if (activeProfile.bloodGroup.isNotEmpty()) {
                                    PreviewDetailRow("Blood Group", activeProfile.bloodGroup)
                                }
                            }
                        }
                    }

                    // --- CONTACT DETAILS ---
                    if (activeProfile.street.isNotEmpty() || activeProfile.city.isNotEmpty() || activeProfile.state.isNotEmpty() || activeProfile.pincode.isNotEmpty()) {
                        PreviewSection(title = "Contact Details", color = themeColor) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (activeProfile.street.isNotEmpty()) {
                                    PreviewDetailRow("Street", activeProfile.street)
                                }
                                if (activeProfile.city.isNotEmpty()) {
                                    PreviewDetailRow("City", activeProfile.city)
                                }
                                if (activeProfile.state.isNotEmpty()) {
                                    PreviewDetailRow("State", activeProfile.state)
                                }
                                if (activeProfile.pincode.isNotEmpty()) {
                                    PreviewDetailRow("Pincode", activeProfile.pincode)
                                }
                            }
                        }
                    }

                    // --- PERMANENT ADDRESS ---
                    if (activeProfile.address.isNotEmpty()) {
                        PreviewSection(title = "Permanent Address", color = themeColor) {
                            Text(
                                text = activeProfile.address,
                                fontSize = 13.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // --- QUALIFICATION ---
                    if (activeProfile.qualification.isNotEmpty()) {
                        PreviewSection(title = "Qualifications", color = themeColor) {
                            BulletList(items = activeProfile.qualification.split("\n"))
                        }
                    }

                    // --- EXPERIENCE ---
                    if (activeProfile.experience.isNotEmpty()) {
                        PreviewSection(title = "Experience", color = themeColor) {
                            BulletList(items = activeProfile.experience.split("\n"))
                        }
                    }

                    // --- SKILLS ---
                    if (activeProfile.skills.isNotEmpty()) {
                        PreviewSection(title = "Skills", color = themeColor) {
                            TagList(items = activeProfile.skills.split(","), color = themeColor)
                        }
                    }

                    // --- LANGUAGES ---
                    if (activeProfile.languages.isNotEmpty()) {
                        PreviewSection(title = "Languages", color = themeColor) {
                            TagList(items = activeProfile.languages.split(","), color = themeColor)
                        }
                    }

                    // --- DECLARATION ---
                    PreviewSection(title = "Declaration", color = themeColor) {
                        Text(
                            text = activeProfile.declaration.ifEmpty { "I hereby declare that the above information is true to the best of my knowledge." },
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF475569),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewSection(
    title: String,
    color: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    ) {
        // Section Title with dynamic left border line
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 0.5.sp
            )
        }
        
        // Content Area indented slightly
        Box(
            modifier = Modifier
                .padding(start = 14.dp)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun PreviewDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF475569),
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.filter { it.isNotBlank() }.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "•",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = item.trim(),
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagList(items: List<String>, color: Color) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.filter { it.isNotBlank() }.forEach { tag ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.08f))
                    .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tag.trim(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
        }
    }
}
