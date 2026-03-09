package com.example.myapplication.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.local.HistoryViewModel
import com.example.myapplication.data.model.AcneCase
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ManagementScreen(
    caseId: String,
    viewModel: HistoryViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val case = viewModel.getCaseById(caseId) ?: return

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val file = File(context.cacheDir, "case_${caseId}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }

            val oneDay = 1000L * 60 * 60 * 24
            val now = System.currentTimeMillis()

            if (now - case.updatedAt > oneDay) {
                viewModel.updateCase(
                    case.copy(
                        initialPhoto = case.latestPhoto,
                        latestPhoto = file.toURI().toString(),
                        updatedAt = now
                    )
                )
            } else {
                viewModel.updateCase(
                    case.copy(
                        latestPhoto = file.toURI().toString(),
                        updatedAt = now
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        MiniCaseCard(case)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "오늘 관리",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CasePhotoBox(
                label = "초기 사진",
                uri = case.initialPhoto
            )

            CasePhotoBox(
                label = "오늘 사진",
                uri = case.latestPhoto
            )
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = { cameraLauncher.launch(null) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("📷 오늘 사진 찍기")
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("history") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("⬅ 기록 화면으로 돌아가기")
        }
    }
}

@Composable
fun MiniCaseCard(case: AcneCase) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CasePhotoThumbnail(case.latestPhoto ?: case.initialPhoto)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    case.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val days = ((System.currentTimeMillis() - case.createdAt) / (1000L * 60 * 60 * 24)).toInt()
                Text("D+$days", color = Color(0xFF4CAF50), fontSize = 13.sp)

                Text(
                    SimpleDateFormat("MM.dd 진단됨", Locale.getDefault()).format(Date(case.createdAt)),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CasePhotoBox(label: String, uri: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFEFEF)),
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}
