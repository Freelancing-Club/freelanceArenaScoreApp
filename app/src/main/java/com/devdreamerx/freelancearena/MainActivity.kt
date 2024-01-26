package com.devdreamerx.freelancearena

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.devdreamerx.freelancearena.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserUid: String

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        //Will be checked later. (Under Review)

//        val statusBarColor: Int
//        val statusBarTextDark: Boolean
//
//        if (isNightModeEnabled()) {
//            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.black)
//            statusBarTextDark = false // White text on dark background
//        } else {
//            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.moderate_blue)
//            statusBarTextDark = true // Black text on light background
//        }
//
//        // Set the status bar color
//        window.statusBarColor = statusBarColor
//
//        // Set the status bar text color
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val decor = window.decorView
//            if (statusBarTextDark) {
//                // If night mode is enabled, set the status bar text to be light
//                decor.systemUiVisibility =
//                    decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            } else {
//                // If night mode is not enabled, set the status bar text to be dark
//                decor.systemUiVisibility = decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
//            }
//        }



        binding.appBarMain.fab.setOnClickListener { view ->
            // Toggle the night mode (Replace this with your actual code to toggle night mode)
            toggleNightMode()

            // Show a Snackbar with the mode message
            val isNightMode = isNightModeEnabled()/* logic to determine current mode */
            val modeMessage = if (isNightMode) "Switched to Dark Mode" else "Switched to Light Mode"

            Snackbar.make(view, modeMessage, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_aboutus
            ), drawerLayout )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        updateRegistrationNumber()
        fetchUserName()

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }
//

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        // Assuming you have only one item for demonstration purposes
        val menuItem = menu.findItem(R.id.log_out_action)
        setCustomMenuItem(menuItem)
        return true
    }

    private fun setCustomMenuItem(menuItem: MenuItem) {
        menuItem.setActionView(R.layout.custom_menu_item)
        val actionView = menuItem.actionView
        val icon = actionView?.findViewById<ImageView>(R.id.menu_icon)
        val text = actionView?.findViewById<TextView>(R.id.menu_title)

        icon?.setImageResource(R.drawable.logout)
        if (text != null) {
            text.text = getString(R.string.log_out)
        }
    }



// Rest of your code remains unchanged


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out_action -> {
                // Handle log out here
                mAuth.signOut()
                // Redirect to login activity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Close the current activity

                // Display toast message
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateRegistrationNumber() {
        // Fetch the user's registration number from Firestore and update the TextView
        // Use similar code as in HomeFragment but with direct access to the TextView
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        val currentUserUid = mAuth.currentUser?.uid ?: ""

        usersCollection.document(currentUserUid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val email = documentSnapshot.getString("email") ?: ""
                    val regNumber = convertDummyEmailToIdentifier(email)
                    updateNavHeaderRegistrationNumber(regNumber)
                } else {
                    Log.e("MainActivity", "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error fetching user registration number: ${e.message}")
            }
    }

    private fun fetchUserName(){
        val usersCollection = firestore.collection("users")

        usersCollection.document(currentUserUid).get()
            .addOnSuccessListener {documentSnapshot ->
                if(documentSnapshot.exists()){
                    val userName = documentSnapshot.getString("name") ?: ""
                    updateNavHeaderUserName(userName)
                }else{
                    Log.e("MainActivity", "User document do not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error fetching user name: ${e.message}")
            }
    }
    private fun updateNavHeaderUserName(userName: String) {
        val navView: NavigationView = binding.navView
        val headerView: View = navView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.userNameNavHeader)
        userNameTextView.text = userName
    }

    private fun updateNavHeaderRegistrationNumber(regNumber: String) {
        val navView: NavigationView = binding.navView
        val headerView: View = navView.getHeaderView(0)
        val regNumberTextView: TextView = headerView.findViewById(R.id.regNumber)
        regNumberTextView.text = regNumber
    }

    private fun convertDummyEmailToIdentifier(dummyEmail: String): String {
        // Extract the student identifier from the email and convert to uppercase
        val parts = dummyEmail.split("@")
        val identifier = parts[0]
        return identifier.uppercase(Locale.getDefault())
    }

    private fun toggleNightMode() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // Currently in night mode, switch to day mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                // Currently in day mode, switch to night mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            // Unspecified or system default, toggle to night mode
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun isNightModeEnabled(): Boolean {
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        return when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_YES -> true
            else -> false
        }
    }


}
