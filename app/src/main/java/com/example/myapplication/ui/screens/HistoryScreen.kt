package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.local.HistoryViewModel
import com.example.myapplication.data.model.AcneCase
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    navController: NavHostController
) {
    val cases by viewModel.cases.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("진단 기록", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "현재 ${cases.size}개의 여드름을 관리하고 있어요",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        if (cases.isEmpty()) {
            Text("아직 저장된 기록이 없습니다.", color = Color.Gray)
            return
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            cases.forEach { case ->
                AcneCaseCard(
                    case = case,
                    onClickManage = {
                        navController.navigate("manage/${case.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun AcneCaseCard(
    case: AcneCase,
    onClickManage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            CasePhotoThumbnail(case.latestPhoto ?: case.initialPhoto)

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(case.label, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

                val days = ((System.currentTimeMillis() - case.createdAt) / (1000L * 60 * 60 * 24)).toInt()
                Text("D+$days", color = Color(0xFF4CAF50))

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onClickManage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text("오늘 관리로 이동")
                }
            }
        }
    }
}

@Composable
fun CasePhotoThumbnail(uri: String?) {
    Box(
        modifier = Modifier
            .size(85.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0)),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Photo,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
