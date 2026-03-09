package com.example.myapplication.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.data.remote.OnnxModelHelper
import com.example.myapplication.util.softmax
import com.example.myapplication.util.classNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import java.io.File
import java.io.FileOutputStream

@Composable
fun CropScreen(navController: NavHostController, photoUri: Uri) {
    val context = LocalContext.current
    var cropRect by remember { mutableStateOf<Rect?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var displayBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // [추가됨] 화면에 Image 컴포넌트가 실제 몇 픽셀로 그려졌는지 저장할 변수
    var viewWidth by remember { mutableFloatStateOf(0f) }
    var viewHeight by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(photoUri) {
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap = originalBitmap

            // 디스플레이용 리사이징
            val targetWidth = 1440
            displayBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                targetWidth,
                (targetWidth.toFloat() / originalBitmap.width * originalBitmap.height).toInt(),
                true
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "스캔하고자 하는 위치를 드래그해주세요",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
                .zIndex(1f) // 텍스트가 가려지지 않게
                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        displayBitmap?.let { dispBmp ->
            Image(
                bitmap = dispBmp.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    // [추가됨] 실제 화면 크기를 측정합니다.
                    .onGloballyPositioned { coordinates ->
                        viewWidth = coordinates.size.width.toFloat()
                        viewHeight = coordinates.size.height.toFloat()
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                cropRect = Rect(
                                    offset.x.toInt(),
                                    offset.y.toInt(),
                                    offset.x.toInt(),
                                    offset.y.toInt()
                                )
                            },
                            onDrag = { change, dragAmount ->
                                cropRect = cropRect?.let { rect ->
                                    Rect(
                                        rect.left,
                                        rect.top,
                                        (rect.right + dragAmount.x).toInt(),
                                        (rect.bottom + dragAmount.y).toInt()
                                    )
                                }
                            }
                        )
                    }
            )

            // ... (Canvas 그리는 코드는 기존과 동일하므로 생략 가능, 그대로 두세요) ...
            cropRect?.let { rect ->
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = Color.White.copy(alpha = 0.15f),
                        topLeft = Offset(rect.left.toFloat(), rect.top.toFloat()),
                        size = Size(
                            (rect.right - rect.left).toFloat(),
                            (rect.bottom - rect.top).toFloat()
                        )
                    )
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(rect.left.toFloat(), rect.top.toFloat()),
                        size = Size(
                            (rect.right - rect.left).toFloat(),
                            (rect.bottom - rect.top).toFloat()
                        ),
                        style = Stroke(width = 5f)
                    )
                }
            }
        }

        Button(
            onClick = {
                bitmap?.let { original ->
                    // [핵심 수정] 좌표 계산 로직
                    if (viewWidth > 0 && viewHeight > 0 && cropRect != null) {
                        val rect = cropRect!!

                        // 1. 이미지 비율과 화면 비율 계산
                        val imageRatio = original.width.toFloat() / original.height.toFloat()
                        val screenRatio = viewWidth / viewHeight

                        // 2. 실제로 이미지가 화면에 그려진 영역(Rendered Size) 계산
                        var renderedWidth = viewWidth
                        var renderedHeight = viewHeight
                        var offsetX = 0f
                        var offsetY = 0f

                        if (screenRatio > imageRatio) {
                            // 화면이 더 납작함 -> 양옆에 검은 여백(Pillarbox)이 생김 (이미지 높이가 꽉 참)
                            renderedWidth = viewHeight * imageRatio
                            offsetX = (viewWidth - renderedWidth) / 2
                        } else {
                            // 화면이 더 길쭉함 -> 위아래 검은 여백(Letterbox)이 생김 (이미지 너비가 꽉 참)
                            renderedHeight = viewWidth / imageRatio
                            offsetY = (viewHeight - renderedHeight) / 2
                        }

                        // 3. 스케일 팩터 계산 (원본 크기 / 렌더링된 크기)
                        val scale = original.width.toFloat() / renderedWidth

                        // 4. 좌표 보정 (터치좌표 - 여백) * 스케일
                        // coerceAtLeast/Most로 이미지 밖으로 나가는 것 방지
                        val left = ((rect.left - offsetX) * scale).toInt().coerceIn(0, original.width)
                        val top = ((rect.top - offsetY) * scale).toInt().coerceIn(0, original.height)
                        val right = ((rect.right - offsetX) * scale).toInt().coerceIn(0, original.width)
                        val bottom = ((rect.bottom - offsetY) * scale).toInt().coerceIn(0, original.height)

                        // 너비나 높이가 0이 되지 않도록 방어 코드
                        val validWidth = (right - left).coerceAtLeast(1)
                        val validHeight = (bottom - top).coerceAtLeast(1)

                        val cropped = Bitmap.createBitmap(
                            original,
                            left,
                            top,
                            validWidth,
                            validHeight
                        )

                        // --- 이 아래는 기존 코드와 동일 ---
                        navController.currentBackStackEntry?.savedStateHandle?.set("croppedBitmap", cropped)

                        val resized = Bitmap.createScaledBitmap(cropped, 224, 224, true)
                        val modelHelper = OnnxModelHelper(context)
                        val result = modelHelper.runInference(resized)
                        val probabilities = result.softmax()
                        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
                        val predictedLabel = classNames[maxIndex] ?: "알 수 없음"
                        val predictedConfidence = probabilities[maxIndex] * 100

                        navController.currentBackStackEntry?.savedStateHandle?.set("predictedLabel", predictedLabel)
                        navController.currentBackStackEntry?.savedStateHandle?.set("predictedConfidence", predictedConfidence)

                        navController.navigate("result/${Uri.encode(predictedLabel)}")
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("✅ 완료")
        }
    }
}