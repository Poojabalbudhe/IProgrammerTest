package com.iprogrammer.whetherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var CITY: String = "Pune"
    val API: String = "dd7eb0d6664ab49599be87da3ab2dd2c"
    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isNetworkAvailable()) {
            weatherInfoTask().execute()
        }
    }

    inner class weatherInfoTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE

        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

                val name = CITY
                val temperature = temp
                val datetime = updatedAtText
                val databaseHandler: DatabaseHandler = DatabaseHandler(context)
                if (name.trim() != "" && temperature.trim() != "" && datetime.trim() != "") {
                    val status = databaseHandler.addCities(Cities(name, temp, datetime))
                    if (status > -1) {
                        Toast.makeText(applicationContext, "Data saved", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "id or name or email cannot be blank",
                        Toast.LENGTH_LONG
                    ).show()
                }

                viewRecord()


            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }

    }


    fun withEditText(view: View) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        val inflater = layoutInflater
        builder.setTitle("Enter city")
        val dialogLayout = inflater.inflate(R.layout.search_alert_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK")
        { dialogInterface, i ->

            if (TextUtils.isEmpty(editText.text.toString())) {
                Toast.makeText(this, "Empty field not allowed!", Toast.LENGTH_SHORT).show()
            } else {
                CITY = editText.text.toString()

                if (isNetworkAvailable()) {
                    weatherInfoTask().execute()
                } else {
                    Toast.makeText(this, "Check internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.show()
    }

    fun viewRecord() {
        val databaseHandler: DatabaseHandler = DatabaseHandler(context)
        val user: List<Cities> = databaseHandler.viewCities()

        val userArrayName = Array<String>(user.size) { "null" }
        val userArrayTemp = Array<String>(user.size) { "null" }
        val userArrayDateTime = Array<String>(user.size) { "null" }
        var index = 0
        for (e in user) {
            userArrayName[index] = e.cityName
            userArrayTemp[index] = e.cityTemp
            userArrayDateTime[index] = e.cityDate
            index++
        }
        val myListAdapter = MyListAdapter(this, userArrayName, userArrayTemp, userArrayDateTime)
        listView.adapter = myListAdapter
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


}
