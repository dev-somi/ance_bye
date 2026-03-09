package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.acneGuideList
import com.example.myapplication.data.model.allProductsDB
import com.example.myapplication.ui.components.AcneDescriptionCard
import com.example.myapplication.ui.components.IngredientBadge
import com.example.myapplication.ui.components.ProductCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    onBackClick: () -> Unit = {}
) {
    // 1. 데이터 가져오기
    val guideList = acneGuideList
    val productDB = allProductsDB

    // [상태 1] 현재 선택된 여드름 타입
    var selectedAcneType by remember { mutableStateOf(guideList[0]) }

    // [상태 2] 현재 선택된 치료 목표
    var selectedGuide by remember(selectedAcneType) {
        mutableStateOf(selectedAcneType.treatmentGuides[0])
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // 배경색 지정 권장
        // 만약 상태바와 내용이 겹치면 아래 주석을 해제해서 '최소한의' 여백만 주세요.
        // .statusBarsPadding()
    ) {

        Text(
            text = "타입별 여드름",
            style = MaterialTheme.typography.headlineMedium,
            // 기존 paddingValues에 의존하지 않고 직접 여백 조절 가능
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

            // 1. 상단 탭 (블랙헤드, 화이트헤드...)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(guideList) { item ->
                    FilterChip(
                        selected = (item == selectedAcneType),
                        onClick = { selectedAcneType = item },
                        label = { Text(item.title.split("(")[0]) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00695C),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 2. 메인 컨텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // ▼▼▼ (1) 설명 카드: 여기서 바뀐 함수를 호출합니다! ▼▼▼
                AcneDescriptionCard(acneType = selectedAcneType)

                Spacer(modifier = Modifier.height(24.dp))

                // (2) 치료 목표 선택
                Text("치료 목표를 선택하세요", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedAcneType.treatmentGuides) { guide ->
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

                Spacer(modifier = Modifier.height(20.dp))

                // (3) 성분 및 제품 추천 리스트
                selectedGuide.ingredients.forEach { ingredientName ->
                    IngredientBadge(name = ingredientName)
                    Spacer(modifier = Modifier.height(8.dp))

                    val matchingProducts = productDB.filter { it.matchingIngredient == ingredientName }

                    if (matchingProducts.isNotEmpty()) {
                        matchingProducts.forEach { product ->
                            ProductCard(product)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    } else {
                        Text(
                            "현재 등록된 추천 제품이 없습니다.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun PreviewGuide() {
    GuideScreen()
}