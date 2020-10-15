package com.gullielli.gebtechnicalassignment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gullielli.gebtechnicalassignment.model.QuoteResponse
import com.gullielli.gebtechnicalassignment.repo.Repository
import com.gullielli.gebtechnicalassignment.util.CheckNetwork
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_prompt.view.*
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    // TODO CREATE BASE ACTIVITY

    private lateinit var viewModel: MainViewModel
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

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
        var internetCheck: Boolean

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        getRandomQuoteBtn.setOnClickListener {

            internetCheck = doWeHaveInternetAccess.isNetworkAvail(this)

            if(!internetCheck) {
                noInternetAccess()
            } else  {
                getNewRandomQuote(categories.random())
            }
        }

        emailQuoteBtn.setOnClickListener {
            // do something
            // call the phone's mail client and send email there?
            // ...or send email from within the app... decisions decisions...
            internetCheck = doWeHaveInternetAccess.isNetworkAvail(this)

            if(!internetCheck) {
                noInternetAccess()
            } else if (authorTV.equals("")) {
                failedResponse()
            } else {
                addEmailAddresses()
            }
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

    private fun addEmailAddresses()  {
        var email = ""

        //Inflate the dialog with custom alert prompt
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_prompt, null)
        mDialogView.emailAddressToSend.text = ""

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Add email")

        //show dialog
        val mAlertDialog = mBuilder.show()

        mDialogView.dialogAddEmailBtn.setOnClickListener {

            // Super basic validation to check if there are any illegal characters in the email address
            if (mDialogView.dialogEmailET.text.toString().matches(emailPattern.toRegex())) {

                email += mDialogView.dialogEmailET.text.toString()+"\n"
                mDialogView.emailAddressToSend.text = email

                mDialogView.dialogEmailET.text.clear()
                // Toast - Don't need to show this message - it's annoying from a user point of view
//                val t = Toast.makeText(this, "Valid email address", Toast.LENGTH_LONG)
//                t.setGravity(Gravity.CENTER, 0, 0)
//                t.show()

            } else {

                val t = Toast.makeText(this, "Invalid email address", Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            }
        }

        // Done button
        mDialogView.dialogDoneBtn.setOnClickListener {

            // If there are no email addresses, then do nothing and display a toast..
            // If there are email address, then open email client and pass the email(s), quote title, quote text and quote author.
            if(!email.equals("")) {
                openEmailClient(email, titleTV.text as String, quoteTV.text as String, authorTV.text as String)
            } else {
                val t = Toast.makeText(this, "Don't forget to enter an email address", Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            }
        }

        // Cancel button - dismiss the dialog
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    // Not the most elegant way to do this but running out of time.
    private fun openEmailClient(emailAddresses: String, emailSubject: String, emailQuote: String, emailQuoteAuthor: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto: Example@gmail.com")
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, "test")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hello World")
        intent.putExtra(Intent.EXTRA_TEXT, "text body")

        startActivity(intent)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            //Log.d("Email error:", e.toString())
            val t = Toast.makeText(this, "Something went wrong opening your email client..", Toast.LENGTH_LONG)
            t.setGravity(Gravity.CENTER, 0, 0)
            t.show()
        }
    }
}