package com.fitnessapp.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitnessapp.FitnessApp
import com.fitnessapp.R
import com.fitnessapp.data.model.CompletedExerciseWithSets
import com.fitnessapp.databinding.FragmentHistoryDetailBinding
import com.fitnessapp.databinding.ItemHistoryExerciseBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HistoryDetailFragment : Fragment() {

    private var _binding: FragmentHistoryDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((requireActivity().application as FitnessApp).repository)
    }

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionId = arguments?.getLong("sessionId") ?: return

        val adapter = HistoryExerciseAdapter()
        binding.recyclerExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerExercises.adapter = adapter

        viewModel.loadSessionDetail(sessionId)
        viewModel.sessionDetail.observe(viewLifecycleOwner) { sessionWithEx ->
            if (sessionWithEx != null) {
                binding.textSessionName.text = sessionWithEx.session.templateName
                binding.textSessionDate.text = dateFormat.format(Date(sessionWithEx.session.startedAt))

                val completedAt = sessionWithEx.session.completedAt
                if (completedAt != null) {
                    val durationMs = completedAt - sessionWithEx.session.startedAt
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
                    binding.textSessionDuration.text = String.format("Dauer: %d:%02d min", minutes, seconds)
                } else {
                    binding.textSessionDuration.text = "Nicht abgeschlossen"
                }

                adapter.submitList(sessionWithEx.exercises.sortedBy { it.exercise.orderIndex })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
