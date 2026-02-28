package com.fitnessapp.ui.templates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnessapp.data.model.ExerciseTemplate
import com.fitnessapp.databinding.ItemExerciseTemplateBinding

class ExerciseTemplateAdapter(
    private val onDeleteClick: (ExerciseTemplate) -> Unit
) : ListAdapter<ExerciseTemplate, ExerciseTemplateAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemExerciseTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: ExerciseTemplate) {
            binding.textExerciseName.text = exercise.name
            binding.textExerciseDetail.text = String.format(
                "%d Sätze x %d Wdh @ %.1f kg",
                exercise.sets, exercise.reps, exercise.weight
            )
            binding.btnDeleteExercise.setOnClickListener { onDeleteClick(exercise) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ExerciseTemplate>() {
        override fun areItemsTheSame(oldItem: ExerciseTemplate, newItem: ExerciseTemplate) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExerciseTemplate, newItem: ExerciseTemplate) = oldItem == newItem
    }
}
