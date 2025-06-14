package com.example.yadatracker.fragments

import android.graphics.Color
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

class BourseFragment : Fragment() {

    // Declare TextViews for Stock Market Items
    private lateinit var tvDaraYekomEtfPrice: TextView
    private lateinit var tvDaraYekomEtfChange: TextView
    private lateinit var tvShirazPrice: TextView
    private lateinit var tvShirazChange: TextView
    private lateinit var tvNouriPrice: TextView
    private lateinit var tvNouriChange: TextView
    private lateinit var tvZagrosPrice: TextView
    private lateinit var tvZagrosChange: TextView
    private lateinit var tvFarsPrice: TextView
    private lateinit var tvFarsChange: TextView
    private lateinit var tvHafarsPrice: TextView
    private lateinit var tvHafarsChange: TextView
    private lateinit var tvVeshahrPrice: TextView
    private lateinit var tvVeshahrChange: TextView
    private lateinit var tvVepasarPrice: TextView
    private lateinit var tvVepasarChange: TextView

    // Button
    private lateinit var btnRefreshBourse: Button

    // API URL
    private val API_URL = "https://YADATrade.pythonanywhere.com/market-data"
    private val TAG = "BourseFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bourse, container, false)

        // Initialize TextViews
        tvDaraYekomEtfPrice = view.findViewById(R.id.tvDaraYekomEtfPrice)
        tvDaraYekomEtfChange = view.findViewById(R.id.tvDaraYekomEtfChange)
        tvShirazPrice = view.findViewById(R.id.tvShirazPrice)
        tvShirazChange = view.findViewById(R.id.tvShirazChange)
        tvNouriPrice = view.findViewById(R.id.tvNouriPrice)
        tvNouriChange = view.findViewById(R.id.tvNouriChange)
        tvZagrosPrice = view.findViewById(R.id.tvZagrosPrice)
        tvZagrosChange = view.findViewById(R.id.tvZagrosChange)
        tvFarsPrice = view.findViewById(R.id.tvFarsPrice)
        tvFarsChange = view.findViewById(R.id.tvFarsChange)
        tvHafarsPrice = view.findViewById(R.id.tvHafarsPrice)
        tvHafarsChange = view.findViewById(R.id.tvHafarsChange)
        tvVeshahrPrice = view.findViewById(R.id.tvVeshahrPrice)
        tvVeshahrChange = view.findViewById(R.id.tvVeshahrChange)
        tvVepasarPrice = view.findViewById(R.id.tvVepasarPrice)
        tvVepasarChange = view.findViewById(R.id.tvVepasarChange)

        // Initialize Button
        btnRefreshBourse = view.findViewById(R.id.btnRefreshBourse)

        // Set up refresh button click listener
        btnRefreshBourse.setOnClickListener {
            Log.d(TAG, "Refresh button clicked. Fetching data.")
            fetchMarketData()
        }

        // Initial data fetch
        fetchMarketData()

