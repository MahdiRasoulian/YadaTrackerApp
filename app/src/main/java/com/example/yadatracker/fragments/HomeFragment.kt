package com.example.yadatracker.fragments



import android.graphics.Color

import android.os.Bundle

import android.util.Log

import android.view.LayoutInflater

import android.view.View

import android.view.ViewGroup

import android.widget.Button

import android.widget.ImageView

import android.widget.TextView

import android.widget.Toast

import androidx.fragment.app.Fragment

import com.android.volley.DefaultRetryPolicy

import com.android.volley.Request

import com.android.volley.toolbox.StringRequest

import com.android.volley.toolbox.Volley

import com.example.yadatracker.R // Ensure this R import points to your app's R file

import org.json.JSONObject

import java.text.DecimalFormat

import java.text.SimpleDateFormat

import java.time.LocalDateTime

import java.time.ZoneId

import java.time.ZoneOffset

import java.time.format.DateTimeFormatter

import java.util.Calendar

import java.util.Locale



class HomeFragment : Fragment() {



// Declare TextViews for Live Market Grid

    private lateinit var tvUpdateTime: TextView

    private lateinit var tvIranGoldPrice: TextView

    private lateinit var tvIranGoldChange: TextView

    private lateinit var tvGlobalGoldPrice: TextView

    private lateinit var tvGlobalGoldChange: TextView

    private lateinit var tvUsdtIrtPrice: TextView

    private lateinit var tvUsdtIrtChange: TextView

    private lateinit var tvBitcoinPrice: TextView

    private lateinit var tvBitcoinChange: TextView

    private lateinit var tvBtcDominance: TextView



// New TextViews for PAXG/USDT in Live Market Grid

    private lateinit var tvPaxgUsdtPriceLiveGrid: TextView

    private lateinit var tvPaxgUsdtChangeLiveGrid: TextView



// TextViews for Iranian Stock Market (4 separate cards in Section 2)

    private lateinit var tvTedpixIndex: TextView

    private lateinit var tvTedpixChange: TextView

    private lateinit var tvEqualWeigh: TextView

    private lateinit var tvEqualWeightChange: TextView

    private lateinit var tvTavanEtfValue: TextView

    private lateinit var tvTavanEtfChange: TextView

    private lateinit var tvAyarEtfValue: TextView

    private lateinit var tvAyarEtfChange: TextView



// Button and Banner

    private lateinit var btnRefresh: Button

    private lateinit var ivBanner: ImageView



// IMPORTANT: UPDATE THIS API_URL TO YOUR PYTHONANYWHERE DOMAIN

// Example: "https://yourusername.pythonanywhere.com/market-data"

    private val API_URL = "https://YADATrade.pythonanywhere.com/market-data" // <--- Ensure this is your live API URL



    private val TAG = "HomeFragment"



    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View? {

// Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)



// Initialize UI elements from the inflated view

        tvUpdateTime = view.findViewById(R.id.tvUpdateTime)

        ivBanner = view.findViewById(R.id.ivBanner)

        btnRefresh = view.findViewById(R.id.btnRefresh)



// Initialize TextViews for Live Market Grid

        tvIranGoldPrice = view.findViewById(R.id.tvIranGoldPrice)

        tvIranGoldChange = view.findViewById(R.id.tvIranGoldChange)

        tvGlobalGoldPrice = view.findViewById(R.id.tvGlobalGoldPrice)

        tvGlobalGoldChange = view.findViewById(R.id.tvGlobalGoldChange)

        tvUsdtIrtPrice = view.findViewById(R.id.tvUsdtIrtPrice)

        tvUsdtIrtChange = view.findViewById(R.id.tvUsdtIrtChange)

        tvBitcoinPrice = view.findViewById(R.id.tvBitcoinPrice)

        tvBitcoinChange = view.findViewById(R.id.tvBitcoinChange)

        tvBtcDominance = view.findViewById(R.id.tvBtcDominance)



// Initialize NEW TextViews for PAXG/USDT in Live Market Grid

        tvPaxgUsdtPriceLiveGrid = view.findViewById(R.id.tvPaxgUsdtPriceLiveGrid)

        tvPaxgUsdtChangeLiveGrid = view.findViewById(R.id.tvPaxgUsdtChangeLiveGrid)



// Initialize TextViews for Detailed Iranian Stock Market (4 separate cards)

        tvTedpixIndex = view.findViewById(R.id.tvTedpixIndex)

        tvTedpixChange = view.findViewById(R.id.tvTedpixChange)

        tvEqualWeigh = view.findViewById(R.id.tvEqualWeigh)

        tvEqualWeightChange = view.findViewById(R.id.tvEqualWeightChange)

        tvTavanEtfValue = view.findViewById(R.id.tvTavanEtfValue)

        tvTavanEtfChange = view.findViewById(R.id.tvTavanEtfChange)

        tvAyarEtfValue = view.findViewById(R.id.tvAyarEtfValue)

        tvAyarEtfChange = view.findViewById(R.id.tvAyarEtfChange)



// Set up refresh button click listener

        btnRefresh.setOnClickListener {

            Log.d(TAG, "Refresh button clicked. Fetching data.")

            fetchMarketData()

        }



// Initial data fetch when the fragment is created

        fetchMarketData()



        return view

    }



