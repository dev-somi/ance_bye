package com.example.myapplication.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.local.HistoryViewModel

@Preview(showBackground = true, name = "홈 화면")
@Composable
fun HomeScreenPreview() {
    val dummyNavController = rememberNavController()
    HomeScreen(navController = dummyNavController)
}

@Preview(showBackground = true, name = "스캔 화면")
@Composable
fun ScanScreenPreview() {
    val dummyNavController = rememberNavController()
    ScanScreen(navController = dummyNavController)
}

@Preview(showBackground = true, name = "기록 화면")
@Composable
fun HistoryScreenPreview() {
    val dummyViewModel = HistoryViewModel()
    val navController = rememberNavController()

    HistoryScreen(
        viewModel = dummyViewModel,
        navController = navController
    )
}

@Preview(showBackground = true, name = "가이드 화면")
@Composable
fun ProfileScreenPreview() {
    GuideScreen()
}
