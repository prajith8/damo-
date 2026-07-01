package com.example.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.ResumeProfile
import java.io.ByteArrayOutputStream
import java.io.InputStream

object PrintUtils {

    fun printResume(context: Context, profile: ResumeProfile) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${profile.fullName.ifEmpty { "Resume" }.replace(" ", "_")}_Print"
        
        val webView = WebView(context)
        val htmlContent = generateResumeHtml(context, profile)
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }
        
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    }

    private fun getBase64Image(context: Context, uriString: String?): String? {
        if (uriString.isNullOrEmpty()) return null
        return try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                // Scale down bitmap to a reasonable size for resume to save memory and size
                val scaledBitmap = scaleBitmap(bitmap, 250)
                val byteArrayOutputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.NO_WRAP)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension * height) / width
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * width) / height
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun generateResumeHtml(context: Context, profile: ResumeProfile): String {
        val primaryColor = when (profile.selectedTheme.lowercase()) {
            "blue" -> "#0057ff"
            "black" -> "#111111"
            "gold" -> "#c49b00"
            "green" -> "#009944"
            else -> "#0057ff"
        }
        
        val base64Photo = getBase64Image(context, profile.photoUri)
        val photoHtml = if (base64Photo != null) {
            """<img src="data:image/jpeg;base64,$base64Photo" class="profile-photo" alt="Photo">"""
        } else {
            ""
        }

        // Parse list sections
        val qualItems = if (profile.qualification.isNotEmpty()) {
            profile.qualification.split("\n").filter { it.isNotBlank() }
                .joinToString("") { "<li>$it</li>" }
        } else ""

        val expItems = if (profile.experience.isNotEmpty()) {
            profile.experience.split("\n").filter { it.isNotBlank() }
                .joinToString("") { "<li>$it</li>" }
        } else ""

        val langItems = if (profile.languages.isNotEmpty()) {
            profile.languages.split(",").filter { it.isNotBlank() }
                .joinToString("") { "<li>${it.trim()}</li>" }
        } else ""

        val skillItems = if (profile.skills.isNotEmpty()) {
            profile.skills.split(",").filter { it.isNotBlank() }
                .joinToString("") { "<li>${it.trim()}</li>" }
        } else ""

        val genderStr = if (profile.gender == "Other") {
            profile.customGender ?: ""
        } else {
            profile.gender
        }

        return """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="UTF-8">
        <title>${profile.fullName.ifEmpty { "Resume" }}</title>
        <style>
            :root {
                --primary: $primaryColor;
                --text-dark: #111111;
                --text-light: #555555;
            }
            body {
                font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                margin: 0;
                padding: 30px;
                color: var(--text-dark);
                line-height: 1.5;
                background-color: #ffffff;
            }
            .header-container {
                display: flex;
                align-items: center;
                border-bottom: 3px solid var(--primary);
                padding-bottom: 15px;
                margin-bottom: 25px;
            }
            .header-text {
                flex: 1;
            }
            .profile-photo {
                width: 100px;
                height: 100px;
                border-radius: 8px;
                border: 2px solid #dddddd;
                object-fit: cover;
                margin-right: 20px;
            }
            .name {
                font-size: 28px;
                font-weight: bold;
                color: var(--text-dark);
                margin: 0 0 5px 0;
                letter-spacing: -0.5px;
            }
            .contact-info {
                font-size: 13px;
                color: var(--text-light);
                margin-bottom: 8px;
            }
            .contact-pill {
                display: inline-block;
                margin-right: 12px;
            }
            .details-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 5px;
                font-size: 13px;
                color: var(--text-light);
                margin-top: 8px;
            }
            .section-title {
                font-size: 16px;
                font-weight: bold;
                color: var(--text-dark);
                border-left: 5px solid var(--primary);
                padding-left: 10px;
                margin-top: 25px;
                margin-bottom: 10px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            .section-content {
                font-size: 13px;
                color: #222222;
                margin-left: 15px;
            }
            ul {
                margin: 0;
                padding-left: 18px;
            }
            li {
                margin-bottom: 4px;
            }
            .grid-details-card {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 12px;
                background-color: #fafafa;
                border: 1px solid #eeeeee;
                border-radius: 6px;
                padding: 10px;
                margin-left: 15px;
            }
            .grid-detail-item {
                font-size: 12px;
            }
            .grid-detail-item strong {
                color: var(--text-light);
                display: block;
                font-size: 10px;
                text-transform: uppercase;
                margin-bottom: 2px;
            }
            .address-block {
                margin-left: 15px;
                font-size: 13px;
                background-color: #fafafa;
                padding: 8px;
                border-radius: 6px;
                border-left: 3px solid #dddddd;
            }
            .declaration-text {
                font-style: italic;
                color: #444444;
                margin-left: 15px;
                margin-top: 10px;
                font-size: 13px;
            }
            @media print {
                body {
                    padding: 0;
                }
            }
        </style>
        </head>
        <body>
            <div class="header-container">
                $photoHtml
                <div class="header-text">
                    <div class="name">${profile.fullName.ifEmpty { "YOUR NAME" }}</div>
                    <div class="contact-info">
                        ${if (profile.email.isNotEmpty()) """<span class="contact-pill">&#9993; ${profile.email}</span>""" else ""}
                        ${if (profile.phone.isNotEmpty()) """<span class="contact-pill">&#9742; ${profile.phone}</span>""" else ""}
                    </div>
                    <div class="details-grid">
                        ${if (genderStr.isNotEmpty()) """<div><strong>Gender:</strong> $genderStr</div>""" else ""}
                        ${if (profile.dob.isNotEmpty()) """<div><strong>DOB:</strong> ${profile.dob}</div>""" else ""}
                        ${if (profile.fathersName.isNotEmpty()) """<div><strong>Father's Name:</strong> ${profile.fathersName}</div>""" else ""}
                        ${if (profile.mothersName.isNotEmpty()) """<div><strong>Mother's Name:</strong> ${profile.mothersName}</div>""" else ""}
                        ${if (profile.maritalStatus.isNotEmpty()) """<div><strong>Marital Status:</strong> ${profile.maritalStatus}</div>""" else ""}
                    </div>
                </div>
            </div>

            <!-- PERSONAL DETAILS -->
            ${if (profile.nationality.isNotEmpty() || profile.religion.isNotEmpty() || profile.bloodGroup.isNotEmpty()) """
            <div class="section-title">Personal Details</div>
            <div class="grid-details-card">
                ${if (profile.nationality.isNotEmpty()) """<div class="grid-detail-item"><strong>Nationality</strong>${profile.nationality}</div>""" else ""}
                ${if (profile.religion.isNotEmpty()) """<div class="grid-detail-item"><strong>Religion</strong>${profile.religion}</div>""" else ""}
                ${if (profile.bloodGroup.isNotEmpty()) """<div class="grid-detail-item"><strong>Blood Group</strong>${profile.bloodGroup}</div>""" else ""}
            </div>
            """ else ""}

            <!-- CONTACT DETAILS -->
            ${if (profile.street.isNotEmpty() || profile.city.isNotEmpty() || profile.state.isNotEmpty() || profile.pincode.isNotEmpty()) """
            <div class="section-title">Contact Details</div>
            <div class="grid-details-card">
                ${if (profile.street.isNotEmpty()) """<div class="grid-detail-item"><strong>Street</strong>${profile.street}</div>""" else ""}
                ${if (profile.city.isNotEmpty()) """<div class="grid-detail-item"><strong>City</strong>${profile.city}</div>""" else ""}
                ${if (profile.state.isNotEmpty()) """<div class="grid-detail-item"><strong>State</strong>${profile.state}</div>""" else ""}
                ${if (profile.pincode.isNotEmpty()) """<div class="grid-detail-item"><strong>Pincode</strong>${profile.pincode}</div>""" else ""}
            </div>
            """ else ""}

            <!-- ADDRESS -->
            ${if (profile.address.isNotEmpty()) """
            <div class="section-title">Address</div>
            <div class="address-block">
                ${profile.address.replace("\n", "<br>")}
            </div>
            """ else ""}

            <!-- CAREER OBJECTIVE -->
            <div class="section-title">Career Objective</div>
            <div class="section-content">
                ${profile.careerObjective.ifEmpty { "To secure a responsible position and contribute my skills." }}
            </div>

            <!-- QUALIFICATION -->
            ${if (qualItems.isNotEmpty()) """
            <div class="section-title">Qualification</div>
            <div class="section-content">
                <ul>$qualItems</ul>
            </div>
            """ else ""}

            <!-- EXPERIENCE -->
            ${if (expItems.isNotEmpty()) """
            <div class="section-title">Experience</div>
            <div class="section-content">
                <ul>$expItems</ul>
            </div>
            """ else ""}

            <!-- LANGUAGES -->
            ${if (langItems.isNotEmpty()) """
            <div class="section-title">Languages</div>
            <div class="section-content">
                <ul>$langItems</ul>
            </div>
            """ else ""}

            <!-- SKILLS -->
            ${if (skillItems.isNotEmpty()) """
            <div class="section-title">Skills</div>
            <div class="section-content">
                <ul>$skillItems</ul>
            </div>
            """ else ""}

            <!-- DECLARATION -->
            <div class="section-title">Declaration</div>
            <div class="declaration-text">
                ${profile.declaration.ifEmpty { "I hereby declare that the above information is true." }}
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}
