package com.devdreamerx.freelancearena

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devdreamerx.freelancearena.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var mAuth: FirebaseAuth
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

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        binding.btnSignup.setOnClickListener{
            val email = binding.edtEmailSignup.text.toString()
            val password = binding.edtPasswordSignup.text.toString()

//             Check if email and password are not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@SignupActivity, "Please fill the required details", Toast.LENGTH_SHORT).show()
            } else {
                signUp(email, password)
            }
        }

        binding.edtNameSignup.setOnClickListener { startElevationAnimation(binding.edtNameSignup) }
        binding.edtEmailSignup.setOnClickListener { startElevationAnimation(binding.edtEmailSignup) }
        binding.edtPasswordSignup.setOnClickListener { startElevationAnimation(binding.edtPasswordSignup) }


    }

    private fun signUp(registrationNumber: String, password: String) {
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
}