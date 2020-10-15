package com.gullielli.gebtechnicalassignment.repo

import com.gullielli.gebtechnicalassignment.api.APIManager
import com.gullielli.gebtechnicalassignment.model.QuoteResponse
import retrofit2.Response

class Repository {

//    suspend fun getQuotes(): Response<QuoteResponse> {
//        return APIManager.api.getQuotes()
//    }

    suspend fun repoGetRandomQuote(category: String?): Response<QuoteResponse?> {
        return APIManager.api.apiGetRandomQuote(category)
    }
}