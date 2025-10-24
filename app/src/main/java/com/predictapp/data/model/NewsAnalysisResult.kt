package com.predictapp.data.model

data class NewsAnalysisResult(
    val prediction: String = "",
    val success: Boolean = true,
    val message: String? = null
) {
    companion object {
        fun error(message: String): NewsAnalysisResult {
            return NewsAnalysisResult(
                success = false,
                message = message
            )
        }
    }
}