        return view
    }

    private fun fetchMarketData() {
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

        // Dara Yekom ETF
        val iranianStockIndexData = data.optJSONObject("iranian_stock_market_index")
        if (iranianStockIndexData != null) {
            val price = iranianStockIndexData.optDouble("dara_yekom_etf", Double.NaN)
            val change = iranianStockIndexData.optDouble("dara_yekom_etf_change", Double.NaN)
            if (!price.isNaN()) tvDaraYekomEtfPrice.text = "${formatter.format(price)} IRR" else tvDaraYekomEtfPrice.text = "-- IRR"
            if (!change.isNaN()) {
                tvDaraYekomEtfChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvDaraYekomEtfChange, change)
            } else {
                tvDaraYekomEtfChange.text = "--%"
                tvDaraYekomEtfChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "iranian_stock_market_index data not found in response")
        }

        // Shiraz
        val shiraz = iranianStockIndexData?.optJSONObject("shiraz")
        if (shiraz != null) {
            val price = shiraz.optDouble("price", Double.NaN)
            val change = shiraz.optDouble("change", Double.NaN)
            if (!price.isNaN()) tvShirazPrice.text = "${formatter.format(price)} Toman" else tvShirazPrice.text = "-- Toman"
            if (!change.isNaN()) {
                tvShirazChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvShirazChange, change)
            } else {
                tvShirazChange.text = "--%"
                tvShirazChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "shiraz data not found in response")
        }

        // Nouri
        val nouri = iranianStockIndexData?.optJSONObject("nouri")
        if (nouri != null) {
            val price = nouri.optDouble("price", Double.NaN)
            val change = nouri.optDouble("change", Double.NaN)
            if (!price.isNaN()) tvNouriPrice.text = "${formatter.format(price)} Toman" else tvNouriPrice.text = "-- Toman"
            if (!change.isNaN()) {
                tvNouriChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvNouriChange, change)
            } else {
                tvNouriChange.text = "--%"
                tvNouriChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "nouri data not found in response")
        }

        // Zagros
        val zagros = iranianStockIndexData?.optJSONObject("zagros")
        if (zagros != null) {
            val price = zagros.optDouble("price", Double.NaN)
            val change = zagros.optDouble("change", Double.NaN)
            if (!price.isNaN()) tvZagrosPrice.text = "${formatter.format(price)} Toman" else tvZagrosPrice.text = "-- Toman"
            if (!change.isNaN()) {
                tvZagrosChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvZagrosChange, change)
            } else {
                tvZagrosChange.text = "--%"
                tvZagrosChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "zagros data not found in response")
        }

        // Fars
        val fars = iranianStockIndexData?.optJSONObject("fars")
        if (fars != null) {
            val price = fars.optDouble("price", Double.NaN)
            val change = fars.optDouble("change", Double.NaN)
            if (!price.isNaN()) tvFarsPrice.text = "${formatter.format(price)} Toman" else tvFarsPrice.text = "-- Toman"
            if (!change.isNaN()) {
                tvFarsChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvFarsChange, change)
            } else {
                tvFarsChange.text = "--%"
                tvFarsChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "fars data not found in response")
        }

        // Hafars
        val hafars = iranianStockIndexData?.optJSONObject("hafars")
        if (hafars != null) {
            val price = hafars.optDouble("price", Double.NaN)
            val change = hafars.optDouble("change", Double.NaN)
            if (!price.isNaN()) tvHafarsPrice.text = "${formatter.format(price)} Toman" else tvHafarsPrice.text = "-- Toman"
            if (!change.isNaN()) {
                tvHafarsChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvHafarsChange, change)
            } else {
                tvHafarsChange.text = "--%"
                tvHafarsChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "hafars data not found in response")
        }

        // Veshahr
        val veshahr = iranianStockIndexData?.optJSONObject("veshahr")
        if (veshahr != null) {
            val price = veshahr.optDouble("price", Double.NaN)
            val change = veshahr.optDouble("change", Double.NaN)
            if (!price.isNaN()) tvVeshahrPrice.text = "${formatter.format(price)} Toman" else tvVeshahrPrice.text = "-- Toman"
            if (!change.isNaN()) {
                tvVeshahrChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvVeshahrChange, change)
            } else {
                tvVeshahrChange.text = "--%"
                tvVeshahrChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "veshahr data not found in response")
        }

        // Vepasar
        val vepasar = iranianStockIndexData?.optJSONObject("vepasar")
        if (vepasar != null) {
            val price = vepasar.optDouble("price", Double.NaN)
            val change = vepasar.optDouble("change", Double.NaN)
            if (!price.isNaN()) tvVepasarPrice.text = "${formatter.format(price)} Toman" else tvVepasarPrice.text = "-- Toman"
            if (!change.isNaN()) {
                tvVepasarChange.text = changeFormatter.format(change / 100.0)
                setChangeTextColor(tvVepasarChange, change)
            } else {
                tvVepasarChange.text = "--%"
                tvVepasarChange.setTextColor(requireContext().getColor(R.color.gray))
            }
        } else {
            Log.e(TAG, "vepasar data not found in response")
        }
    }

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