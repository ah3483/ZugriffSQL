package com.fitnessapp.data

import android.content.Context
import android.net.Uri
import com.fitnessapp.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class DatabaseAccessManager(private val database: FitnessDatabase) {

    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()

        val templateDao = database.workoutTemplateDao()
        val sessionDao = database.workoutSessionDao()

        val templates = templateDao.getAllTemplatesOnce()
        val templatesArray = JSONArray()
        for (t in templates) {
            val obj = JSONObject().apply {
                put("id", t.template.id)
                put("name", t.template.name)
                put("description", t.template.description)
                put("createdAt", t.template.createdAt)
            }
            val exercisesArray = JSONArray()
            for (e in t.exercises) {
                exercisesArray.put(JSONObject().apply {
                    put("id", e.id)
                    put("name", e.name)
                    put("sets", e.sets)
                    put("reps", e.reps)
                    put("weight", e.weight)
                    put("orderIndex", e.orderIndex)
                })
            }
            obj.put("exercises", exercisesArray)
            templatesArray.put(obj)
        }
        root.put("templates", templatesArray)

        val sessions = sessionDao.getAllSessionsOnce()
        val sessionsArray = JSONArray()
        for (s in sessions) {
            val obj = JSONObject().apply {
                put("id", s.session.id)
                put("templateId", s.session.templateId ?: JSONObject.NULL)
                put("templateName", s.session.templateName)
                put("startedAt", s.session.startedAt)
                put("completedAt", s.session.completedAt ?: JSONObject.NULL)
                put("notes", s.session.notes)
            }
            val exercisesArray = JSONArray()
            for (e in s.exercises) {
                val exObj = JSONObject().apply {
                    put("id", e.exercise.id)
                    put("exerciseName", e.exercise.exerciseName)
                    put("orderIndex", e.exercise.orderIndex)
                }
                val setsArray = JSONArray()
                for (set in e.sets) {
                    setsArray.put(JSONObject().apply {
                        put("setNumber", set.setNumber)
                        put("reps", set.reps)
                        put("weight", set.weight)
                        put("completed", set.completed)
                    })
                }
                exObj.put("sets", setsArray)
                exercisesArray.put(exObj)
            }
            obj.put("exercises", exercisesArray)
            sessionsArray.put(obj)
        }
        root.put("sessions", sessionsArray)
        root.put("exportedAt", System.currentTimeMillis())
        root.put("version", 1)

        root.toString(2)
    }

    suspend fun importFromJson(context: Context, uri: Uri): ImportResult = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext ImportResult(false, "Datei konnte nicht geöffnet werden")

            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonStr = reader.readText()
            reader.close()

            val root = JSONObject(jsonStr)
            var templatesImported = 0
            var sessionsImported = 0

            val templateDao = database.workoutTemplateDao()
            val sessionDao = database.workoutSessionDao()

            if (root.has("templates")) {
                val templates = root.getJSONArray("templates")
                for (i in 0 until templates.length()) {
                    val t = templates.getJSONObject(i)
                    val templateId = templateDao.insertTemplate(
                        WorkoutTemplate(
                            name = t.getString("name"),
                            description = t.optString("description", ""),
                            createdAt = t.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                    if (t.has("exercises")) {
                        val exercises = t.getJSONArray("exercises")
                        for (j in 0 until exercises.length()) {
                            val e = exercises.getJSONObject(j)
                            templateDao.insertExercise(
                                ExerciseTemplate(
                                    workoutTemplateId = templateId,
                                    name = e.getString("name"),
                                    sets = e.getInt("sets"),
                                    reps = e.getInt("reps"),
                                    weight = e.optDouble("weight", 0.0),
                                    orderIndex = e.optInt("orderIndex", j)
                                )
                            )
                        }
                    }
                    templatesImported++
                }
            }

            if (root.has("sessions")) {
                val sessions = root.getJSONArray("sessions")
                for (i in 0 until sessions.length()) {
                    val s = sessions.getJSONObject(i)
                    val sessionId = sessionDao.insertSession(
                        WorkoutSession(
                            templateName = s.getString("templateName"),
                            startedAt = s.getLong("startedAt"),
                            completedAt = if (s.isNull("completedAt")) null else s.getLong("completedAt"),
                            notes = s.optString("notes", "")
                        )
                    )
                    if (s.has("exercises")) {
                        val exercises = s.getJSONArray("exercises")
                        for (j in 0 until exercises.length()) {
                            val e = exercises.getJSONObject(j)
                            val exerciseId = sessionDao.insertCompletedExercise(
                                CompletedExercise(
                                    sessionId = sessionId,
                                    exerciseName = e.getString("exerciseName"),
                                    orderIndex = e.optInt("orderIndex", j)
                                )
                            )
                            if (e.has("sets")) {
                                val sets = e.getJSONArray("sets")
                                val setList = mutableListOf<CompletedSet>()
                                for (k in 0 until sets.length()) {
                                    val set = sets.getJSONObject(k)
                                    setList.add(
                                        CompletedSet(
                                            exerciseId = exerciseId,
                                            setNumber = set.getInt("setNumber"),
                                            reps = set.getInt("reps"),
                                            weight = set.getDouble("weight"),
                                            completed = set.optBoolean("completed", true)
                                        )
                                    )
                                }
                                sessionDao.insertCompletedSets(setList)
                            }
                        }
                    }
                    sessionsImported++
                }
            }

            ImportResult(
                true,
                "$templatesImported Vorlagen und $sessionsImported Trainings importiert"
            )
        } catch (e: Exception) {
            ImportResult(false, "Import fehlgeschlagen: ${e.message}")
        }
    }
}

data class ImportResult(val success: Boolean, val message: String)
