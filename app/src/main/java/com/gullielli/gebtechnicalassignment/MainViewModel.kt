package com.gullielli.gebtechnicalassignment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gullielli.gebtechnicalassignment.model.QuoteResponse
import com.gullielli.gebtechnicalassignment.repo.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository) : ViewModel() {

    val quoteResponse: MutableLiveData<Response<QuoteResponse>> = MutableLiveData()

    fun getQuotes() {
        viewModelScope.launch {

            val response = repository.getQuotes()
            quoteResponse.value = response
        }
    }
}