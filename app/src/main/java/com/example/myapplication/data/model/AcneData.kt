package com.example.myapplication.data.model

import com.example.myapplication.R

// =================================================================
// 1. [구조 정의] 데이터들의 뼈대 (수정 금지)
// =================================================================

data class Product(
    val name: String,
    val description: String,
    val isPrescriptionRequired: Boolean,
    val matchingIngredient: String,
    val imageResId: Int? = null
)

data class AcneType(
    val title: String,
    val subtitle: String,
    val resultScreen_Title : String,
    val description: String,
    val resultScreen_Description : String,
    val treatmentGuides: List<TreatmentGuide>,
    val imageResId: Int
)

data class TreatmentGuide(
    val goalName: String,
    val ingredients: List<String>
)

// 진단 결과 데이터 틀 (색상은 Long, 비용 관련 필드 추가됨)
data class DiagnosisResult(
    val level: String,
    val title: String,
    val colorHex: Long,          // 0xFF...L (Long 타입)
    val description: String,
    val recommendationTitle: String,
    val recommendationBody: String,
    val costTitle: String,
    val costBody: String
)

// =================================================================
// 2. [창고] 전체 제품 데이터베이스
// =================================================================
val allProductsDB = listOf(
    // [국소 레티노이드]
    Product(
        "디페린 겔 0.1%",
        "아다팔렌 성분, 3세대 레티노이드",
        true,
        "국소 레티노이드",
        R.drawable.img_differin // 예: res/drawable/img_differin.png
    ),

    // [살리실산 (BHA)]
    Product(
        "클리어틴 외용액",
        "블랙헤드/화이트헤드 용해",
        false,
        "살리실산 (BHA)",
        R.drawable.img_clearteen // 사진 파일명에 맞게 수정하세요
    ),
    Product(
        "스트라이덱스 패드",
        "간편한 각질 제거 패드",
        false,
        "살리실산 (BHA)",
        R.drawable.img_stridex
    ),

    // [아젤라산]
    Product(
        "아젤리아 크림",
        "염증 완화 및 색소 침착 개선",
        true,
        "아젤라산",
        R.drawable.img_azelia
    ),

    // [벤조일 퍼옥사이드]
    Product("벤작 AC 겔", "여드름 균 살균 및 면포 용해", false, "벤조일 퍼옥사이드", R.drawable.img_benzac),
    Product("톡리어 겔", "가벼운 제형의 살균제", false, "벤조일 퍼옥사이드", R.drawable.img_toclear),

    // [국소 클라스코테론]
    Product("윈레비 (Winlevi) 크림", "국소 안드로겐 억제제 (피지 조절)", true, "국소 클라스코테론", R.drawable.img_winlevi), // 사진 없으면 null 또는 생략

    // [국소 항생제]
    Product("크레오신 티", "클린다마이신 성분, 여드름 균 증식 억제", true, "국소 항생제", R.drawable.img_cleocin),

    // [복합제]
    Product("에피듀오 겔", "아다팔렌 + 벤조일 퍼옥사이드 복합제", true, "국소 레티노이드 + BP 복합제", R.drawable.img_epiduo),
    Product("듀악겔", "클린다마이신 + 벤조일 퍼옥사이드", true, "국소 항생제 + BP 복합제", R.drawable.img_duac),

    // [경구 약물]
    Product("독시사이클린 정", "중등도 염증 완화 항생제", true, "경구 독시사이클린", R.drawable.img_doxy),
    Product("미노씬 캡슐", "미노사이클린 성분 항생제", true, "경구 미노사이클린", R.drawable.img_minocin),
    Product("로아큐탄 (이소티논)", "강력한 피지 억제제 (임산부 금기)", true, "경구 이소트레티노인", R.drawable.img_isotinon),

    // [전문 시술/주사]
    Product("트리암시놀론 주사", "병변 내 직접 주사 (염증 급속 완화)", true, "병변 내 코르티코스테로이드 주사", R.drawable.img_triamcinolone),

    // [호르몬 조절]
    Product("알닥톤 정", "스피로노락톤 성분 (안드로겐 억제)", true, "스피로노락톤", R.drawable.img_aldactone),
    Product("경구 피임약 (야즈)", "호르몬 불균형 조절", true, "복합 경구 피임약 (COCs)", R.drawable.img_yaz)
)