    private fun fetchMarketData() {

        tvUpdateTime.text = "Fetching data..."

// Use requireContext() for Volley queue as it's within a Fragment

        val requestQueue = Volley.newRequestQueue(requireContext())

        Log.d(TAG, "Attempting to fetch data from URL: $API_URL")



        val stringRequest = StringRequest(

            Request.Method.GET, API_URL,

            { response ->

                Log.d(TAG, "API Response received: $response")

                try {

                    val jsonResponse = JSONObject(response)

                    updateUI(jsonResponse)

                    Toast.makeText(requireContext(), "Data updated successfully!", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {

                    Log.e(TAG, "Error parsing data (JSON): ${e.message}", e)

                    Toast.makeText(requireContext(), "Error parsing data: ${e.message}", Toast.LENGTH_LONG).show()

                    e.printStackTrace()

                }

            },

            { error ->

                var errorMessage = "Network error: " + (error.message ?: "Unknown error")

                if (error.networkResponse != null) {

                    val statusCode = error.networkResponse.statusCode

                    val responseData = String(error.networkResponse.data)

                    errorMessage += "\nStatus Code: $statusCode"

                    errorMessage += "\nResponse Data: $responseData"

                    Log.e(TAG, "Volley Error - Status: $statusCode, Data: $responseData", error)

                } else {

                    Log.e(TAG, "Volley Error - No network response. Message: ${error.message}", error)

                }

                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()

                error.printStackTrace()

            }

        )



        val socketTimeout = 10000 // 10 seconds

        val retryPolicy = DefaultRetryPolicy(

            socketTimeout,

            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

        )

        stringRequest.retryPolicy = retryPolicy



        requestQueue.add(stringRequest)

        Log.d(TAG, "Volley request added to queue.")

    }



    private fun updateUI(data: JSONObject) {

        val formatter = DecimalFormat("#,###")

        val changeFormatter = DecimalFormat("+#.##%;-#.##%")

        val dominanceFormatter = DecimalFormat("0.00") // For BTC dominance, 2 decimal places



// --- Update timestamp to DEVICE LOCAL TIME ---

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            val timestampString = data.optString("timestamp", "")

            if (timestampString.isNotEmpty()) {

                try {

// Parse the timestamp string as UTC

                    val utcDateTime = LocalDateTime.parse(timestampString, DateTimeFormatter.ISO_DATE_TIME)

                    val zonedDateTimeUtc = utcDateTime.atZone(ZoneOffset.UTC)



// Convert to device's local time zone

                    val localZonedDateTime = zonedDateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())



// Format for display

                    tvUpdateTime.text = "Last Updated: ${localZonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"

                } catch (e: Exception) {

                    Log.e(TAG, "Error parsing timestamp for local time: ${e.message}", e)

                    tvUpdateTime.text = "Last Updated: N/A (Timestamp Parse Error)"

                }

            } else {

                tvUpdateTime.text = "Last Updated: N/A (No Timestamp)"

            }

        } else {

// Fallback for older Android versions (uses device's current time from Calendar)

            try {

                val calendar = Calendar.getInstance()

                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                tvUpdateTime.text = "Last Updated: ${dateFormat.format(calendar.time)}"

            } catch (e: Exception) {

                Log.e(TAG, "Error formatting date for older API: ${e.message}", e)

                tvUpdateTime.text = "Last Updated: N/A (Fallback Error)"

            }

        }



// --- Iran Gold ---

        val iranGold = data.optJSONObject("iran_gold")

        if (iranGold != null) {

            val price = iranGold.optDouble("price", Double.NaN)

            val change = iranGold.optDouble("change", Double.NaN)

            if (!price.isNaN()) tvIranGoldPrice.text = "${formatter.format(price)} T" else tvIranGoldPrice.text = "-- T"

            if (!change.isNaN()) {

                tvIranGoldChange.text = changeFormatter.format(change / 100.0)

                setChangeTextColor(tvIranGoldChange, change)

            } else {

                tvIranGoldChange.text = "--%"

                tvIranGoldChange.setTextColor(Color.GRAY)

            }

        } else {

            tvIranGoldPrice.text = "-- T"

            tvIranGoldChange.text = "--%"

            tvIranGoldChange.setTextColor(Color.GRAY)

        }



// --- Global Gold ---

        val globalGold = data.optJSONObject("global_gold")

        if (globalGold != null) {

            val price = globalGold.optDouble("price", Double.NaN)

            val change = globalGold.optDouble("change", Double.NaN)

            if (!price.isNaN()) tvGlobalGoldPrice.text = "${formatter.format(price)} USD" else tvGlobalGoldPrice.text = "-- USD"

            if (!change.isNaN()) {

                tvGlobalGoldChange.text = changeFormatter.format(change / 100.0)

                setChangeTextColor(tvGlobalGoldChange, change)

            } else {

                tvGlobalGoldChange.text = "--%"

                tvGlobalGoldChange.setTextColor(Color.GRAY)

            }

        } else {

            tvGlobalGoldPrice.text = "-- USD"

            tvGlobalGoldChange.text = "--%"

            tvGlobalGoldChange.setTextColor(Color.GRAY)

        }



// --- USDT/IRT ---

        val usdtIrt = data.optJSONObject("usdt_irt")

        if (usdtIrt != null) {

            val price = usdtIrt.optDouble("price", Double.NaN)

            val change = usdtIrt.optDouble("change", Double.NaN)

            if (!price.isNaN()) tvUsdtIrtPrice.text = "${formatter.format(price)} T"

            else tvUsdtIrtPrice.text = "-- T"



            if (!change.isNaN()) {

                tvUsdtIrtChange.text = changeFormatter.format(change / 100.0)

                setChangeTextColor(tvUsdtIrtChange, change)

            } else {

                tvUsdtIrtChange.text = "--%"

                tvUsdtIrtChange.setTextColor(Color.GRAY)

            }

        } else {

            tvUsdtIrtPrice.text = "-- T"

            tvUsdtIrtChange.text = "--%"

            tvUsdtIrtChange.setTextColor(Color.GRAY)

        }



// --- Bitcoin ---

        val bitcoin = data.optJSONObject("bitcoin")

        if (bitcoin != null) {

            val price = bitcoin.optDouble("price", Double.NaN)

            val change = bitcoin.optDouble("change", Double.NaN)

            if (!price.isNaN()) tvBitcoinPrice.text = "${formatter.format(price)} USD" else tvBitcoinPrice.text = "-- USD"

            if (!change.isNaN()) {

                tvBitcoinChange.text = changeFormatter.format(change / 100.0)

                setChangeTextColor(tvBitcoinChange, change)

            } else {

                tvBitcoinChange.text = "--%"

                tvBitcoinChange.setTextColor(Color.GRAY)

            }

        } else {

            tvBitcoinPrice.text = "-- USD"

            tvBitcoinChange.text = "--%"

            tvBitcoinChange.setTextColor(Color.GRAY)

        }



// --- PAXG/USDT

        val paxgUsdt = data.optJSONObject("paxg_usdt")

        if (paxgUsdt != null) {

            val price = paxgUsdt.optDouble("price", Double.NaN)

            val change = paxgUsdt.optDouble("change", Double.NaN)

            if (!price.isNaN()) tvPaxgUsdtPriceLiveGrid.text = "${formatter.format(price)} USD" else tvPaxgUsdtPriceLiveGrid.text = "-- USD"

            if (!change.isNaN()) {

                tvPaxgUsdtChangeLiveGrid.text = changeFormatter.format(change / 100.0)

                setChangeTextColor(tvPaxgUsdtChangeLiveGrid, change)

            } else {

                tvPaxgUsdtChangeLiveGrid.text = "--%"

                tvPaxgUsdtChangeLiveGrid.setTextColor(Color.GRAY)

            }

        } else {

            tvPaxgUsdtPriceLiveGrid.text = "-- USD"

            tvPaxgUsdtChangeLiveGrid.text = "--%"

            tvPaxgUsdtChangeLiveGrid.setTextColor(Color.GRAY)

        }



// --- BTC Dominance ---

        val btcDominance = data.optJSONObject("btc_dominance")

        if (btcDominance != null) {

            val dominance = btcDominance.optDouble("dominance", Double.NaN)

            if (!dominance.isNaN()) {

                tvBtcDominance.text = "${dominanceFormatter.format(dominance)}%"

            } else {

                tvBtcDominance.text = "--%"

            }

        } else {

            tvBtcDominance.text = "--%"

        }



// --- Iranian Stock Market Index Details

        val iranianStockIndexData = data.optJSONObject("iranian_stock_market_index")

        if (iranianStockIndexData != null) {

            val tedpixIndex = iranianStockIndexData.optDouble("tedpix_index", Double.NaN)

            val tedpixChange = iranianStockIndexData.optDouble("tedpix_change_percent", Double.NaN)

            val equalWeightIndex = iranianStockIndexData.optDouble("index_equalWeight", Double.NaN)

            val equalWeightChange = iranianStockIndexData.optDouble("index_equalWeight_change_percent", Double.NaN)

            val tavanEtf = iranianStockIndexData.optDouble("tavan_etf", Double.NaN)

            val ayarEtf = iranianStockIndexData.optDouble("ayar_etf", Double.NaN)



// TEDPIX

            if (!tedpixIndex.isNaN()) tvTedpixIndex.text = "${formatter.format(tedpixIndex)} Points" else tvTedpixIndex.text = "-- Points"

            if (!tedpixChange.isNaN()) {

                tvTedpixChange.text = changeFormatter.format(tedpixChange / 100.0)

                setChangeTextColor(tvTedpixChange, tedpixChange)

            } else {

                tvTedpixChange.text = "--%"

                tvTedpixChange.setTextColor(Color.GRAY)

            }



// Equal Weight (IFX)

            if (!equalWeightIndex.isNaN()) tvEqualWeigh.text = "${formatter.format(equalWeightIndex)} Points" else tvEqualWeigh.text = "-- Points"

            if (!equalWeightChange.isNaN()) {

                tvEqualWeightChange.text = changeFormatter.format(equalWeightChange / 100.0)

                setChangeTextColor(tvEqualWeightChange, equalWeightChange)

            } else {

                tvEqualWeightChange.text = "--%"

                tvEqualWeightChange.setTextColor(Color.GRAY)

            }



// Tavan ETF

            if (!tavanEtf.isNaN()) tvTavanEtfValue.text = "${formatter.format(tavanEtf)} IRR" else tvTavanEtfValue.text = "-- IRR"

            tvTavanEtfChange.text = "--%" // No change data from API

            tvTavanEtfChange.setTextColor(Color.GRAY)



// Ayar ETF

            if (!ayarEtf.isNaN()) tvAyarEtfValue.text = "${formatter.format(ayarEtf)} IRR" else tvAyarEtfValue.text = "-- IRR"

            tvAyarEtfChange.text = "--%" // No change data from API

            tvAyarEtfChange.setTextColor(Color.GRAY)



        } else {

// Set all detailed Iranian Stock Market fields to N/A

            tvTedpixIndex.text = "-- Points"

            tvTedpixChange.text = "--%"

            tvTedpixChange.setTextColor(Color.GRAY)



            tvEqualWeigh.text = "-- Points"

            tvEqualWeightChange.text = "--%"

            tvEqualWeightChange.setTextColor(Color.GRAY)



            tvTavanEtfValue.text = "-- IRR"

            tvTavanEtfChange.text = "--%"

            tvTavanEtfChange.setTextColor(Color.GRAY)



            tvAyarEtfValue.text = "-- IRR"

            tvAyarEtfChange.text = "--%"

            tvAyarEtfChange.setTextColor(Color.GRAY)

        }

    }



    private fun setChangeTextColor(textView: TextView, change: Double) {

        if (change > 0) {

            textView.setTextColor(requireContext().getColor(R.color.green))

        } else if (change < 0) {

            textView.setTextColor(requireContext().getColor(R.color.red))

        } else {

            textView.setTextColor(Color.GRAY)

        }

    }

}