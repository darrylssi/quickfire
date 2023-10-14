package nz.ac.uclive.ajs418.quickfire.database

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.entity.Media
import org.json.JSONArray
import org.json.JSONObject


internal class PrepopulateRoomCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            prePopulateMedias(context)
        }
    }

    private suspend fun prePopulateMedias(context: Context) {
        try {
            val mediaDao = QuickfireDatabase.getDatabase(context).mediaDao()

            val moviesJsonArray = context.resources.openRawResource(R.raw.movies).bufferedReader().use {
                JSONArray(it.readText())
            }
            val tvShowsJsonArray = context.resources.openRawResource(R.raw.tv_shows).bufferedReader().use {
                JSONArray(it.readText())
            }

            // Convert JSON arrays to lists
            val moviesList = jsonArrayToList(moviesJsonArray)
            val tvShowsList = jsonArrayToList(tvShowsJsonArray)

            // Concatenate the two lists
            val mediaList = moviesList + tvShowsList

            // Convert the concatenated list back to a JSON array
            val concatenatedJsonArray = listToJSONArray(mediaList)

            concatenatedJsonArray.takeIf { it.length() > 0 }?.let { list ->
                for (index in 0 until list.length()) {
                    val mediaObj = list.getJSONObject(index)
                    mediaDao.insert(
                        Media(
                            mediaObj.getString("title"),
                            mediaObj.getString("year").toLong(),
                            mediaObj.getString("type"),
                            mediaObj.optDouble("rating", 0.0).toString().toFloat(),
                            mediaObj.getString("synopsis"),
                            mediaObj.optString("img", null),
                        )
                    )
                }
                Log.e("Prepopulate Media Data", "successfully pre-populated medias into database")
            }
        } catch (exception: Exception) {
            Log.e(
                "Prepopulate Media Data",
                exception.localizedMessage ?: "failed to pre-populate medias into database"
            )
        }
    }

    private fun jsonArrayToList(jsonArray: JSONArray): List<JSONObject> {
        val list = mutableListOf<JSONObject>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getJSONObject(i))
        }
        return list
    }

    private fun listToJSONArray(list: List<JSONObject>): JSONArray {
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray
    }
}
