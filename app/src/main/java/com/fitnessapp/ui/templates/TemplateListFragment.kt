package com.fitnessapp.ui.templates

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
import com.fitnessapp.data.model.WorkoutTemplate
import com.fitnessapp.databinding.FragmentTemplateListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TemplateListFragment : Fragment() {

    private var _binding: FragmentTemplateListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TemplateListViewModel by viewModels {
        TemplateListViewModelFactory((requireActivity().application as FitnessApp).repository)
    }

    private lateinit var adapter: TemplateAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTemplateListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TemplateAdapter(
            onEditClick = { template ->
                val bundle = Bundle().apply { putLong("templateId", template.id) }
                findNavController().navigate(R.id.action_templateList_to_templateEdit, bundle)
            },
            onStartClick = { template ->
                val bundle = Bundle().apply { putLong("templateId", template.id) }
                findNavController().navigate(R.id.action_templateList_to_workout, bundle)
            },
            onDeleteClick = { template ->
                confirmDelete(template)
            }
        )

        binding.recyclerTemplates.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTemplates.adapter = adapter

        viewModel.allTemplates.observe(viewLifecycleOwner) { templates ->
            adapter.submitList(templates)
            binding.textEmpty.visibility = if (templates.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerTemplates.visibility = if (templates.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.fabAddTemplate.setOnClickListener {
            findNavController().navigate(R.id.action_templateList_to_templateEdit)
        }
    }

    private fun confirmDelete(template: WorkoutTemplate) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete)
            .setMessage(R.string.confirm_delete_template)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTemplate(template)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
