package com.example.yadatracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.yadatracker.fragments.BourseFragment
import com.example.yadatracker.fragments.CoinsFragment
import com.example.yadatracker.fragments.CryptoFragment
import com.example.yadatracker.fragments.GoldExchangeFragment // Keep GoldExchangeFragment for now
import com.example.yadatracker.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    // Custom Bottom Navigation Tab Layouts
    private lateinit var tabCoins: LinearLayout
    private lateinit var iconCoins: ImageView
    private lateinit var labelCoins: TextView

    // Renamed for consistency with menu item ID and new XML IDs
    private lateinit var tabExchangeRate: LinearLayout
    private lateinit var iconExchangeRate: ImageView
    private lateinit var labelExchangeRate: TextView

    private lateinit var tabHome: LinearLayout
    private lateinit var iconHome: ImageView
    private lateinit var labelHome: TextView

    private lateinit var tabBourse: LinearLayout
    private lateinit var iconBourse: ImageView
    private lateinit var labelBourse: TextView

    private lateinit var tabCrypto: LinearLayout
    private lateinit var iconCrypto: ImageView
    private lateinit var labelCrypto: TextView

    private var activeTab: String = "home" // Default active tab is home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Custom Bottom Navigation Tab Layouts and their children
        tabCoins = findViewById(R.id.tab_coins)
        iconCoins = findViewById(R.id.icon_coins)
        labelCoins = findViewById(R.id.label_coins)

        // Initialize Exchange Rate tab with new IDs
        tabExchangeRate = findViewById(R.id.tab_exchange_rate)
        iconExchangeRate = findViewById(R.id.icon_exchange_rate)
        labelExchangeRate = findViewById(R.id.label_exchange_rate)

        tabHome = findViewById(R.id.tab_home)
        iconHome = findViewById(R.id.icon_home)
        labelHome = findViewById(R.id.label_home)

        tabBourse = findViewById(R.id.tab_bourse)
        iconBourse = findViewById(R.id.icon_bourse)
        labelBourse = findViewById(R.id.label_bourse)

        tabCrypto = findViewById(R.id.tab_crypto)
        iconCrypto = findViewById(R.id.icon_crypto)
        labelCrypto = findViewById(R.id.label_crypto)

        // Set up custom Bottom Navigation listeners
        tabCoins.setOnClickListener { selectTab("coins") }
        // Corrected the string ID to match the new XML ID and for future consistency
        tabExchangeRate.setOnClickListener { selectTab("exchange-rate") }
        tabHome.setOnClickListener { selectTab("home") }
        tabBourse.setOnClickListener { selectTab("bourse") }
        tabCrypto.setOnClickListener { selectTab("crypto") }

        // Initial selection and UI update for bottom nav
        if (savedInstanceState == null) {
            // Load the default fragment (e.g., Home or Exchange)
            // If you want "Exchange" to be the initial tab, set it here:
            // selectTab("exchange-rate")
            // Otherwise, keep it as "home" or your desired initial tab
            selectTab(activeTab) // Loads home fragment initially based on activeTab default
        }
    }

    private fun selectTab(tabId: String) {
        // Allow re-selecting home to refresh, but prevent unnecessary reloads for others
        if (activeTab == tabId && tabId != "home") {
            return
        }

        activeTab = tabId
        updateTabAppearance() // Update visual appearance of tabs

        val fragment: Fragment = when (tabId) {
            "coins" -> CoinsFragment()
            "exchange-rate" -> GoldExchangeFragment() // Corrected string ID to match the click listener
            "bourse" -> BourseFragment()
            "crypto" -> CryptoFragment()
            "home" -> HomeFragment()
            else -> HomeFragment() // Default to HomeFragment for any unhandled tabId
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateTabAppearance() {
        val tabs = mapOf(
            "coins" to Pair(tabCoins, iconCoins to labelCoins),
            "exchange-rate" to Pair(tabExchangeRate, iconExchangeRate to labelExchangeRate), // Updated with new IDs
            "home" to Pair(tabHome, iconHome to labelHome),
            "bourse" to Pair(tabBourse, iconBourse to labelBourse),
            "crypto" to Pair(tabCrypto, iconCrypto to labelCrypto)
        )

        val normalIconSize = resources.getDimensionPixelSize(R.dimen.normal_icon_size)
        val homeIconSize = resources.getDimensionPixelSize(R.dimen.home_icon_size)

        for ((id, tabPair) in tabs) {
            val tabLayout = tabPair.first
            val iconView = tabPair.second.first
            val labelView = tabPair.second.second

            val isCenter = (id == "home")
            val isActive = (activeTab == id)

            if (isCenter) {
                // Apply custom background for the Home tab
                tabLayout.setBackgroundResource(R.drawable.home_tab_background)
                iconView.setColorFilter(Color.WHITE) // Set icon to white
                labelView.setTextColor(Color.WHITE) // Set text to white

                // Adjust size for home icon (larger)
                val params = iconView.layoutParams
                params.width = homeIconSize
                params.height = homeIconSize
                iconView.layoutParams = params
            } else {
                // Apply background for active/inactive non-home tabs
                tabLayout.setBackgroundResource(
                    if (isActive) R.drawable.tab_active_background
                    else android.R.color.transparent // No specific background for inactive, relies on ripple
                )
                iconView.setColorFilter(
                    if (isActive) getColor(R.color.app_primary) // Primary color for active icons
                    else getColor(R.color.text_muted_foreground) // Muted color for inactive icons
                )
                labelView.setTextColor(
                    if (isActive) getColor(R.color.app_primary) // Primary color for active labels
                    else getColor(R.color.text_muted_foreground) // Muted color for inactive labels
                )
                // Reset size for other icons
                val params = iconView.layoutParams
                params.width = normalIconSize
                params.height = normalIconSize
                iconView.layoutParams = params
            }
        }
    }
}
