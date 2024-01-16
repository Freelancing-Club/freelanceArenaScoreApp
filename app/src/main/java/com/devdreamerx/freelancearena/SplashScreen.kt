package com.devdreamerx.freelancearena

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devdreamerx.freelancearena.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val splashDelay: Long = 1500
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBarSplash)

        mAuth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthentication()
        }, splashDelay)
    }

    private fun checkAuthentication() {
        val user = mAuth.currentUser
        if (user != null) {
            // User is already authenticated, go to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is not authenticated, go to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}