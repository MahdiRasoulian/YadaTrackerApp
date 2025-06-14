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
import com.example.yadatracker.fragments.GoldExchangeFragment
import com.example.yadatracker.fragments.HomeFragment // Import the new HomeFragment

class MainActivity : AppCompatActivity() {

    // Custom Bottom Navigation Tab Layouts
    private lateinit var tabCoins: LinearLayout
    private lateinit var iconCoins: ImageView
    private lateinit var labelCoins: TextView

    private lateinit var tabGoldExchange: LinearLayout
    private lateinit var iconGoldExchange: ImageView
    private lateinit var labelGoldExchange: TextView

    private lateinit var tabHome: LinearLayout // This is the tabHome being referenced
    private lateinit var iconHome: ImageView // This is the iconHome being referenced
    private lateinit var labelHome: TextView // This is the labelHome being referenced

    // Renamed from Gold Ounce to Bourse
    private lateinit var tabBourse: LinearLayout
    private lateinit var iconBourse: ImageView
    private lateinit var labelBourse: TextView // CORRECTED: Removed extra 'var' keyword

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

        tabGoldExchange = findViewById(R.id.tab_gold_exchange)
        iconGoldExchange = findViewById(R.id.icon_gold_exchange)
        labelGoldExchange = findViewById(R.id.label_gold_exchange)

        // These are correct references to elements in activity_main.xml
        tabHome = findViewById(R.id.tab_home)
        iconHome = findViewById(R.id.icon_home)
        labelHome = findViewById(R.id.label_home)

        // Initialize Bourse tab (formerly Gold Ounce)
        tabBourse = findViewById(R.id.tab_bourse)
        iconBourse = findViewById(R.id.icon_bourse)
        labelBourse = findViewById(R.id.label_bourse)

        tabCrypto = findViewById(R.id.tab_crypto)
        iconCrypto = findViewById(R.id.icon_crypto)
        labelCrypto = findViewById(R.id.label_crypto)

        // Set up custom Bottom Navigation listeners
        tabCoins.setOnClickListener { selectTab("coins") }
        tabGoldExchange.setOnClickListener { selectTab("gold-exchange") }
        tabHome.setOnClickListener { selectTab("home") }
        tabBourse.setOnClickListener { selectTab("bourse") }
        tabCrypto.setOnClickListener { selectTab("crypto") }

        // Initial selection and UI update for bottom nav
        if (savedInstanceState == null) {
            selectTab(activeTab) // Load home fragment initially
        }
    }

    private fun selectTab(tabId: String) {
        if (activeTab == tabId && tabId != "home") {
            // If the same tab (and not home) is clicked, do nothing
            return
        }

        activeTab = tabId
        updateTabAppearance() // Update visual appearance of tabs

        val fragment: Fragment = when (tabId) {
            "coins" -> CoinsFragment()
            "Goldexchange" -> GoldExchangeFragment()
            "bourse" -> BourseFragment()
            "crypto" -> CryptoFragment()
            "home" -> HomeFragment() // Load HomeFragment for the main content
            else -> HomeFragment() // Default to HomeFragment
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateTabAppearance() {
        val tabs = mapOf(
            "coins" to Pair(tabCoins, iconCoins to labelCoins),
            "gold-exchange" to Pair(tabGoldExchange, iconGoldExchange to labelGoldExchange),
            "home" to Pair(tabHome, iconHome to labelHome),
            "bourse" to Pair(tabBourse, iconBourse to labelBourse), // Updated to Bourse
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