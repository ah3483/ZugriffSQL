package com.fitnessapp.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnessapp.R
import com.fitnessapp.data.model.CompletedExerciseWithSets
import com.fitnessapp.databinding.ItemHistoryExerciseBinding

class HistoryExerciseAdapter :
    ListAdapter<CompletedExerciseWithSets, HistoryExerciseAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemHistoryExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exerciseWithSets: CompletedExerciseWithSets) {
            binding.textExerciseName.text = exerciseWithSets.exercise.exerciseName
            binding.layoutSets.removeAllViews()

            exerciseWithSets.sets.sortedBy { it.setNumber }.forEach { set ->
                val textView = TextView(binding.root.context).apply {
                    text = String.format(
                        "Satz %d: %.1f kg x %d Wdh %s",
                        set.setNumber,
                        set.weight,
                        set.reps,
                        if (set.completed) "✓" else "✗"
                    )
                    textSize = 14f
                    setTextColor(context.getColor(R.color.text_secondary))
                    setPadding(0, 4, 0, 4)
                }
                binding.layoutSets.addView(textView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CompletedExerciseWithSets>() {
        override fun areItemsTheSame(oldItem: CompletedExerciseWithSets, newItem: CompletedExerciseWithSets) =
            oldItem.exercise.id == newItem.exercise.id
        override fun areContentsTheSame(oldItem: CompletedExerciseWithSets, newItem: CompletedExerciseWithSets) =
            oldItem == newItem
    }
}
