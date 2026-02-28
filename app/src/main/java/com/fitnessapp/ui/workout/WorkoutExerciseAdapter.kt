package com.fitnessapp.ui.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fitnessapp.databinding.ItemWorkoutExerciseBinding

class WorkoutExerciseAdapter(
    private val onSetToggled: (exerciseIndex: Int, setIndex: Int) -> Unit,
    private val onSetValuesChanged: (exerciseIndex: Int, setIndex: Int, reps: Int, weight: Double) -> Unit
) : RecyclerView.Adapter<WorkoutExerciseAdapter.ViewHolder>() {

    private var exercises: List<ActiveExercise> = emptyList()

    fun submitList(list: List<ActiveExercise>) {
        exercises = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemWorkoutExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: ActiveExercise, exerciseIndex: Int) {
            binding.textExerciseName.text = exercise.name

            val setAdapter = WorkoutSetAdapter(
                exerciseIndex = exerciseIndex,
                sets = exercise.completedSets,
                onSetToggled = onSetToggled,
                onSetValuesChanged = onSetValuesChanged
            )
            binding.recyclerSets.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerSets.adapter = setAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkoutExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exercises[position], position)
    }

    override fun getItemCount(): Int = exercises.size
}
