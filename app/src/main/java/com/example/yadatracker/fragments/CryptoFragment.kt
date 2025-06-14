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
import org.json.JSONArray // Import JSONArray to handle the array of cryptocurrencies
import org.json.JSONObject
import java.text.DecimalFormat

class CryptoFragment : Fragment() {

    // Declare TextViews for Cryptocurrencies
    private lateinit var tvBitcoinPrice: TextView
    private lateinit var tvBitcoinChange: TextView
    private lateinit var tvEthereumPrice: TextView
    private lateinit var tvEthereumChange: TextView
    private lateinit var tvTetherPrice: TextView
    private lateinit var tvTetherChange: TextView
    private lateinit var tvXRPPrice: TextView
    private lateinit var tvXRPChange: TextView
    private lateinit var tvBinanceCoinPrice: TextView
    private lateinit var tvBinanceCoinChange: TextView
    private lateinit var tvSolanaPrice: TextView
    private lateinit var tvSolanaChange: TextView
    private lateinit var tvUSDCoinPrice: TextView
    private lateinit var tvUSDCoinChange: TextView
    private lateinit var tvDogecoinPrice: TextView
    private lateinit var tvDogecoinChange: TextView
    private lateinit var tvTRONPrice: TextView
    private lateinit var tvTRONChange: TextView
    private lateinit var tvSuiPrice: TextView
    private lateinit var tvSuiChange: TextView
    private lateinit var tvLitecoinPrice: TextView
    private lateinit var tvLitecoinChange: TextView
    private lateinit var tvToncoinPrice: TextView
    private lateinit var tvToncoinChange: TextView

    // Button
    private lateinit var btnRefreshCrypto: Button

    // API URL
    private val API_URL = "https://YADATrade.pythonanywhere.com/market-data"
    private val TAG = "CryptoFragment"

