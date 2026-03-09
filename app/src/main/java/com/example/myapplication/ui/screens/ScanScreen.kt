package com.example.myapplication.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import java.io.File
import java.io.FileOutputStream

@Composable
fun ScanScreen(navController: NavHostController) {
    val context = LocalContext.current

    // 설정 저장소 (체크리스트 상태 저장용)
    val prefs = context.getSharedPreferences("settings", Activity.MODE_PRIVATE)

    // 체크리스트 관련 상태
    var checklist1 by remember { mutableStateOf(false) }
    var checklist2 by remember { mutableStateOf(false) }
    var checklist3 by remember { mutableStateOf(false) }
    var showChecklist by rememberSaveable {
        mutableStateOf(prefs.getBoolean("showChecklist", true))
    }

    val allChecked = checklist1 && checklist2 && checklist3

    // 1️⃣ [카메라] 런처
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // 🔥 [수정 포인트] 파일명 뒤에 시간을 붙여서, 이전 사진과 겹치지 않게 합니다.
            val fileName = "captured_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, fileName)

            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }

            val uri = Uri.fromFile(file)

            // 🔥 [중요] 비트맵 대신 "파일 경로(Uri 문자열)"를 넘깁니다.
            // Uri에 특수문자가 있을 수 있으므로 encode 필수!
            navController.navigate("crop/${Uri.encode(uri.toString())}")
        }
    }

    // 2️⃣ [갤러리] 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // 갤러리에서 받은 Uri도 인코딩해서 넘깁니다.
            navController.navigate("crop/${Uri.encode(uri.toString())}")
        }
    }

    // 권한 요청 런처
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 메인 화면 UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "스캔 아이콘",
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("피부 상태를 스캔해보세요!", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "촬영하거나 갤러리에서 사진을 불러오세요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3️⃣ [카메라 버튼]
            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        cameraLauncher.launch(null)
                    }
                },
                enabled = !showChecklist,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("카메라로 찍기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4️⃣ [갤러리 버튼]
            OutlinedButton(
                onClick = {
                    // [기존 코드] 진짜 갤러리 열기
                    // ****** 테스트용 apk 빌드 후 주석 제거 **************
                    galleryLauncher.launch("image/*")

                    // [새 코드] 우리가 만든 가짜 갤러리 화면으로 이동!
                    // ****** 테스트용 apk 빌드 후 주석 처리 **************
                    //navController.navigate("fake_gallery/single")
                },
                enabled = !showChecklist,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("갤러리에서 선택")
            }
        }

        // 체크리스트 모달
        if (showChecklist) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {}
            )

            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("시작하기 전에", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("정확한 분석을 위해 확인해주세요", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checklist1, onCheckedChange = { checklist1 = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1. 세안을 했나요?")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checklist2, onCheckedChange = { checklist2 = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("2. 메이크업을 지웠나요?")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checklist3, onCheckedChange = { checklist3 = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("3. 조명이 충분한가요?")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = allChecked,
                            onCheckedChange = {
                                val newState = it
                                checklist1 = newState
                                checklist2 = newState
                                checklist3 = newState
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("모두 선택")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (allChecked) {
                                showChecklist = false
                                prefs.edit().putBoolean("showChecklist", false).apply()
                            }
                        },
                        enabled = allChecked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("완료")
                    }
                }
            }
        }
    }
}