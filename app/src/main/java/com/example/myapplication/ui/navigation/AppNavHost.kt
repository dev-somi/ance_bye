package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.local.HistoryViewModel
import com.example.myapplication.ui.screens.CropScreen
import com.example.myapplication.ui.screens.HistoryScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.ManagementScreen
import com.example.myapplication.ui.screens.ResultScreen
import com.example.myapplication.ui.screens.ScanScreen
import com.example.myapplication.ui.screens.ScanSelectScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.screens.DiagnosisResultRoute
import com.example.myapplication.ui.screens.FakeGalleryScreen
import com.example.myapplication.ui.screens.FullFaceScanScreen
import com.example.myapplication.ui.screens.GuideScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val historyViewModel: HistoryViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }

            composable("scan") { ScanSelectScreen(navController) }
            composable("scan_single") { ScanScreen(navController) }

            composable("face_scan") {
                FullFaceScanScreen(navController)
            }

            composable("diagnosis_result") {
                DiagnosisResultRoute(navController)
            }

            composable("history") { HistoryScreen(historyViewModel, navController) }

            composable("guide") { GuideScreen() }

            composable("crop/{photoUri}") { backStackEntry ->
                val uriStr = backStackEntry.arguments?.getString("photoUri")
                uriStr?.let { CropScreen(navController, Uri.parse(it)) }
            }

            composable("manage/{caseId}") { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: return@composable
                ManagementScreen(caseId, historyViewModel, navController)
            }

            composable("result/{resultText}") { backStackEntry ->
                val resultText = backStackEntry.arguments?.getString("resultText")
                    ?: "결과를 불러올 수 없습니다."

                ResultScreen(resultText, navController, historyViewModel)
            }

            // NavHost 안쪽...

            composable("fake_gallery/{type}") { backStackEntry ->
                // 1. 넘어온 타입 받기 ("single" 또는 "full")
                val type = backStackEntry.arguments?.getString("type") ?: "single"
                val context = LocalContext.current

                FakeGalleryScreen(
                    onImageSelected = { selectedId ->
                        val fakeUri = Uri.parse("android.resource://${context.packageName}/$selectedId")
                        val encodedUri = Uri.encode(fakeUri.toString())

                        // 2. 타입에 따라 갈림길 만들기
                        if (type == "single") {
                            // [단일 분석] -> 자르기(Crop) 화면으로 이동
                            navController.navigate("crop/$encodedUri") {
                                popUpTo("fake_gallery/{type}") { inclusive = true }
                            }
                        } else {
                            // [전체 분석] -> 바로 결과 화면으로 이동

                            // ⚠️ 주의: 전체 분석 화면이 뷰모델에서 이미지를 가져다 쓴다면
                            // 여기서 이미지를 뷰모델에 넣어주는 코드가 필요할 수 있습니다!
                            // (아까 지우라고 했던 viewModel.updateImage(fakeUri) 같은 것)

                            // 예: mainViewModel.setSelectedImage(fakeUri)

                            navController.navigate("diagnosis_result") {
                                popUpTo("fake_gallery/{type}") { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", "홈", Icons.Filled.Home),
        BottomNavItem("scan", "스캔", Icons.Filled.CameraAlt),
        BottomNavItem("history", "기록", Icons.Filled.History),
        BottomNavItem("guide", "가이드", Icons.Filled.Info)
    )

    NavigationBar (
        containerColor = Color.White, // 배경 흰색으로 변경
        tonalElevation = 10.dp        // 그림자 살짝 추가
    ){
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF546E7A), // 선택된 배경색 (진한 회청색)
                    selectedIconColor = Color.White,    // 선택된 아이콘색 (흰색)
                    selectedTextColor = Color(0xFF546E7A), // 선택된 글씨색
                    unselectedIconColor = Color(0xFFB0BEC5), // 선택 안 된 아이콘색 (연한 회색)
                    unselectedTextColor = Color(0xFFB0BEC5)  // 선택 안 된 글씨색
                ),
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
