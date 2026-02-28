package com.fitnessapp.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnessapp.data.model.WorkoutSession
import com.fitnessapp.databinding.ItemHistorySessionBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HistoryAdapter(
    private val onClick: (WorkoutSession) -> Unit,
    private val onDeleteClick: (WorkoutSession) -> Unit
) : ListAdapter<WorkoutSession, HistoryAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY)

    inner class ViewHolder(private val binding: ItemHistorySessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: WorkoutSession) {
            binding.textSessionName.text = session.templateName
            binding.textSessionDate.text = dateFormat.format(Date(session.startedAt))

            if (session.completedAt != null) {
                val durationMs = session.completedAt - session.startedAt
                val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
                binding.textSessionDuration.text = String.format("Dauer: %d:%02d min", minutes, seconds)
            } else {
                binding.textSessionDuration.text = "Nicht abgeschlossen"
            }

            binding.root.setOnClickListener { onClick(session) }
            binding.btnDeleteSession.setOnClickListener { onDeleteClick(session) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistorySessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<WorkoutSession>() {
        override fun areItemsTheSame(oldItem: WorkoutSession, newItem: WorkoutSession) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: WorkoutSession, newItem: WorkoutSession) = oldItem == newItem
    }
}
