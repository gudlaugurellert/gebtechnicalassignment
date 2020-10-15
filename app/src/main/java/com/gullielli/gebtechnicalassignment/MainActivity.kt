package com.gullielli.gebtechnicalassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gullielli.gebtechnicalassignment.repo.Repository

class MainActivity : AppCompatActivity() {

    // TODO CREATE BASE ACTIVITY


    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.getQuotes()

        viewModel.quoteResponse.observe(this, Observer { response ->
            // lets see if this works
            if (response.isSuccessful) {
                Log.d("reply", response.body()?.contents?.quotes?.get(0).toString())
            } else {
                Log.d("reply", "failed..")
            }
        })
    }
}