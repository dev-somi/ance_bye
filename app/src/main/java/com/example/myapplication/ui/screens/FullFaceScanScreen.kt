package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController

@Composable
fun FullFaceScanScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    // 1️⃣ [카메라] 런처 (촬영 후 바로 비트맵 반환)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // 데이터 전달 및 이동
            navController.currentBackStackEntry?.savedStateHandle?.set("fullFaceBitmap", bitmap)
            navController.currentBackStackEntry?.savedStateHandle?.set("diagnosisLevel", "Moderate")
            navController.navigate("diagnosis_result")
        }
    }

    // 2️⃣ [갤러리] 런처 (추가됨!)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // 갤러리 URI -> 비트맵 변환 과정
            // (다음 화면이 Bitmap을 받도록 설계되어 있어서 변환이 필요합니다)
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            }

            // 변환된 비트맵을 담아서 이동
            navController.currentBackStackEntry?.savedStateHandle?.set("fullFaceBitmap", bitmap)
            navController.currentBackStackEntry?.savedStateHandle?.set("diagnosisLevel", "Moderate")
            navController.navigate("diagnosis_result")
        }
    }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    // ⚠️ 자동 실행(LaunchedEffect) 제거됨
    // 사용자가 버튼을 눌러서 선택할 수 있게 변경

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "얼굴 스캔 아이콘",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "얼굴 전체를 분석합니다.",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "카메라로 찍거나 갤러리에서 선택해주세요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3️⃣ [카메라 버튼]
            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        cameraLauncher.launch(null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("촬영하기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4️⃣ [갤러리 버튼] (추가됨!)
            OutlinedButton(
                onClick = {
                    // [기존 코드] 진짜 갤러리 열기
                    // ****** 테스트용 apk 빌드 후 주석 제거 **************
                    galleryLauncher.launch("image/*")

                    // [새 코드] 가짜 갤러리 화면으로 이동
                    // ****** 테스트용 apk 빌드 후 주석 처리 **************
                    // navController.navigate("fake_gallery/full")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("갤러리에서 선택")
            }
        }
    }
}