// =================================================================
// 3. [메뉴판] 여드름 가이드 리스트 (의학적 근거 반영 완료)
// =================================================================
val acneGuideList = listOf(
    // ---------------- [1. 개방형 면포] ----------------
    AcneType(
        title = "개방형 면포 (Blackhead)",
        subtitle = "비염증성",
        resultScreen_Title = "블랙헤드",
        description = "모공 입구가 열려 피지와 각질이 공기에 노출되어 산화되어 검게 보이는 초기 여드름입니다.",
        resultScreen_Description = "모공 속 과도하게 분비된 피지, 각질, 노폐물이 뭉쳐 모공 입구에 쌓이고, 공기와 접촉하여 산화되면서 까맣게 변한 형태입니다.",
        treatmentGuides = listOf(
            TreatmentGuide("각질 제거", listOf("국소 레티노이드", "살리실산 (BHA)", "아젤라산")),
            TreatmentGuide("항균", listOf("벤조일 퍼옥사이드"))
        ),
        imageResId = R.drawable.blackhead
    ),

    // ---------------- [2. 폐쇄형 면포] ----------------
    AcneType(
        title = "폐쇄형 면포 (Whitehead)",
        subtitle = "비염증성",
        resultScreen_Title = "화이트헤드(좁쌀여드름)",
        description = "모공 입구가 닫혀 피지와 각질이 피부 아래에 갇혀 하얗게 솟아 보이는 여드름입니다. 염증성으로 발전하기 전 조기 관리가 권장됩니다.",
        resultScreen_Description = "모공 입구가 각질과 피지로 막혀 피부 속에 하얗게 갇혀 있는 형태입니다. 만졌을 때 아프지는 않지만, 피부 속에 무언가 만져지거나, 빛 아래에서 피부를 당겼을 때 보일 수 있습니다.",
        treatmentGuides = listOf(
            TreatmentGuide("각질 제거", listOf("국소 레티노이드", "아젤라산", "살리실산 (BHA)")),
            TreatmentGuide("피지 분비 억제", listOf("국소 클라스코테론")),
            TreatmentGuide("항균", listOf("벤조일 퍼옥사이드", "국소 항생제"))
        ),
        imageResId = R.drawable.whitehead
    ),

    // ---------------- [3. 구진] ----------------
    AcneType(
        title = "구진 (Papules)",
        subtitle = "염증성",
        resultScreen_Title = "구진성 여드름(화농성 여드름)",
        description = "면포에 세균(C. acnes) 증식과 염증 반응이 더해져 붉게 솟아오르는 병변입니다. 통증과 붉은 기를 동반합니다.",
        resultScreen_Description = "막힌 모공 속 피지(화이트헤드)에 염증이 생겨 피부 표면으로 붉고 단단하게 솟아오른 형태입니다. 고름은 없지만 만지면 통증이 느껴질 수 있는 초기 염증성 여드름입니다",
        treatmentGuides = listOf(
            TreatmentGuide("항균 + 항염 + 면포 용해", listOf("국소 레티노이드 + BP 복합제", "국소 항생제 + BP 복합제", "국소 레티노이드 + 국소 항생제 복합제")),
            TreatmentGuide("전신 항생제 (중등증)", listOf("경구 독시사이클린", "경구 미노사이클린")),
            TreatmentGuide("항염증 및 색소 완화", listOf("아젤라산")),
            TreatmentGuide("급성 염증 관리", listOf("병변 내 코르티코스테로이드 주사")),
            TreatmentGuide("표준 치료 실패 시", listOf("경구 이소트레티노인"))
        ),
        imageResId = R.drawable.papule
    ),

    // ---------------- [4. 농포] ----------------
    AcneType(
        title = "농포 (Pustules)",
        subtitle = "염증성",
        resultScreen_Title = "농포성 여드름(화농성 여드름)",
        description = "구진 단계에서 염증이 심화되어 고름(pus)이 차고 곪은 형태입니다. 흉터 위험이 높으므로 적극적인 치료가 필요합니다.",
        resultScreen_Description = "구진성 여드름에서 염증이 심해져 모공 속에 피지, 죽은 세포, 박테리아가 뭉쳐 고름(농)이 차 있는, 붉게 부어오른 둥근 형태의 여드름입니다.",
        treatmentGuides = listOf(
            TreatmentGuide("복합 요법 (항균 + 항염)", listOf("국소 레티노이드 + BP 복합제", "국소 항생제 + BP 복합제")),
            TreatmentGuide("전신 항생제 (중등증)", listOf("경구 독시사이클린", "경구 미노사이클린")),
            TreatmentGuide("항염증 및 색소 완화", listOf("아젤라산")),
            TreatmentGuide("급성 염증 관리", listOf("병변 내 스테로이드 주사")),
            TreatmentGuide("표준 치료 실패 시", listOf("경구 이소트레티노인"))
        ),
        imageResId = R.drawable.pastule
    ),

    // ---------------- [5. 결절/낭종] ----------------
    AcneType(
        title = "결절/낭종 (Nodules/Cysts)",
        subtitle = "심각한 염증성",
        resultScreen_Title = "결절/낭종성 여드름",
        description = "피부 깊숙한 곳에 발생하는 크고 단단한 병변으로, 영구적인 흉터를 남길 수 있습니다. 강력한 전신 치료가 필요합니다.",
        resultScreen_Description = "피부 진피층 속 깊이 자리잡은, 만졌을 때 속에서 느껴지는 크고 단단한 여드름입니다. 붉게 부어오르거나 검붉은색을 띄며 고름을 동반할 수 있습니다",
        treatmentGuides = listOf(
            TreatmentGuide("강력한 피지 조절", listOf("경구 이소트레티노인")),
            TreatmentGuide("전신 항생제", listOf("경구 독시사이클린", "경구 미노사이클린", "경구 사레사이클린")),
            TreatmentGuide("호르몬 조절 (여성)", listOf("스피로노락톤", "복합 경구 피임약 (COCs)")),
            TreatmentGuide("급성 염증 관리", listOf("병변 내 코르티코스테로이드 주사"))
        ),
        imageResId = R.drawable.nodule
    )
)

