package com.example.myapplication.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.data.local.HistoryViewModel
import com.example.myapplication.data.model.AcneCase
import com.example.myapplication.data.model.AcneType
import com.example.myapplication.data.model.acneGuideList
import com.example.myapplication.data.model.allProductsDB
import com.example.myapplication.data.model.diagnosisList
import com.example.myapplication.ui.components.AcneDescriptionCard
import com.example.myapplication.ui.components.IngredientBadge
import com.example.myapplication.ui.components.ProductCard
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    resultText: String,
    navController: NavHostController,
    historyViewModel: HistoryViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // 1. 네비게이션 데이터 수신
    val initialLabel = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("predictedLabel") ?: resultText

    val predictedConfidence = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Float>("predictedConfidence") ?: 0f

    val croppedBitmap = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Bitmap>("croppedBitmap")

    // ========================================================================
    // 🔥 [State 관리] 결과 변경 및 바텀시트 제어
    // ========================================================================
    var currentLabel by remember { mutableStateOf(initialLabel) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // 2. [데이터 로직] currentLabel에 따라 데이터 실시간 갱신

    // A. 여드름 타입 찾기
    val matchedAcneType = acneGuideList.find {
        val keyword = currentLabel.split("(")[0].trim()
        it.title.contains(keyword)
    } ?: acneGuideList[0]

    // B. 선택된 치료 목표 (여드름 타입이 바뀌면 초기화)
    var selectedGuide by remember(matchedAcneType) {
        mutableStateOf(matchedAcneType.treatmentGuides.firstOrNull())
    }

    // 3. UI 그리기
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7F9))
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // [섹션 1] 헤더 (사진 + 결과)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (croppedBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = croppedBitmap.asImageBitmap(),
                                contentDescription = "사용자 사진",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Photo,
                            contentDescription = "사진 없음",
                            modifier = Modifier.size(100.dp),
                            tint = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text("분석 결과", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(currentLabel, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))

                        if (currentLabel == initialLabel) {
                            Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "정확도 %.1f%%".format(predictedConfidence),
                                    fontSize = 11.sp,
                                    color = Color(0xFF1565C0),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "사용자 선택됨",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 피드백 트리거 버튼
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = { showBottomSheet = true }) {
                    Text(
                        text = "결과가 맞지 않는 것 같나요? 다른 증상 선택하기 >",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            // [섹션 2] 설명 카드
            Text("🔍 상세 분석", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            AcneDescriptionCard(acneType = matchedAcneType)

            Spacer(modifier = Modifier.height(16.dp))

            // [섹션 3] 맞춤 치료 가이드
            Text("💊 맞춤 치료 가이드", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            if (selectedGuide != null) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(matchedAcneType.treatmentGuides) { guide ->
                        ElevatedFilterChip(
                            selected = (guide == selectedGuide),
                            onClick = { selectedGuide = guide },
                            label = { Text(guide.goalName) },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                selectedContainerColor = Color(0xFF2962FF),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFE3F2FD),
                                labelColor = Color(0xFF1565C0)
                            )
                        )
                    }
                }


                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "목표: ${selectedGuide!!.goalName}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2962FF),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        selectedGuide!!.ingredients.forEach { ingredientName ->
                            IngredientBadge(name = ingredientName)
                            Spacer(modifier = Modifier.height(8.dp))

                            val products = allProductsDB.filter { it.matchingIngredient == ingredientName }

                            if (products.isNotEmpty()) {
                                products.forEach { product ->
                                    ProductCard(product = product)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            } else {
                                Text("관련 추천 제품 정보 없음", fontSize = 12.sp, color = Color.LightGray)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            } else {
                Text("치료 가이드 정보가 없습니다.", color = Color.Gray)
            }

            // [하단 버튼]
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigate("scan_single") },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("다시 촬영")
                }

                Button(
                    onClick = {
                        val savedUri = croppedBitmap?.let {
                            val file = File(context.cacheDir, "record_${System.currentTimeMillis()}.jpg")
                            FileOutputStream(file).use { fos ->
                                it.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                            }
                            Uri.fromFile(file).toString()
                        }
                        val case = AcneCase(
                            label = currentLabel,
                            initialPhoto = savedUri,
                            latestPhoto = null,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        historyViewModel.addCase(case)
                        navController.navigate("history")
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
                ) {
                    Text("결과 저장", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 🔥 [변경] 바텀 시트 (아코디언 기능 적용됨!)
        if (showBottomSheet) {
            // 1. 여기서 state를 만듭니다. (skipPartiallyExpanded = true 설정!)
            val bottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState, // 2. 만든 state를 여기에 연결
                containerColor = Color.White
            ) {
                // 현재 펼쳐져 있는 아이템의 이름을 기억하는 변수
                var expandedTypeTitle by remember { mutableStateOf<String?>(null) }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 40.dp)
                ) {
                    Text(
                        "다른 증상 선택하기",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    Text(
                        "본인의 증상과 가장 비슷한 사진을 선택해주세요.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // key는 데이터 고유 식별을 위해 title 유지 (또는 id가 있다면 id 사용)
                        items(acneGuideList, key = { it.title }) { acneType ->
                            FeedbackItem(
                                acneType = acneType,
                                isSelected = (acneType.title == currentLabel),
                                isExpanded = (acneType.title == expandedTypeTitle),
                                onExpand = {
                                    expandedTypeTitle = if (expandedTypeTitle == acneType.title) null else acneType.title
                                },
                                onSelect = {
                                    // 선택 시 로직은 기존 데이터 흐름 유지를 위해 title을 저장하거나,
                                    // 필요에 따라 resultScreen_Title로 변경하셔도 됩니다.
                                    // 여기서는 "값" 자체는 title로 유지하고 보여주는 것만 변경했습니다.
                                    currentLabel = acneType.title
                                    showBottomSheet = false
                                    Toast.makeText(context, "${acneType.resultScreen_Title}로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 🔥 [변경] 아코디언 기능을 위해 부모의 제어를 받도록 수정됨
@Composable
fun FeedbackItem(
    acneType: AcneType,
    isSelected: Boolean,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7F9)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    // 이미지가 있다면 이미지 표시, 없다면 텍스트 유지
                    if (acneType.imageResId != 0) {
                        Image(
                            painter = painterResource(id = acneType.imageResId), // 여기서 이미지를 불러옵니다
                            contentDescription = acneType.title,
                            contentScale = ContentScale.Crop, // 이미지를 꽉 차게 자름
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // 이미지가 없을 때만 글씨 표시
                        Text("사진", fontSize = 10.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // [변경 포인트 1] 제목 표시 변경
                // 기존: acneType.title.split("(")[0]
                // 변경: acneType.resultScreen_Title
                Text(
                    text = acneType.resultScreen_Title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    // [변경 포인트 2] 설명 표시 변경
                    // 기존: acneType.description
                    // 변경: acneType.resultScreen_Description
                    Text(
                        text = acneType.resultScreen_Description,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp // 가독성을 위해 줄간격 살짝 추가 추천
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onSelect,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("이 증상으로 결과 보기")
                    }
                }
            }
        }
    }
}