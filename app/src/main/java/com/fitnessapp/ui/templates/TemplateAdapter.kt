package com.fitnessapp.ui.templates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnessapp.data.model.WorkoutTemplate
import com.fitnessapp.databinding.ItemTemplateBinding

class TemplateAdapter(
    private val onEditClick: (WorkoutTemplate) -> Unit,
    private val onStartClick: (WorkoutTemplate) -> Unit,
    private val onDeleteClick: (WorkoutTemplate) -> Unit
) : ListAdapter<WorkoutTemplate, TemplateAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(template: WorkoutTemplate) {
            binding.textTemplateName.text = template.name
            if (template.description.isNotBlank()) {
                binding.textTemplateDescription.text = template.description
                binding.textTemplateDescription.visibility = View.VISIBLE
            } else {
                binding.textTemplateDescription.visibility = View.GONE
            }

            binding.btnEditTemplate.setOnClickListener { onEditClick(template) }
            binding.btnStartWorkout.setOnClickListener { onStartClick(template) }
            binding.btnDeleteTemplate.setOnClickListener { onDeleteClick(template) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<WorkoutTemplate>() {
        override fun areItemsTheSame(oldItem: WorkoutTemplate, newItem: WorkoutTemplate) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: WorkoutTemplate, newItem: WorkoutTemplate) = oldItem == newItem
    }
}
