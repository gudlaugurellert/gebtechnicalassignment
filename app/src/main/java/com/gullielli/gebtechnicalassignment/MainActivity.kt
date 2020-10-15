package com.gullielli.gebtechnicalassignment

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gullielli.gebtechnicalassignment.model.QuoteResponse
import com.gullielli.gebtechnicalassignment.repo.Repository
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // TODO CREATE BASE ACTIVITY

    private lateinit var viewModel: MainViewModel

    private val categories = listOf("inspire","management","sports","life","funny","love","art","students")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteTV.text = getString(R.string.click_button_below)
        titleTV.text = ""
        authorTV.text = ""

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        getRandomQuoteBtn.setOnClickListener {
            getNewRandomQuote(categories.random())
        }

        emailQuoteBtn.setOnClickListener {
            // do something
        }

        viewModel.randomQuoteResponse.observe(this, Observer { response ->

            if (response.isSuccessful) {
                successfulResponse(response)
            } else {
                failedResponse(response)
            }
        })
    }

    private fun getNewRandomQuote(categories: String) {
        titleTV.text = ""
        quoteTV.setTextColor(Color.RED)
        quoteTV.text = getString(R.string.fetching_new_quote)
        authorTV.text = ""
        viewModel.mainMVGetRandomQuote(categories)
    }

    private fun successfulResponse(response: Response<QuoteResponse?>) {
        quoteTV.setTextColor(Color.BLUE)
        titleTV.text = response.body()?.contents?.quotes?.get(0)?.title.toString()
        quoteTV.text = response.body()?.contents?.quotes?.get(0)?.quote.toString()
        authorTV.text = response.body()?.contents?.quotes?.get(0)?.author.toString()
    }

    private fun failedResponse(response: Response<QuoteResponse?>) {
        quoteTV.text = getString(R.string.something_went_wrong)
    }
}