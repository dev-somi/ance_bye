package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// R 클래스 import 필요 (패키지명에 따라 다름. 보통 자동완성 됩니다)
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakeGalleryScreen(
    // 이미지가 선택되었을 때 실행할 함수 (선택된 이미지 ID를 넘겨줌)
    onImageSelected: (Int) -> Unit
) {
    // 1. 준비하신 이미지 리스트
    val galleryImages = listOf(
        R.drawable.test_face,
        R.drawable.test_acne1,
        R.drawable.test_acne2
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사진 선택 (테스트용)", fontSize = 18.sp) }
            )
        }
    ) { paddingValues ->
        // 2. 갤러리처럼 격자 형태로 보여주기
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 한 줄에 2개씩 배치
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(galleryImages) { imageResId ->
                // 각 이미지 아이템
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // 꽉 차게 자르기
                    modifier = Modifier
                        .aspectRatio(1f) // 정사각형 모양 유지
                        .clickable {
                            // 3. 클릭 시 선택된 이미지 ID를 가지고 콜백 실행
                            onImageSelected(imageResId)
                        }
                )
            }
        }
    }
}