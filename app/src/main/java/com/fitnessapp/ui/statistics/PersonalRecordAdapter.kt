package com.fitnessapp.ui.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnessapp.R
import com.fitnessapp.data.dao.ExercisePersonalRecord

class PersonalRecordAdapter :
    ListAdapter<ExercisePersonalRecord, PersonalRecordAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.tvExerciseName)
        val maxWeight: TextView = view.findViewById(R.id.tvMaxWeight)
        val maxReps: TextView = view.findViewById(R.id.tvMaxReps)
        val totalSets: TextView = view.findViewById(R.id.tvTotalSets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_personal_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        holder.exerciseName.text = record.exerciseName
        holder.maxWeight.text = String.format("%.1f kg", record.maxWeight)
        holder.maxReps.text = "${record.maxReps} Wdh"
        holder.totalSets.text = "${record.totalSets} Sätze"
    }

    class DiffCallback : DiffUtil.ItemCallback<ExercisePersonalRecord>() {
        override fun areItemsTheSame(a: ExercisePersonalRecord, b: ExercisePersonalRecord) =
            a.exerciseName == b.exerciseName
        override fun areContentsTheSame(a: ExercisePersonalRecord, b: ExercisePersonalRecord) =
            a == b
    }
}
