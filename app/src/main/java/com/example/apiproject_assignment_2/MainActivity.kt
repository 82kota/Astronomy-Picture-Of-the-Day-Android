package com.example.apiproject_assignment_2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.apiproject_assignment_2.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.Calendar
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apiKey = "DEMO_KEY"

        //API default request string
        var requestString = "https://api.nasa.gov/planetary/apod?api_key=$apiKey"

        val modes = resources.getStringArray(R.array.Modes)
        val spinner = binding.spinner
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, modes)
        spinner.adapter = adapter
        val datePicker:DatePicker = binding.datePicker

        //flag for multiple objects return - jump to recyclerview activity
        var changeActivityFlag = false

        datePicker.visibility = View.GONE
        binding.astronomyPicture.visibility = View.GONE

        val calendar = Calendar.getInstance()


        //set listener for the go button
        binding.goButton.setOnClickListener{
            //if change activity is true jumps to another activity
            getPicture(requestString, changeActivityFlag).start()
        }

        //mode select spinner logic
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                //logic on option select
                if(modes[position] == "Today\'s picture"){
                    datePicker.visibility = View.GONE
                    requestString = "https://api.nasa.gov/planetary/apod?api_key=$apiKey"
                }else if(modes[position] == "Select specific date"){
                    datePicker.visibility = View.VISIBLE

                    datePicker.init(
                       calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ){ view, year, month, day ->
                        //add padding to numbers
                        val formattedDay = day.toString().padStart(2, '0')
                        val formattedMonth = (month + 1).toString().padStart(2, '0')
                        val selectedDate = "$year-$formattedMonth-$formattedDay"

                        //add selected date to the api request
                        requestString = "https://api.nasa.gov/planetary/apod?date=$selectedDate&api_key=$apiKey"

                    }
                    datePicker.maxDate = calendar.timeInMillis
                    //select date logic
                }else if(modes[position] == "Random date"){
                    datePicker.visibility = View.GONE
                    requestString = "https://api.nasa.gov/planetary/apod?count=1&api_key=$apiKey"
                }else if(modes[position] == "Get a random batch"){
                    datePicker.visibility = View.GONE
                    changeActivityFlag = true
                    requestString = "https://api.nasa.gov/planetary/apod?count=6&api_key=$apiKey"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //logic for nothing selected
            }
            }
    }

    private fun getPicture(requestString:String, changeActivity:Boolean):Thread{
        binding.datePicker.visibility = View.GONE
        return Thread{
            try{
                val url = URL(requestString)
                val connection = url.openConnection() as HttpsURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if(responseCode == HttpsURLConnection.HTTP_OK){
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                    val jsonElement = JsonParser.parseReader(inputStreamReader)

                    if (jsonElement.isJsonObject) {
                        // API returns a single JSON Object
                        val request = Gson().fromJson(jsonElement, APIRequest::class.java)
                        updateUI(request)
                    } else if (jsonElement.isJsonArray) {


                        // API returns a JSON Array (returned when getting a random picture)
                        val listType = object : TypeToken<List<APIRequest>>() {}.type
                        val requestList: List<APIRequest> = Gson().fromJson(jsonElement, listType)

                        //if option is get batch - send the request list to the recycler view
                        if(changeActivity){
                            //switch activity and pass the requestList
                            val recyclerIntent = Intent(this, RecyclerViewActivity::class.java)
                            val bundle = Bundle()
                            val requestArrayList = ArrayList(requestList)
                            bundle.putParcelableArrayList("requestList", requestArrayList)
                            recyclerIntent.putExtras(bundle)

                            startActivity(recyclerIntent)
                        }

                        if (requestList.isNotEmpty()) {
                            updateUI(requestList.first()) // Handle first item
                        }
                    }

                    inputStreamReader.close()
                    inputSystem.close()
                }else{
                    runOnUiThread{
                        binding.explanationTextview.text =
                            getString(R.string.failed_to_connect_to_the_api)
                    }
                }
            } catch (e: IOException){
                runOnUiThread{
                    binding.explanationTextview.text = getString(R.string.error_retrieving_data)
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateUI(request: APIRequest){
        runOnUiThread{
            kotlin.run{
                binding.titleTextview.text = request.title
                binding.explanationTextview.text = request.explanation
                binding.dateView.text = request.date

                binding.astronomyPicture.visibility = View.VISIBLE
                Glide.with(this).load(request.url).into(binding.astronomyPicture)
            }
        }

    }



}