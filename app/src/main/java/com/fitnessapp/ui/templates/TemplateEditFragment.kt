package com.fitnessapp.ui.templates

import android.os.Bundle
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
import com.fitnessapp.databinding.FragmentTemplateEditBinding

class TemplateEditFragment : Fragment() {

    private var _binding: FragmentTemplateEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TemplateEditViewModel by viewModels {
        TemplateEditViewModelFactory((requireActivity().application as FitnessApp).repository)
    }

    private lateinit var exerciseAdapter: ExerciseTemplateAdapter
    private var templateId: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTemplateEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        templateId = arguments?.getLong("templateId") ?: 0

        exerciseAdapter = ExerciseTemplateAdapter { exercise ->
            viewModel.deleteExercise(exercise)
        }

        binding.recyclerExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerExercises.adapter = exerciseAdapter

        if (templateId > 0) {
            binding.textTitle.text = getString(R.string.edit_template)
            viewModel.loadTemplate(templateId)

            viewModel.templateWithExercises.observe(viewLifecycleOwner) { templateWithEx ->
                if (templateWithEx != null) {
                    binding.editTemplateName.setText(templateWithEx.template.name)
                    binding.editTemplateDescription.setText(templateWithEx.template.description)
                }
            }

            viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
                exerciseAdapter.submitList(exercises)
            }
        }

        binding.btnSaveTemplate.setOnClickListener {
            val name = binding.editTemplateName.text.toString().trim()
            if (name.isBlank()) {
                binding.editTemplateName.error = "Name erforderlich"
                return@setOnClickListener
            }
            val description = binding.editTemplateDescription.text.toString().trim()
            viewModel.saveTemplate(name, description)
        }

        viewModel.savedTemplateId.observe(viewLifecycleOwner) { savedId ->
            if (savedId > 0 && templateId == 0L) {
                templateId = savedId
                viewModel.loadTemplate(savedId)
                binding.textTitle.text = getString(R.string.edit_template)
                Toast.makeText(requireContext(), "Vorlage gespeichert", Toast.LENGTH_SHORT).show()

                viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
                    exerciseAdapter.submitList(exercises)
                }
            } else if (savedId > 0) {
                Toast.makeText(requireContext(), "Vorlage aktualisiert", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddExercise.setOnClickListener {
            if (templateId <= 0) {
                Toast.makeText(requireContext(), getString(R.string.save_template_first), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val exerciseName = binding.editExerciseName.text.toString().trim()
            if (exerciseName.isBlank()) {
                binding.editExerciseName.error = "Name erforderlich"
                return@setOnClickListener
            }

            val sets = binding.editSets.text.toString().toIntOrNull() ?: 3
            val reps = binding.editReps.text.toString().toIntOrNull() ?: 10
            val weight = binding.editWeight.text.toString().toDoubleOrNull() ?: 0.0

            viewModel.addExercise(exerciseName, sets, reps, weight)

            binding.editExerciseName.text?.clear()
            binding.editSets.setText("3")
            binding.editReps.setText("10")
            binding.editWeight.setText("0")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
