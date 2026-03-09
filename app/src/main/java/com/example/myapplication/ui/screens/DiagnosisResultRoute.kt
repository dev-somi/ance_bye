package com.example.myapplication.ui.screens

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.example.myapplication.data.model.diagnosisList
import com.example.myapplication.ui.screens.DiagnosisResultScreen

@Composable
fun DiagnosisResultRoute(
    navController: NavHostController
) {
    val previousBackStackEntry = navController.previousBackStackEntry

    // 1. 비트맵 꺼내기
    val userBitmap = previousBackStackEntry
        ?.savedStateHandle
        ?.get<Bitmap>("fullFaceBitmap")

    // 2. 진단 레벨 문자열 꺼내기 (예: "Mild", "Severe" 등)
    val diagnosisLevelString = previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("diagnosisLevel") ?: "Moderate"

    // 3. [핵심] 문자열과 일치하는 데이터 객체 찾기
    // (만약 못 찾으면 기본값으로 Moderate 데이터 사용)
    val resultData = remember(diagnosisLevelString) {
        diagnosisList.find { it.level == diagnosisLevelString }
            ?: diagnosisList.find { it.level == "Moderate" }!!
    }

    // 4. 화면에 데이터 객체(resultData)를 통째로 전달
    DiagnosisResultScreen(
        userImageBitmap = userBitmap,
        resultData = resultData, // <--- 수정된 부분
        onBackClick = {
            navController.popBackStack()
        },
        onRescanClick = {
            navController.popBackStack()
        },
        onConfirmClick = {
            navController.navigate("home") {
                popUpTo("home") { inclusive = false }
            }
        }
    )
}