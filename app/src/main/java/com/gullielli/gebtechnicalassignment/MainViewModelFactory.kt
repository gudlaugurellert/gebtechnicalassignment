package com.gullielli.gebtechnicalassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gullielli.gebtechnicalassignment.repo.Repository

class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return MainViewModel(repository) as T
    }
}