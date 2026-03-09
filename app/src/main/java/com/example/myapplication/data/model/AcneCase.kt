package com.example.myapplication.data.model

data class AcneCase(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String,            // 예: "구진", "농포"
    val initialPhoto: String?,    // 최초 사진
    val latestPhoto: String?,     // 최신 사진
    val createdAt: Long,          // 최초 진단 시각
    val updatedAt: Long           // 마지막 업데이트 시각
)
