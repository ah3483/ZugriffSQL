package com.fitnessapp.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitnessapp.FitnessApp
import com.fitnessapp.R
import com.fitnessapp.data.model.WorkoutSession
import com.fitnessapp.databinding.FragmentHistoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((requireActivity().application as FitnessApp).repository)
    }

    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter(
            onClick = { session ->
                val bundle = Bundle().apply { putLong("sessionId", session.id) }
                findNavController().navigate(R.id.action_history_to_historyDetail, bundle)
            },
            onDeleteClick = { session ->
                confirmDelete(session)
            }
        )

        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter

        viewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            adapter.submitList(sessions)
            binding.textEmpty.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerHistory.visibility = if (sessions.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun confirmDelete(session: WorkoutSession) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete)
            .setMessage("Dieses Training wirklich löschen?")
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteSession(session)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
