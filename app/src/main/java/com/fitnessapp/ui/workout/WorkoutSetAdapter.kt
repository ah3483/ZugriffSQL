package com.fitnessapp.ui.workout

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fitnessapp.R
import com.fitnessapp.databinding.ItemWorkoutSetBinding

class WorkoutSetAdapter(
    private val exerciseIndex: Int,
    private val sets: List<SetResult>,
    private val onSetToggled: (exerciseIndex: Int, setIndex: Int) -> Unit,
    private val onSetValuesChanged: (exerciseIndex: Int, setIndex: Int, reps: Int, weight: Double) -> Unit
) : RecyclerView.Adapter<WorkoutSetAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemWorkoutSetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isUpdating = false

        fun bind(setResult: SetResult, setIndex: Int) {
            isUpdating = true
            binding.textSetNumber.text = String.format("Satz %d", setIndex + 1)
            binding.editWeight.setText(if (setResult.weight > 0) String.format("%.1f", setResult.weight) else "")
            binding.editReps.setText(if (setResult.reps > 0) setResult.reps.toString() else "")
            binding.checkboxCompleted.isChecked = setResult.completed
            updateBackground(setResult.completed)
            isUpdating = false

            binding.checkboxCompleted.setOnCheckedChangeListener { _, _ ->
                if (!isUpdating) {
                    onSetToggled(exerciseIndex, setIndex)
                }
            }

            binding.editWeight.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isUpdating) {
                        val weight = s.toString().toDoubleOrNull() ?: 0.0
                        val reps = binding.editReps.text.toString().toIntOrNull() ?: 0
                        onSetValuesChanged(exerciseIndex, setIndex, reps, weight)
                    }
                }
            })

            binding.editReps.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isUpdating) {
                        val reps = s.toString().toIntOrNull() ?: 0
                        val weight = binding.editWeight.text.toString().toDoubleOrNull() ?: 0.0
                        onSetValuesChanged(exerciseIndex, setIndex, reps, weight)
                    }
                }
            })
        }

        private fun updateBackground(completed: Boolean) {
            val color = if (completed) {
                ContextCompat.getColor(binding.root.context, R.color.set_completed)
            } else {
                ContextCompat.getColor(binding.root.context, android.R.color.transparent)
            }
            binding.root.setBackgroundColor(color)
            binding.root.alpha = if (completed) 0.7f else 1.0f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkoutSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sets[position], position)
    }

    override fun getItemCount(): Int = sets.size
}
