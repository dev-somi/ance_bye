//package com.example.myapplication.data.local.db
//
//import android.database.sqlite.SQLiteDatabase
//import com.example.myapplication.data.model.Product
//
//class ProductDao(private val db: SQLiteDatabase) {
//
//    fun getProductsByIngredient(keyword: String): List<Product> {
//        val products = mutableListOf<Product>()
//
//        val cursor = db.rawQuery(
//            """
//            SELECT
//                dp.item_name,
//                dp.need_doctor,
//                do.item_image
//            FROM product_ingredient pi
//            JOIN drug_product dp ON dp.item_seq = pi.item_seq
//            JOIN ingredient i ON i.ingredient_id = pi.ingredient_id
//            LEFT JOIN drug_overview do ON do.item_seq = dp.item_seq
//            WHERE lower(i.gnl_nm) LIKE ?
//            ORDER BY dp.item_name
//            """,
//            arrayOf("${keyword.lowercase()}%")
//        )
//
//        cursor.use {
//            while (it.moveToNext()) {
//                val name = it.getString(0)
//                val needDoctor = it.getInt(1) == 1
//                val imageUrl = it.getString(2) // ← 추가
//
//                products.add(
//                    Product(
//                        name = name,
//                        description = "",
//                        isPrescriptionRequired = needDoctor,
//                        matchingIngredient = keyword,
//                        imageUrl = imageUrl
//                    )
//                )
//            }
//        }
//
//        return products
//    }
//}
//
