package com.devdreamerx.freelancearena.ui.feedback

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devdreamerx.freelancearena.R
import com.devdreamerx.freelancearena.databinding.FragmentFeedbackBinding

class Feedbackfragment : Fragment() {

    companion object {
        fun newInstance() = Feedbackfragment()
    }

    private val viewModel: FeedbackfragmentViewModel by viewModels()
    private lateinit var binding: FragmentFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }
}