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

    private lateinit var viewModel: MainViewModel

    // Using this to check for illegal characters when entering email address
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    // Quote of the Day categories
    private val categories = listOf("inspire","management","sports","life","funny","love","art","students")

    // Using this flag to check if the user has pressed the get random quote first, before pressing the email this quote button
    // This flag will be true when there is a successful api call to get quotes
    private var quoteFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteTV.text = getString(R.string.click_button_below)

        // Clear the placeholder text
        titleTV.text = ""
        authorTV.text = ""

        val doWeHaveInternetAccess = CheckNetwork()
        var internetCheck: Boolean

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        // Get Random Quote button
        // ...first check if there is internet access
        // ...if there is internet, then go and get a random quote
        getRandomQuoteBtn.setOnClickListener {

            // Everytime the user presses the get quote button we check if there is internet access.
            internetCheck = doWeHaveInternetAccess.isNetworkAvail(this)

            if(!internetCheck) {

                // If there is no internet access, run this function
                // ...it simply just updates the text to say there's no internet..
                noInternetAccess()
            } else  {

                // There is internet so go ahead and get a new random quote.
                getNewRandomQuote(categories.random())
            }
        }

        // Email Quote Button
        // ...first check if there is internet access, or, if the quoteflag is false
        // ...quoteflag is set to true when there is a successful api call to get a quote for the first time
        // ...both of these need to be true in order to start the send email flow
        emailQuoteBtn.setOnClickListener {

            // Everytime the user presses the get email button we check if there is internet access.
            internetCheck = doWeHaveInternetAccess.isNetworkAvail(this)

            println("** author tv?? "+authorTV.text)

            // Check if there is no internet or if no quotes have been fetched yet..
            if(!internetCheck || !quoteFlag) {

                // If there is no internet access, run this function
                // ...it simply just updates the text to say there's no internet..
                if(!internetCheck) {
                    noInternetAccess()
                }

                // If no quotes have been fetched run this function
                // ... this lets the user know to press the get quotes button first
                if(!quoteFlag) {
                    somethingFailed("noQuoteFetched")
                }

            } else {
                // If both internetCheck and quoteFlag return true then go ahead and open the add email prompt
                addEmailAddresses()
            }
        }

        viewModel.randomQuoteResponse.observe(this, Observer { response ->

            if (response.isSuccessful) {
                quoteFlag = true
                successfulResponse(response)
            } else {
                somethingFailed("errorFetchingQuotes")
            }
        })
    }

    // if  there is no internet access then:
    // ..clear the title and author textfields
    // ..set the quote textfield color to black and let the user know there is no internet access
    private fun noInternetAccess() {

        titleTV.text = ""
        quoteTV.setTextColor(Color.BLACK)
        quoteTV.text = getString(R.string.error_no_internet_access)
        authorTV.text = ""
    }

    // This function gets passed a random category from the categories list defined at the top
    // the passed category is then used as a parameter to get the quote of the day details for that category
    // ... mainViewModel -> Repository -> APIManager -> retrofit -> API
    private fun getNewRandomQuote(categories: String) {

        titleTV.text = ""
        quoteTV.setTextColor(Color.RED)
        quoteTV.text = getString(R.string.fetching_new_quote)
        authorTV.text = ""
        viewModel.mainMVGetRandomQuote(categories)
    }

    // When getNewRandomQuote gets a successful response, this function receives the response
    // and updates the relevant textfields with the relevant response
    private fun successfulResponse(response: Response<QuoteResponse?>) {

        quoteTV.setTextColor(Color.BLUE)
        titleTV.text = response.body()?.contents?.quotes?.get(0)?.title.toString()
        quoteTV.text = response.body()?.contents?.quotes?.get(0)?.quote.toString()
        authorTV.text = response.body()?.contents?.quotes?.get(0)?.author.toString()
    }

    // This function updates the quote text with an appropriate error message to let the user know what happened
    private fun somethingFailed(sourceOfFailure: String) {

        when(sourceOfFailure) {
            "noQuoteFetched" -> quoteTV.text = getString(R.string.press_get_quotes_first)
            "errorFetchingQuotes" -> quoteTV.text = getString(R.string.something_went_wrong)
        }
    }

    // This function is called when the user presses the Send email button
    // It will open an alert dialog where the user can enter email addresses
    private fun addEmailAddresses() {

        // Using this to display the added email addresses in the alert dialog
        var email = ""

        // Using this to add all email addresses into as one big string
        // ...example: "email@address1.com,email@address2.com,email@address3.com"
        var emailListToSend = ""

        //Inflate the dialog with custom alert prompt
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_prompt, null)

        // Clear the placeholder text
        mDialogView.emailAddressToSend.text = ""

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle(R.string.add_email)

        val mAlertDialog = mBuilder.show()

        // When Add Email Button is pressed
        // ...first check if the email address contains illegal characters defined in emailPattern
        // .....if there are no illegal characters = take the text input and concatenate into email and emailListToSend variables
        // .....and clear the text input
        mDialogView.dialogAddEmailBtn.setOnClickListener {

            // Super basic validation to check if there are any illegal characters in the email address
            if (mDialogView.dialogEmailET.text.toString().matches(emailPattern.toRegex())) {

                email += mDialogView.dialogEmailET.text.toString()+"\n"

                emailListToSend += mDialogView.dialogEmailET.text.toString()+","

                mDialogView.emailAddressToSend.text = email

                mDialogView.dialogEmailET.text.clear()
                mDialogView.dialogAddEmailBtn.text = getString(R.string.add_another_email)

            } else {
                // If there are illegal chars in the email address then display this message

                // Known bug, and unfortunately don't have time to fix
                // A bug happens when entering .com.au email address, or any email addresses with a second level domain ...
                val t = Toast.makeText(this, "Invalid email address", Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            }
        }

        // When Done button is pressed
        // ...first check that the email variable is not emoty
        // .....if email is not empty, call the openEmailClient function and pass all the necessary information we need to send the email
        mDialogView.dialogDoneBtn.setOnClickListener {

            // If there is email address, then open email client and pass the email(s), quote title, quote text and quote author.
            if(!email.equals("")) {

                openEmailClient(emailListToSend, titleTV.text as String, quoteTV.text as String, authorTV.text as String)

            } else {
                // If there is no email addresses, then do nothing and display this message
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
    // Create the intent to open the phones' email client
    // ...put the data we want to pass into the email client = email addresses, quote title, quote text and quote author
    private fun openEmailClient(emailAddresses: String, emailSubject: String, emailQuote: String, emailQuoteAuthor: String) {

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$emailAddresses")
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        intent.putExtra(Intent.EXTRA_TEXT, "Quote:\n$emailQuote\n\nAuthor: $emailQuoteAuthor")

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
//            e.printStackTrace()
            val errorMsg = e.toString()
            val t = Toast.makeText(this, "Something went wrong.\n$errorMsg", Toast.LENGTH_LONG)
            t.setGravity(Gravity.CENTER, 0, 0)
            t.show()
        }
    }
}