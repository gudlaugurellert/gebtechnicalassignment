package com.gullielli.gebtechnicalassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gullielli.gebtechnicalassignment.repo.Repository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // TODO CREATE BASE ACTIVITY

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteTV.text = getString(R.string.click_button_below)

        getRandomQuoteBtn.setOnClickListener {
            println("random btn clicked")
        }

        emailQuoteBtn.setOnClickListener {
            println("email btn clicked")
        }

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.mainMVGetRandomQuote("management")

        viewModel.randomQuoteResponse.observe(this, Observer { response ->

            if (response.isSuccessful) {
                Log.d("reply", response.body()?.contents?.quotes?.get(0).toString())
                Log.d("reply title", response.body()?.contents?.quotes?.get(0)?.title.toString())
                Log.d("reply quote", response.body()?.contents?.quotes?.get(0)?.quote.toString())
            } else {
                Log.d("reply", "failed..")
            }
        })
    }
}