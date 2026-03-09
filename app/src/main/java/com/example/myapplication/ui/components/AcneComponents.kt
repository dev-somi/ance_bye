package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import coil.compose.AsyncImage // <-- 이제 인터넷 이미지는 안 쓰므로 삭제하거나 주석 처리하세요.
import com.example.myapplication.data.model.AcneType
import com.example.myapplication.data.model.Product

// 💡 [재사용 1] 제품 카드 컴포넌트 (업데이트 완료!)
@Composable
fun ProductCard(product: Product) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // [왼쪽] 제품 이미지 영역
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                // imageUrl 대신 imageResId를 확인합니다.
                if (product.imageResId != null) {
                    Image(
                        painter = painterResource(id = product.imageResId), // 로컬 이미지 불러오기
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        // 이미지를 꽉 차게 자를거면 Crop, 원본 비율 유지하며 다 보여줄거면 Fit
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("사진 없음", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // [오른쪽] 텍스트 정보
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (product.isPrescriptionRequired) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "처방전 필요",
                                fontSize = 11.sp,
                                color = Color.Red,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "처방전 불필요",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// 💡 [재사용 2] 성분 이름표
@Composable
fun IngredientBadge(name: String) {
    Surface(
        color = Color(0xFFE1F5FE),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "💊 $name",
            modifier = Modifier.padding(12.dp),
            color = Color(0xFF0277BD),
            fontWeight = FontWeight.Bold
        )
    }
}

// 💡 [재사용 3] 여드름 설명 카드 (여기는 아까 잘 작성하셔서 그대로 둡니다)
@Composable
fun AcneDescriptionCard(acneType: AcneType) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (acneType.imageResId != null) {
                    Image(
                        painter = painterResource(id = acneType.imageResId),
                        contentDescription = acneType.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("사진", color = Color.DarkGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(acneType.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    color = Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = acneType.subtitle,
                        color = Color(0xFF00695C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = acneType.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}