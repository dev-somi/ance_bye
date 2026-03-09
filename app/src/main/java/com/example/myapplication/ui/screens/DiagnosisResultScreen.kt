// ui/screens/DiagnosisResultScreen.kt
package com.example.myapplication.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import 경로가 다르면 본인 프로젝트에 맞게 수정해주세요 (보통 data.local 또는 data.model)
import com.example.myapplication.data.model.DiagnosisResult

@Composable
fun DiagnosisResultScreen(
    userImageBitmap: Bitmap?,
    resultData: DiagnosisResult, // 데이터를 통째로 받음
    onBackClick: () -> Unit,
    onRescanClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 색상 변환 (Long -> Color)
    val themeColor = Color(resultData.colorHex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F9))
    ) {
        // 1. 상단바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로 가기",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("얼굴 전체 분석 결과", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        // --- 메인 스크롤 영역 ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            // 2. 진단 결과 카드
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // [데이터 연결] 타이틀
                    Text(
                        text = resultData.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = themeColor
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // [데이터 연결] 상세 설명
                    Text(
                        text = resultData.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 사용자 사진
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userImageBitmap != null) {
                            Image(
                                bitmap = userImageBitmap.asImageBitmap(),
                                contentDescription = "진단 사진",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("사진 없음", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // [시각화] 3색 게이지 바 (중복 제거하고 하나만 남김)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFF00C853)) // Mild (초록)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFFF9800)) // Moderate (주황)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFD50000)) // Severe (빨강)
                        )
                    }

                    // [시각화] 화살표 위치 계산 (BiasAlignment 적용)
                    val arrowBias = when (resultData.level) {
                        "Mild" -> -0.7f
                        "Severe" -> 0.7f
                        else -> 0f
                    }

                    // 화살표 아이콘
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        contentAlignment = BiasAlignment( // 중요: BiasAlignment 사용
                            horizontalBias = arrowBias,
                            verticalBias = 0f
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropUp,
                            contentDescription = null,
                            tint = themeColor, // 화살표 색상도 테마색으로
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 권고안 카드 (Recommendation)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, themeColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💊", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        // [데이터 연결] 권고안 제목
                        Text(
                            text = resultData.recommendationTitle,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // [데이터 연결] 권고안 내용
                    Text(
                        text = resultData.recommendationBody,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 비용 안내 카드 (Cost)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF3F6)), // 회색 톤
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💰", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        // [데이터 연결] 비용 제목
                        Text(
                            text = resultData.costTitle,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // [데이터 연결] 비용 내용
                    Text(
                        text = resultData.costBody,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // 5. 하단 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onRescanClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("다시 스캔", color = Color.Gray)
            }
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}