package com.gullielli.gebtechnicalassignment.model

data class QuoteResponse(

    val contents: Contents,
    val success: Success
)

data class Success(

    val total: Int
)

data class Contents(

    val copyright: String,
    val quotes: List<Quote>
)

// the quote response includes a lot more information than what's provided below
// only using what I need for this assignment
data class Quote(

    val author: String,
    val quote: String,
    val title: String
)