    // Formatters for displaying numbers and change percentages
    private val priceFormatter = DecimalFormat("#,###.##") // Allows for decimal places in prices
    private val changeFormatter = DecimalFormat("+#.##%;-#.##%")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crypto, container, false)

        // Initialize TextViews by finding them in the layout
        tvBitcoinPrice = view.findViewById(R.id.tvBitcoinPrice)
        tvBitcoinChange = view.findViewById(R.id.tvBitcoinChange)
        tvEthereumPrice = view.findViewById(R.id.tvEthereumPrice)
        tvEthereumChange = view.findViewById(R.id.tvEthereumChange)
        tvTetherPrice = view.findViewById(R.id.tvTetherPrice)
        tvTetherChange = view.findViewById(R.id.tvTetherChange)
        tvXRPPrice = view.findViewById(R.id.tvXRPPrice)
        tvXRPChange = view.findViewById(R.id.tvXRPChange)
        tvBinanceCoinPrice = view.findViewById(R.id.tvBinanceCoinPrice)
        tvBinanceCoinChange = view.findViewById(R.id.tvBinanceCoinChange)
        tvSolanaPrice = view.findViewById(R.id.tvSolanaPrice)
        tvSolanaChange = view.findViewById(R.id.tvSolanaChange)
        tvUSDCoinPrice = view.findViewById(R.id.tvUSDCoinPrice)
        tvUSDCoinChange = view.findViewById(R.id.tvUSDCoinChange)
        tvDogecoinPrice = view.findViewById(R.id.tvDogecoinPrice)
        tvDogecoinChange = view.findViewById(R.id.tvDogecoinChange)
        tvTRONPrice = view.findViewById(R.id.tvTRONPrice)
        tvTRONChange = view.findViewById(R.id.tvTRONChange)
        tvSuiPrice = view.findViewById(R.id.tvSuiPrice)
        tvSuiChange = view.findViewById(R.id.tvSuiChange)
        tvLitecoinPrice = view.findViewById(R.id.tvLitecoinPrice)
        tvLitecoinChange = view.findViewById(R.id.tvLitecoinChange)
        tvToncoinPrice = view.findViewById(R.id.tvToncoinPrice)
        tvToncoinChange = view.findViewById(R.id.tvToncoinChange)

        // Initialize Button
        btnRefreshCrypto = view.findViewById(R.id.btnRefreshCrypto)

        // Set up refresh button click listener to fetch new data
        btnRefreshCrypto.setOnClickListener {
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
     * Updates the UI with market data received from the API response.
     * This function now correctly parses the mixed JSON structure for cryptocurrencies.
     */
    private fun updateUI(data: JSONObject) {
        // --- Handle specific root-level cryptocurrency objects (like Bitcoin) ---
        // Bitcoin (The direct "bitcoin" object in your JSON)
        val bitcoinRoot = data.optJSONObject("bitcoin")
        if (bitcoinRoot != null) {
            val price = bitcoinRoot.optDouble("price", Double.NaN)
            val change = bitcoinRoot.optDouble("change", Double.NaN) // Uses "change" key
            val unit = bitcoinRoot.optString("unit", "USD") // Use the unit from JSON
            updateCryptoItemUI(price, change, unit, tvBitcoinPrice, tvBitcoinChange)
        } else {
            // If bitcoin root data is missing, set to default
            updateCryptoItemUI(Double.NaN, Double.NaN, "USD", tvBitcoinPrice, tvBitcoinChange)
            Log.e(TAG, "Bitcoin root data not found in response.")
        }

        // --- Handle cryptocurrencies from the 'cryptocurrencies' array ---
        // This array contains most of your listed cryptos
        val cryptocurrenciesArray = data.optJSONArray("cryptocurrencies")
        if (cryptocurrenciesArray != null) {
            for (i in 0 until cryptocurrenciesArray.length()) {
                val cryptoItem = cryptocurrenciesArray.optJSONObject(i)
                if (cryptoItem != null) {
                    val name = cryptoItem.optString("name")
                    val price = cryptoItem.optDouble("price", Double.NaN)
                    // Note: This array uses "change_percent"
                    val changePercent = cryptoItem.optDouble("change_percent", Double.NaN)
                    val unit = cryptoItem.optString("unit", "USD") // Assuming default USD if unit is not in array item

                    // Match the cryptocurrency name to its respective TextViews
                    // Bitcoin is handled by the root object, so it's generally excluded here to avoid overwriting
                    when (name) {
                        "Ethereum" -> updateCryptoItemUI(price, changePercent, unit, tvEthereumPrice, tvEthereumChange)
                        "Tether" -> updateCryptoItemUI(price, changePercent, unit, tvTetherPrice, tvTetherChange)
                        "XRP" -> updateCryptoItemUI(price, changePercent, unit, tvXRPPrice, tvXRPChange)
                        "Binance Coin" -> updateCryptoItemUI(price, changePercent, unit, tvBinanceCoinPrice, tvBinanceCoinChange)
                        "Solana" -> updateCryptoItemUI(price, changePercent, unit, tvSolanaPrice, tvSolanaChange)
                        "USD Coin" -> updateCryptoItemUI(price, changePercent, unit, tvUSDCoinPrice, tvUSDCoinChange)
                        "Dogecoin" -> updateCryptoItemUI(price, changePercent, unit, tvDogecoinPrice, tvDogecoinChange)
                        "TRON" -> updateCryptoItemUI(price, changePercent, unit, tvTRONPrice, tvTRONChange)
                        "Sui" -> updateCryptoItemUI(price, changePercent, unit, tvSuiPrice, tvSuiChange)
                        "Litecoin" -> updateCryptoItemUI(price, changePercent, unit, tvLitecoinPrice, tvLitecoinChange)
                        "Toncoin" -> updateCryptoItemUI(price, changePercent, unit, tvToncoinPrice, tvToncoinChange)
                        // If you prefer the Bitcoin data from this array over the root object, uncomment below:
                        // "Bitcoin" -> updateCryptoItemUI(price, changePercent, unit, tvBitcoinPrice, tvBitcoinChange)
                        else -> Log.d(TAG, "No TextViews configured for crypto: $name")
                    }
                }
            }
        } else {
            Log.e(TAG, "'cryptocurrencies' array not found or is null in response.")
            // If the array is not found, set all crypto TextViews to default values
            setDefaultCryptoUI()
        }
    }

    /**
     * Helper function to update the price and change TextViews for a single cryptocurrency item.
     * Reduces code duplication.
     *
     * @param price The price value. Can be Double.NaN if not available.
     * @param change The change value (percentage). Can be Double.NaN if not available.
     * @param unit The currency unit (e.g., "USD").
     * @param priceTextView The TextView to display the price.
     * @param changeTextView The TextView to display the change percentage.
     */
    private fun updateCryptoItemUI(
        price: Double,
        change: Double, // This value is expected to be the raw percentage (e.g., 0.17 for 0.17%)
        unit: String,
        priceTextView: TextView,
        changeTextView: TextView
    ) {
        // Update price TextView
        if (!price.isNaN()) {
            priceTextView.text = "${priceFormatter.format(price)} $unit"
        } else {
            priceTextView.text = "-- $unit"
        }

        // Update change TextView and set its color
        if (!change.isNaN()) {
            // The changeFormatter expects the value as a decimal (e.g., 0.0017 for 0.17%)
            changeTextView.text = changeFormatter.format(change / 100.0)
            setChangeTextColor(changeTextView, change)
        } else {
            changeTextView.text = "--%"
            changeTextView.setTextColor(requireContext().getColor(R.color.gray))
        }
    }

    /**
     * Helper function to set all cryptocurrency TextViews to default values ("-- USD" and "--%").
     * Called when the 'cryptocurrencies' array is not found in the API response.
     */
    private fun setDefaultCryptoUI() {
        val defaultUnit = "USD" // Assuming USD is the standard unit for these cryptos
        // Update Bitcoin first, then others
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvBitcoinPrice, tvBitcoinChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvEthereumPrice, tvEthereumChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvTetherPrice, tvTetherChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvXRPPrice, tvXRPChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvBinanceCoinPrice, tvBinanceCoinChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvSolanaPrice, tvSolanaChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvUSDCoinPrice, tvUSDCoinChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvDogecoinPrice, tvDogecoinChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvTRONPrice, tvTRONChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvSuiPrice, tvSuiChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvLitecoinPrice, tvLitecoinChange)
        updateCryptoItemUI(Double.NaN, Double.NaN, defaultUnit, tvToncoinPrice, tvToncoinChange)
    }

    /**
     * Sets the text color of a TextView based on the change value.
     * Green for positive, Red for negative, Gray for no change.
     *
     * @param textView The TextView whose color needs to be set.
     * @param change The double value representing the change (e.g., 0.17 for +0.17%).
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