// =================================================================
// 4. [진단 결과] 진단 결과 데이터 (비용 및 권고안 업데이트)
// =================================================================
val diagnosisList = listOf(
    DiagnosisResult(
        level = "Mild",
        title = "경증 (Mild Acne)",
        colorHex = 0xFF00C853L, // 초록색
        description = "주로 비염증성 여드름(블랙헤드, 화이트헤드)으로 구성된 초기 단계입니다. 흉터 발생 위험은 낮으나, 염증성 여드름으로 발전하거나 심리적 고통을 유발할 수 있으므로 조기에 적절한 관리가 권장됩니다.",
        recommendationTitle = "홈케어 및 조기 국소 치료 권장",
        recommendationBody = "비알칼리성(skin pH neutral 또는 약산성) 세정 제품을 하루 두 번 사용하고, 오일 기반이나 면포 유발성이 있는(comedogenic) 제품을 피하십시오. 국소 레티노이드 또는 벤조일 퍼옥사이드가 포함된 복합 국소 치료제를 고려하며, 증상이 악화되면 전문의 상담을 고려해야 합니다.",
        costTitle = "예상 비용 (의원급 기준)",
        costBody = "초진: 4,500원 / 재진: 3,300원\n(약제비 별도, 비급여 시술 제외)"
    ),
    DiagnosisResult(
        level = "Moderate",
        title = "중등증 (Moderate Acne)",
        colorHex = 0xFFFF9800L, // 주황색
        description = "염증성 여드름(구진성, 농포성)이 두드러지며 흉터 발생 위험이 증가하는 단계입니다. 피부과 진료가 권장됩니다.",
        recommendationTitle = "경구 항생제를 포함한 복합 치료 필수",
        recommendationBody = "중등증에서 중증 여드름에는 국소 복합 요법과 경구 독시사이클린 등 항생제를 병용하는 12주 과정의 치료가 권장됩니다. 항생제 내성 위험을 줄이기 위해 경구 항생제는 일반적으로 6개월을 초과하지 않는 최단 기간 동안 사용되어야 하며, 반드시 국소 벤조일 퍼옥사이드와 같은 다른 국소 치료와 병용해야 합니다.",
        costTitle = "예상 진료비 (의원~병원급)",
        costBody = "초진: 4,500원 ~ 6,200원\n재진: 3,300원 ~ 4,500원\n+ 약값 및 검사비: 약 1~2만원 추가 예상"
    ),
    DiagnosisResult(
        level = "Severe",
        title = "중증 (Severe Acne)",
        colorHex = 0xFFD50000L, // 빨간색
        description = "염증성 여드름(구진성, 농포성)과 결절성 여드름이 많은 심각한 상태입니다. 영구적인 흉터가 생길 가능성이 높기 때문에 피부과 진료를 적극 권장합니다.",
        recommendationTitle = "전문의 의뢰 및 경구 이소트레티노인 고려",
        recommendationBody = "경구 이소트레티노인 치료가 강력히 권고됩니다. 여드름의 심각도와 관계없이 지속적인 심리적 고통을 유발하는 경우에도 전문의에게 의뢰해야 합니다.",
        costTitle = "예상 진료비 (종합~상급종합병원)",
        costBody = "초진: 8,700원 ~ 11,400원\n재진: 6,500원 ~ 8,900원\n+ 약값/처치비 별도 (총 3~5만원 이상 예상)"
    )
)

