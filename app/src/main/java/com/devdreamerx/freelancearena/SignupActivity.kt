package com.devdreamerx.freelancearena

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.devdreamerx.freelancearena.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var mAuth: FirebaseAuth
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        binding.Signupbtn.setOnClickListener {
            intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }


        binding.btnSignup.setOnClickListener{
            val name = binding.edtNameSignup.text.toString()
            val email = binding.edtEmailSignup.text.toString()
            val password = binding.edtPasswordSignup.text.toString()

//             Check if email and password are not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@SignupActivity, "Please fill the required details", Toast.LENGTH_SHORT).show()
            } else {
                signUp(name, email, password)
            }
        }

        binding.edtNameSignup.setOnClickListener { startElevationAnimation(binding.edtNameSignup) }
        binding.edtEmailSignup.setOnClickListener { startElevationAnimation(binding.edtEmailSignup) }
        binding.edtPasswordSignup.setOnClickListener { startElevationAnimation(binding.edtPasswordSignup) }

        binding.switchLightDarkMode.setOnClickListener {
            toggleNightMode()
        }

    }

//    private fun signUp(registrationNumber: String, password: String) {
//        mAuth.createUserWithEmailAndPassword(generateEmailFromRegistration(registrationNumber), password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = mAuth.currentUser
//                    user?.let {
//                        // Set registration number as display name or custom claim
//                        // For example, you can set it as display name
//                        val profileUpdates = UserProfileChangeRequest.Builder()
//                            .setDisplayName(registrationNumber)
//                            .build()
//
//                        it.updateProfile(profileUpdates)
//                            .addOnCompleteListener { /* handle update completion if needed */ }
//                    }
//
//                    val intent = Intent(this@SignupActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else {
//                    val errorMessage = task.exception?.message ?: "Registration failed"
//                    Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//    }


    private fun signUp(name: String, registrationNumber: String, password: String) {
        mAuth.createUserWithEmailAndPassword(generateEmailFromRegistration(registrationNumber), password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    user?.let {
                        // Set registration number as display name or custom claim
                        // For example, you can set it as display name
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(registrationNumber)
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { /* handle update completion if needed */ }

                        // Obtain UID
                        val uid = user.uid

                        // Create Firestore document in "users" collection with UID
                        val usersCollection = FirebaseFirestore.getInstance().collection("users")
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to user.email,  // You may want to store the actual email for reference
                            // Add other fields as needed
                            "score" to 0  // Default score is set to 0
                        )

                        usersCollection.document(uid).set(userData)
                            .addOnSuccessListener {
                                Log.d("SignupActivity", "Firestore document created successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("SignupActivity", "Error creating Firestore document: ${e.message}")
                            }
                    }

                    val intent = Intent(this@SignupActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }


    // Generate a fake email from registration number for Firebase authentication
    private fun generateEmailFromRegistration(registrationNumber: String): String {
        return "$registrationNumber@yourdomain.com"
    }


    private fun startElevationAnimation(view: View) {
        val animatorSet = AnimatorInflater.loadAnimator(this, R.animator.elevation_change) as AnimatorSet
        animatorSet.setTarget(view)
        animatorSet.start()
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