package utilities

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit
import data.cargame.Record


class HighScoreManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("HighScores", Context.MODE_PRIVATE)


    fun getScores(): ArrayList<Record> {
        val json = sharedPreferences.getString("scores_list", null) ?: return ArrayList()

        val type = object : TypeToken<ArrayList<Record>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun addScore(newRecord: Record) {
        val scores = getScores()
        scores.add(newRecord)

        scores.sortByDescending { it.score }

        val topten = if (scores.size > 10) ArrayList(scores.take(10)) else scores


        val json = Gson().toJson(scores)
        sharedPreferences.edit { putString("scores_list", json) }

    }

    fun clearAllScores() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
