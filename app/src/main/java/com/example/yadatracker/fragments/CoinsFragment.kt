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

class CoinsFragment : Fragment() {

    // Declare TextViews for Gold Coins & Metals
    private lateinit var tvIRGold18KPrice: TextView
    private lateinit var tvIRGold18KChange: TextView
    private lateinit var tvIRGold24KPrice: TextView
    private lateinit var tvIRGold24KChange: TextView
    private lateinit var tvIRCoinBaharPrice: TextView
    private lateinit var tvIRCoinBaharChange: TextView
    private lateinit var tvIRCoinEmamiPrice: TextView
    private lateinit var tvIRCoinEmamiChange: TextView
    private lateinit var tvIRCoinHalfPrice: TextView
    private lateinit var tvIRCoinHalfChange: TextView
    private lateinit var tvIRCoinQuarterPrice: TextView
    private lateinit var tvIRCoinQuarterChange: TextView
    private lateinit var tvIRCoin1GPrice: TextView
    private lateinit var tvIRCoin1GChange: TextView
    private lateinit var tvIRGoldMeltedPrice: TextView
    private lateinit var tvIRGoldMeltedChange: TextView

    // Button
    private lateinit var btnRefreshCoins: Button

    // API URL
    private val API_URL = "https://YADATrade.pythonanywhere.com/market-data"
    private val TAG = "CoinsFragment"

    // Formatters for displaying numbers and change percentages
    private val priceFormatter = DecimalFormat("#,###")
    private val changeFormatter = DecimalFormat("+#.##%;-#.##%")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_coins, container, false)

        // Initialize TextViews by finding them in the layout
        tvIRGold18KPrice = view.findViewById(R.id.tvIRGold18KPrice)
        tvIRGold18KChange = view.findViewById(R.id.tvIRGold18KChange)
        tvIRGold24KPrice = view.findViewById(R.id.tvIRGold24KPrice)
        tvIRGold24KChange = view.findViewById(R.id.tvIRGold24KChange)
        tvIRCoinBaharPrice = view.findViewById(R.id.tvIRCoinBaharPrice)
        tvIRCoinBaharChange = view.findViewById(R.id.tvIRCoinBaharChange)
        tvIRCoinEmamiPrice = view.findViewById(R.id.tvIRCoinEmamiPrice)
        tvIRCoinEmamiChange = view.findViewById(R.id.tvIRCoinEmamiChange)
        tvIRCoinHalfPrice = view.findViewById(R.id.tvIRCoinHalfPrice)
        tvIRCoinHalfChange = view.findViewById(R.id.tvIRCoinHalfChange)
        tvIRCoinQuarterPrice = view.findViewById(R.id.tvIRCoinQuarterPrice)
        tvIRCoinQuarterChange = view.findViewById(R.id.tvIRCoinQuarterChange)
        tvIRCoin1GPrice = view.findViewById(R.id.tvIRCoin1GPrice)
        tvIRCoin1GChange = view.findViewById(R.id.tvIRCoin1GChange)
        tvIRGoldMeltedPrice = view.findViewById(R.id.tvIRGoldMeltedPrice)
        tvIRGoldMeltedChange = view.findViewById(R.id.tvIRGoldMeltedChange)

        // Initialize Button
        btnRefreshCoins = view.findViewById(R.id.btnRefreshCoins)

        // Set up refresh button click listener to fetch new data
        btnRefreshCoins.setOnClickListener {
            Log.d(TAG, "Refresh button clicked. Fetching data.")
            fetchMarketData()
        }

        // Fetch initial market data when the fragment is created
        fetchMarketData()

        return view
    }

    /**
     * Fetches market data from the specified API URL using Volley.
     * Handles successful responses by updating the UI and errors by showing toasts.
     */
    private fun fetchMarketData() {
        // Get a new request queue for network operations
        val requestQueue = Volley.newRequestQueue(requireContext())
        Log.d(TAG, "Attempting to fetch data from URL: $API_URL")

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
                    Toast.makeText(requireContext(), "Data updated successfully!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    // Handle JSON parsing errors
                    Log.e(TAG, "Error parsing data (JSON): ${e.message}", e)
                    Toast.makeText(requireContext(), "Error parsing data: ${e.message}", Toast.LENGTH_LONG).show()
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
     * Updates the UI with market data received from the API.
     * This function now uses a helper to reduce code repetition.
     */
    private fun updateUI(data: JSONObject) {
        // Update Gold 18K data
        updateMarketItemUI(data.optJSONObject("iran_gold"), tvIRGold18KPrice, tvIRGold18KChange)

        // Update Gold 24K data
        updateMarketItemUI(data.optJSONObject("gold_24k"), tvIRGold24KPrice, tvIRGold24KChange)

        // Update Bahar Coin data
        updateMarketItemUI(data.optJSONObject("bahar_coin"), tvIRCoinBaharPrice, tvIRCoinBaharChange)

        // Update Emami Coin data
        updateMarketItemUI(data.optJSONObject("emami_coin"), tvIRCoinEmamiPrice, tvIRCoinEmamiChange)

        // Update Half Coin data
        updateMarketItemUI(data.optJSONObject("half_coin"), tvIRCoinHalfPrice, tvIRCoinHalfChange)

        // Update Quarter Coin data
        updateMarketItemUI(data.optJSONObject("quarter_coin"), tvIRCoinQuarterPrice, tvIRCoinQuarterChange)

        // Update 1G Coin data
        updateMarketItemUI(data.optJSONObject("coin_1g"), tvIRCoin1GPrice, tvIRCoin1GChange)

        // Update Melted Gold data
        updateMarketItemUI(data.optJSONObject("ir_gold_melted"), tvIRGoldMeltedPrice, tvIRGoldMeltedChange)
    }

    /**
     * Helper function to update the price and change TextViews for a single market item.
     * Reduces code duplication in the updateUI function.
     *
     * @param itemJson The JSONObject containing "price" and "change" for a market item.
     * @param priceTextView The TextView to display the price.
     * @param changeTextView The TextView to display the change percentage.
     */
    private fun updateMarketItemUI(itemJson: JSONObject?, priceTextView: TextView, changeTextView: TextView) {
        if (itemJson != null) {
            val price = itemJson.optDouble("price", Double.NaN)
            val change = itemJson.optDouble("change", Double.NaN)

            // Update price TextView
            if (!price.isNaN()) {
                priceTextView.text = "${priceFormatter.format(price)} T"
            } else {
                priceTextView.text = "-- T"
            }

            // Update change TextView and set its color
            if (!change.isNaN()) {
                changeTextView.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(changeTextView, change)
            } else {
                changeTextView.text = "--%"
                changeTextView.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            // If the item JSON is null, set both TextViews to default '--' values and gray color
            priceTextView.text = "-- T"
            changeTextView.text = "--%"
            changeTextView.setTextColor(requireContext().getColor(R.color.gray))
        }
    }

    /**
     * Sets the text color of a TextView based on the change value.
     * Green for positive, Red for negative, Gray for no change.
     *
     * @param textView The TextView whose color needs to be set.
     * @param change The double value representing the change.
     */
    private fun setChangeTextColor(textView: TextView, change: Double) {
        if (change > 0) {
            textView.setTextColor(requireContext().getColor(R.color.green))
        } else if (change < 0) {
            textView.setTextColor(requireContext().getColor(R.color.red))
        } else {
            textView.setTextColor(requireContext().getColor(R.color.gray))
        }
    }
}
