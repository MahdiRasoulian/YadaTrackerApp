package com.example.yadatracker.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yadatracker.R
import org.json.JSONObject
import java.text.DecimalFormat

class GoldExchangeFragment : Fragment() {

    // Declare TextViews for Exchange Rates, ordered for UI
    private lateinit var tvEuroPrice: TextView
    private lateinit var tvTurkishLiraPrice: TextView
    private lateinit var tvJapaneseYenPrice: TextView
    private lateinit var tvBritishPoundPrice: TextView
    private lateinit var tvAustralianDollarPrice: TextView
    private lateinit var tvCanadianDollarPrice: TextView
    private lateinit var tvChineseRenminbiPrice: TextView
    private lateinit var tvSwissFrancPrice: TextView


    // Button
    private lateinit var btnRefreshExchange: Button

    // API URL - This will need to be updated to your Python backend endpoint for exchange rates
    private val API_URL = "https://YADATrade.pythonanywhere.com/market-data"
    private val TAG = "GoldExchangeFragment" // Keeping original TAG name as per instruction

    // Formatters for displaying numbers
    private val priceFormatter = DecimalFormat("#,##0.0000") // More decimal places for currency


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gold_exchange, container, false)

        // Initialize TextViews by finding them in the layout, ordered for UI
        tvEuroPrice = view.findViewById(R.id.tvEuroPrice)
        tvTurkishLiraPrice = view.findViewById(R.id.tvTurkishLiraPrice)
        tvJapaneseYenPrice = view.findViewById(R.id.tvJapaneseYenPrice)
        tvBritishPoundPrice = view.findViewById(R.id.tvBritishPoundPrice)
        tvAustralianDollarPrice = view.findViewById(R.id.tvAustralianDollarPrice)
        tvCanadianDollarPrice = view.findViewById(R.id.tvCanadianDollarPrice)
        tvChineseRenminbiPrice = view.findViewById(R.id.tvChineseRenminbiPrice)
        tvSwissFrancPrice = view.findViewById(R.id.tvSwissFrancPrice)


        // Initialize Button
        btnRefreshExchange = view.findViewById(R.id.btnRefreshExchange)

        // Set up refresh button click listener to fetch new data
        btnRefreshExchange.setOnClickListener {
            Log.d(TAG, "Refresh button clicked. Fetching exchange data.")
            fetchExchangeData()
        }

        // Fetch initial exchange data when the fragment is created
        fetchExchangeData()

        return view
    }

    /**
     * Fetches exchange data from the specified API URL using Volley.
     * Handles successful responses by updating the UI and errors by showing toasts.
     */
    private fun fetchExchangeData() {
        // Get a new request queue for network operations
        val requestQueue = Volley.newRequestQueue(requireContext())
        Log.d(TAG, "Attempting to fetch exchange data from URL: $API_URL")

        // Create a StringRequest to fetch data as a string
        val stringRequest = StringRequest(
            Request.Method.GET, API_URL,
            { response ->
                // This block is executed on a successful API response
                Log.d(TAG, "API Response received: $response")
                try {
                    // Parse the response string into a JSONObject
                    val jsonResponse = JSONObject(response)
                    // Update the UI with the parsed JSON data
                    updateUI(jsonResponse)
                    Toast.makeText(requireContext(), "Exchange data updated successfully!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    // Handle JSON parsing errors
                    Log.e(TAG, "Error parsing exchange data (JSON): ${e.message}", e)
                    Toast.makeText(requireContext(), "Error parsing exchange data: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace() // Print stack trace for debugging
                }
            },
            { error ->
                // This block is executed if there's a network or API error
                var errorMessage = "Network error: " + (error.message ?: "Unknown error")
                if (error.networkResponse != null) {
                    // If a network response is available, get status code and data
                    val statusCode = error.networkResponse.statusCode
                    val responseData = String(error.networkResponse.data)
                    errorMessage += "\nStatus Code: $statusCode"
                    errorMessage += "\nResponse Data: $responseData"
                    Log.e(TAG, "Volley Error - Status: $statusCode, Data: $responseData", error)
                } else {
                    // If no network response, log the error message
                    Log.e(TAG, "Volley Error - No network response. Message: ${error.message}", error)
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                error.printStackTrace() // Print stack trace for debugging
            }
        )

        // Set a retry policy for the request (10 seconds timeout, default retries/backoff)
        val socketTimeout = 10000 // 10 seconds
        val retryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy

        // Add the request to the Volley queue to be executed
        requestQueue.add(stringRequest)
        Log.d(TAG, "Volley request added to queue.")
    }

    /**
     * Updates the UI with exchange rate data received from the API response.
     * This function assumes your backend will provide exchange rates in a suitable structure.
     */
    private fun updateUI(data: JSONObject) {
        val exchangeRates = data.optJSONObject("exchange_rates")

        if (exchangeRates != null) {
            // USD/EUR (1 USD = X EUR)
            val usdEurData = exchangeRates.optJSONObject("USD_EUR")
            updateExchangeItemUI(usdEurData?.optDouble("price", Double.NaN), "EUR", tvEuroPrice)

            // USD/TRY (1 USD = X TRY)
            val usdTryData = exchangeRates.optJSONObject("USD_TRY")
            updateExchangeItemUI(usdTryData?.optDouble("price", Double.NaN), "TRY", tvTurkishLiraPrice)

            // USD/JPY (1 USD = X JPY)
            val usdjpyData = exchangeRates.optJSONObject("USD_JPY")
            updateExchangeItemUI(usdjpyData?.optDouble("price", Double.NaN), "JPY", tvJapaneseYenPrice)

            // USD/GBP (1 USD = X GBP)
            val usdGbpData = exchangeRates.optJSONObject("USD_GBP")
            updateExchangeItemUI(usdGbpData?.optDouble("price", Double.NaN), "GBP", tvBritishPoundPrice)

            // USD/AUD (1 USD = X AUD)
            val usdAudData = exchangeRates.optJSONObject("USD_AUD")
            updateExchangeItemUI(usdAudData?.optDouble("price", Double.NaN), "AUD", tvAustralianDollarPrice)

            // USD/CAD (1 USD = X CAD)
            val usdCadData = exchangeRates.optJSONObject("USD_CAD")
            updateExchangeItemUI(usdCadData?.optDouble("price", Double.NaN), "CAD", tvCanadianDollarPrice)

            // USD/CNY (1 USD = X CNY)
            val usdCnyData = exchangeRates.optJSONObject("USD_CNY")
            updateExchangeItemUI(usdCnyData?.optDouble("price", Double.NaN), "CNY", tvChineseRenminbiPrice)

            // USD/CHF (1 USD = X CHF)
            val usdChfData = exchangeRates.optJSONObject("USD_CHF")
            updateExchangeItemUI(usdChfData?.optDouble("price", Double.NaN), "CHF", tvSwissFrancPrice)

        } else {
            Log.e(TAG, "'exchange_rates' object not found or is null in response.")
            setDefaultExchangeUI()
        }
    }

    /**
     * Helper function to update the price TextView for a single exchange rate item.
     *
     * @param price The price value. Can be Double.NaN if not available.
     * @param unit The currency unit to display (e.g., "EUR", "JPY", "TRY").
     * @param priceTextView The TextView to display the price.
     */
    private fun updateExchangeItemUI(
        price: Double?,
        unit: String,
        priceTextView: TextView
    ) {
        if (price != null && !price.isNaN()) {
            priceTextView.text = "${priceFormatter.format(price)} $unit"
        } else {
            priceTextView.text = "-- $unit"
        }
    }

    /**
     * Helper function to set all exchange rate TextViews to default values.
     * Called when the 'exchange_rates' object is not found in the API response.
     */
    private fun setDefaultExchangeUI() {
        updateExchangeItemUI(Double.NaN, "EUR", tvEuroPrice)
        updateExchangeItemUI(Double.NaN, "TRY", tvTurkishLiraPrice)
        updateExchangeItemUI(Double.NaN, "JPY", tvJapaneseYenPrice)
        updateExchangeItemUI(Double.NaN, "GBP", tvBritishPoundPrice)
        updateExchangeItemUI(Double.NaN, "AUD", tvAustralianDollarPrice)
        updateExchangeItemUI(Double.NaN, "CAD", tvCanadianDollarPrice)
        updateExchangeItemUI(Double.NaN, "CNY", tvChineseRenminbiPrice)
        updateExchangeItemUI(Double.NaN, "CHF", tvSwissFrancPrice)
    }
}
