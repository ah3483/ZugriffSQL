package com.fitnessapp.ui.workout

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitnessapp.FitnessApp
import com.fitnessapp.R
import com.fitnessapp.databinding.FragmentWorkoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory((requireActivity().application as FitnessApp).repository)
    }

    private lateinit var adapter: WorkoutExerciseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val templateId = arguments?.getLong("templateId") ?: return

        adapter = WorkoutExerciseAdapter(
            onSetToggled = { exIdx, setIdx ->
                viewModel.toggleSetCompleted(exIdx, setIdx)
            },
            onSetValuesChanged = { exIdx, setIdx, reps, weight ->
                viewModel.updateSetValues(exIdx, setIdx, reps, weight)
            }
        )

        binding.recyclerWorkoutExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerWorkoutExercises.adapter = adapter

        binding.chronometer.base = SystemClock.elapsedRealtime()
        binding.chronometer.start()

        viewModel.startWorkoutFromTemplate(templateId)

        viewModel.activeExercises.observe(viewLifecycleOwner) { exercises ->
            if (exercises.isNotEmpty()) {
                binding.textWorkoutTitle.text = "Training"
                adapter.submitList(exercises.toList())
            }
        }

        binding.btnFinishWorkout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_workout)
                .setMessage(R.string.confirm_finish_workout)
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.finishWorkout()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }

        viewModel.workoutFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                binding.chronometer.stop()
                Toast.makeText(requireContext(), getString(R.string.workout_completed), Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
