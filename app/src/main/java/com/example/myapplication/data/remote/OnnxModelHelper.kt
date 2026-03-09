package com.example.myapplication.data.remote

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap

class OnnxModelHelper(context: Context) {

    private var env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private var session: OrtSession

    init {
        // assets에서 모델 읽기
        val assetManager = context.assets
        val modelBytes = assetManager.open("model.onnx").readBytes()
        session = env.createSession(modelBytes)
    }

    fun runInference(bitmap: Bitmap): FloatArray {
        // 이미지 전처리: [1, 3, 224, 224] float32 로 변환 (예시)
        val inputTensor = preprocessImage(bitmap)

        // 입력 이름 가져오기
        val inputName = session.inputNames.iterator().next()

        // 추론 실행
        val output = session.run(mapOf(inputName to OnnxTensor.createTensor(env, inputTensor)))

        // 결과 가져오기
        val result = (output[0].value as Array<FloatArray>)[0]
        output.close()
        return result
    }

    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val input = Array(1) { Array(3) { Array(224) { FloatArray(224) } } }

        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resized.getPixel(x, y)
                val r = ((pixel shr 16) and 0xFF) / 255.0f
                val g = ((pixel shr 8) and 0xFF) / 255.0f
                val b = (pixel and 0xFF) / 255.0f

                input[0][0][y][x] = r
                input[0][1][y][x] = g
                input[0][2][y][x] = b
            }
        }
        return input
    }
}