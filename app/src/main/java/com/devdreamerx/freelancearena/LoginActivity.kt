package com.devdreamerx.freelancearena

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devdreamerx.freelancearena.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firebase authentication
        mAuth = FirebaseAuth.getInstance()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        binding.LoginBtn.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()

            // Check if email and password are not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Please fill the required details", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }

    }


    private fun login(registrationNumber: String, password: String) {
        mAuth.signInWithEmailAndPassword(generateEmailFromRegistration(registrationNumber), password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.message ?: "User does not exist"
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun generateEmailFromRegistration(registrationNumber: String): String {
        return "$registrationNumber@yourdomain.com"
    }

}