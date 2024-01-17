package com.devdreamerx.freelancearena

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        window.statusBarColor = ContextCompat.getColor(this, R.color.moderate_blue)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mAuth = FirebaseAuth.getInstance()

        updateRegistrationNumber()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
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
}
