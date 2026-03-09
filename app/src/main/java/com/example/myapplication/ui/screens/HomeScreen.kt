package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Text("안녕하세요!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "피부가 잘 회복되고 있어요, 오늘도 여드름 상태 확인해볼까요?",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFC0CB), Color(0xFFB2DFDB))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { navController.navigate("scan") },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(24.dp))

                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "카메라",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "오늘의 여드름 사진 촬영하기",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "현 상태를 체크해봅시다!",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
