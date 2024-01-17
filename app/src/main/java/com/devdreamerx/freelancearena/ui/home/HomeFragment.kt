package com.devdreamerx.freelancearena.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devdreamerx.freelancearena.R
import com.devdreamerx.freelancearena.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentUserUid: String
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        firestore = FirebaseFirestore.getInstance()

        fetchUserScoreFromFirestore()

        return view
    }

    private fun fetchUserScoreFromFirestore() {
        val usersCollection = firestore.collection("users")

        usersCollection.document(currentUserUid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Retrieve the score value from the document
                    val userScore = documentSnapshot.getLong("score") ?: 0
                    binding.actualScoreHome.text = userScore.toString()
                } else {
                    Log.e("HomeFragment", "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error fetching user score: ${e.message}")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding reference to null to avoid potential memory leaks
        _binding = null
    }
}