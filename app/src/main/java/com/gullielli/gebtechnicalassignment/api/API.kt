package com.gullielli.gebtechnicalassignment.api

import com.gullielli.gebtechnicalassignment.model.QuoteResponse
import com.gullielli.gebtechnicalassignment.util.Constants.Companion.API_KEY
import com.gullielli.gebtechnicalassignment.util.Constants.Companion.API_KEY_VALUE
import com.gullielli.gebtechnicalassignment.util.Constants.Companion.QOD_CATEGORY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface API {
//    @Headers("$API_KEY:$API_KEY_VALUE")
//    @GET(QOD_CATEGORY)
//    suspend fun getQuotes(): Response<QuoteResponse>

    @Headers("$API_KEY:$API_KEY_VALUE")
    @GET(QOD_CATEGORY)
    suspend fun apiGetRandomQuote (@Query("category") category: String?): Response<QuoteResponse?>
}