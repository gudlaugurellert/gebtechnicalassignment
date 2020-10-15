package com.gullielli.gebtechnicalassignment

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gullielli.gebtechnicalassignment.model.QuoteResponse
import com.gullielli.gebtechnicalassignment.repo.Repository
import com.gullielli.gebtechnicalassignment.util.CheckNetwork
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // TODO CREATE BASE ACTIVITY

    private lateinit var viewModel: MainViewModel

    // Quote of the Day categories
    // TODO IF TIME, MAKE IT NOT HARDCODED
    private val categories = listOf("inspire","management","sports","life","funny","love","art","students")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteTV.text = getString(R.string.click_button_below)
        titleTV.text = ""
        authorTV.text = ""

        val doWeHaveInternetAccess = CheckNetwork()
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        var internetCheck: Boolean

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        getRandomQuoteBtn.setOnClickListener {

            internetCheck = doWeHaveInternetAccess.isNetworkAvail(this)

            if(!internetCheck) {
                noInternetAccess()
            } else {
                getNewRandomQuote(categories.random())
            }
        }

        emailQuoteBtn.setOnClickListener {
            // do something
            // call the phone's mail client and send email there?
            // ...or send email from within the app... decisions decisions...
        }

        viewModel.randomQuoteResponse.observe(this, Observer { response ->

            if (response.isSuccessful) {
                successfulResponse(response)
            } else {
                failedResponse()
            }
        })
    }

    private fun noInternetAccess() {
        titleTV.text = ""
        quoteTV.setTextColor(Color.BLACK)
        quoteTV.text = getString(R.string.error_no_internet_access)
        authorTV.text = ""
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

    private fun failedResponse() {
        quoteTV.text = getString(R.string.something_went_wrong)
    }
}