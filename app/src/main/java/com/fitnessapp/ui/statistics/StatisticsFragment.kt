package com.fitnessapp.ui.statistics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitnessapp.R
import com.fitnessapp.databinding.FragmentStatisticsBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()
    private val recordAdapter = PersonalRecordAdapter()

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importDatabase(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPersonalRecords.layoutManager = LinearLayoutManager(context)
        binding.rvPersonalRecords.adapter = recordAdapter

        observeData()
        setupButtons()
    }

    private fun observeData() {
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            if (summary != null) {
                binding.tvTotalSessions.text = "${summary.totalSessions}"
                binding.tvTotalExercises.text = "${summary.totalExercises}"
                binding.tvTotalSets.text = "${summary.totalSets}"
                binding.tvAvgDuration.text = String.format("%.0f min", summary.avgDurationMinutes)
            }
        }

        viewModel.totalVolume.observe(viewLifecycleOwner) { volume ->
            binding.tvTotalVolume.text = String.format("%.0f kg", volume ?: 0.0)
        }

        viewModel.mostUsedTemplate.observe(viewLifecycleOwner) { template ->
            binding.tvMostUsedTemplate.text = template ?: "-"
        }

        viewModel.last30DaysSessions.observe(viewLifecycleOwner) { count ->
            binding.tvLast30Days.text = "$count"
        }

        viewModel.personalRecords.observe(viewLifecycleOwner) { records ->
            recordAdapter.submitList(records)
            binding.tvNoRecords.visibility = if (records.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.monthlyStats.observe(viewLifecycleOwner) { stats ->
            if (!stats.isNullOrEmpty()) {
                val sb = StringBuilder()
                for (s in stats) {
                    sb.appendLine("${s.month}: ${s.sessionCount} Trainings, ${s.totalSets} Sätze")
                }
                binding.tvMonthlyStats.text = sb.toString().trim()
            } else {
                binding.tvMonthlyStats.text = "Keine Daten"
            }
        }

        viewModel.exerciseNames.observe(viewLifecycleOwner) { names ->
            if (!names.isNullOrEmpty()) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerExercise.adapter = adapter
                binding.spinnerExercise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                        viewModel.selectExercise(names[pos])
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                binding.progressSection.visibility = View.VISIBLE
            } else {
                binding.progressSection.visibility = View.GONE
            }
        }

        viewModel.exerciseProgress.observe(viewLifecycleOwner) { progress ->
            if (!progress.isNullOrEmpty()) {
                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.GERMANY)
                val sb = StringBuilder()
                for (p in progress) {
                    sb.appendLine("${dateFormat.format(Date(p.date))}: ${String.format("%.1f", p.maxWeight)} kg, ${p.maxReps} Wdh")
                }
                binding.tvProgressData.text = sb.toString().trim()
            } else {
                binding.tvProgressData.text = "Keine Daten"
            }
        }

        viewModel.exportResult.observe(viewLifecycleOwner) { json ->
            if (json != null) {
                shareExport(json)
                viewModel.clearExportResult()
            }
        }

        viewModel.importResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                viewModel.clearImportResult()
            }
        }
    }

    private fun setupButtons() {
        binding.btnExport.setOnClickListener {
            viewModel.exportDatabase()
        }

        binding.btnImport.setOnClickListener {
            importLauncher.launch("application/json")
        }
    }

    private fun shareExport(json: String) {
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY)
            val fileName = "fitness_export_${dateFormat.format(Date())}.json"
            val file = File(requireContext().cacheDir, fileName)
            file.writeText(json)

            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Daten exportieren"))
        } catch (e: Exception) {
            Toast.makeText(context, "Export fehlgeschlagen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
