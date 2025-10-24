package com.predictapp.data.model

data class NewsAnalysisResult(
    val summary: String = "",
    val prediction: String = "",
    val industryAnalysis: String = "",
    val marketTrend: String = "",
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