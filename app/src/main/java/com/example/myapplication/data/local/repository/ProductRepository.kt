//package com.example.myapplication.data.local.repository
//
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import com.example.myapplication.data.local.db.DrugDbHelper
//import com.example.myapplication.data.local.db.ProductDao
//import com.example.myapplication.data.model.Product
//
//class ProductRepository(context: Context) {
//
//    private val db: SQLiteDatabase =
//        DrugDbHelper(context).openDatabase()
//    private val productDao = ProductDao(db)
//
//    fun getProductsByIngredient(keyword: String): List<Product> {
//        return productDao.getProductsByIngredient(keyword)
//    }
//
//    fun getImageMapByNames(names: List<String>): Map<String, String?> {
//        if (names.isEmpty()) return emptyMap()
//
//        val placeholders = names.joinToString(",") { "?" }
//
//        val cursor = db.rawQuery(
//            """
//        SELECT dp.item_name, do.item_image
//        FROM drug_product dp
//        LEFT JOIN drug_overview do
//            ON dp.item_seq = do.item_seq
//        WHERE dp.item_name IN ($placeholders)
//        """,
//            names.toTypedArray()
//        )
//
//        val map = mutableMapOf<String, String?>()
//        while (cursor.moveToNext()) {
//            map[cursor.getString(0)] = cursor.getString(1)
//        }
//        cursor.close()
//        return map
//
//    }
//}
//
