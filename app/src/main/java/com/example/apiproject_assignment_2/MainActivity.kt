package com.example.apiproject_assignment_2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
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
import com.example.apiproject_assignment_2.models.APIRequest
import com.example.apiproject_assignment_2.models.EPICRequest
import com.example.apiproject_assignment_2.network.APICall
import java.util.Calendar

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

        //demo key only allows 50 daily and 30 hourly requests
        val apiKey = "DEMO_KEY"

        //APOD API default request string
        var requestString = "https://api.nasa.gov/planetary/apod?api_key=$apiKey"

        val modes = resources.getStringArray(R.array.Modes)
        val spinner = binding.spinner
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, modes)
        spinner.adapter = adapter
        val datePicker:DatePicker = binding.datePicker

        //flag for multiple objects return - jump to recyclerview activity
        var isMultipleRequest = false

        datePicker.visibility = View.GONE
        resetAdditionalUI()

        val calendar = Calendar.getInstance()

        //set listener and logic for the go button
        binding.goButton.setOnClickListener{

            runOnUiThread {
                resetAdditionalUI()
                binding.goButton.isEnabled = false
            }

            val apiCall = APICall()
            apiCall.fetchAPOD(
                requestString,

                //logic changes based on selected mode
                onSingleRequest = {
                    request ->
                    displayAPOD(request)
                    runOnUiThread {
                        binding.goButton.isEnabled = true
                    }
                },
                onMultipleRequest = {
                    //when one or multiple random images are requested, a JSON array is returned

                    requestList ->
                    //multiple random images
                    if(isMultipleRequest){
                        switchToRecyclerActivity(requestList)
                    }
                    //one random image
                    else if(requestList.isNotEmpty()){
                        displayAPOD(requestList.first())
                    }
                    runOnUiThread {
                        binding.goButton.isEnabled = true
                    }
                },

                onApiError = {
                    displayAPIConnectionError()
                    runOnUiThread {
                        binding.goButton.isEnabled = true
                    }
                },
                onIOException = {
                    displayIOError()
                    runOnUiThread {
                        binding.goButton.isEnabled = true
                    }
                }
            )

        }

        binding.earthButton.setOnClickListener {
            resetAdditionalUI()

            val epicDate = binding.dateView.text


            //EPIC API default request string
            var earthPicRequestString = "https://api.nasa.gov/EPIC/api/natural/date/$epicDate?api_key=$apiKey"
            val todayDate = getFormattedCurrentDate(calendar, '-')

            //if the APOD's date is today, change the EPIC request string (EPIC API requirement)
            if(epicDate == todayDate){
                earthPicRequestString = "https://api.nasa.gov/EPIC/api/natural/images?api_key=$apiKey"
            }


            val apiCall = APICall()
            apiCall.fetchEPIC(

                earthPicRequestString,
                onSuccess = {
                    requestList ->
                    if(requestList.isNotEmpty()) {
                        displayEPIC(requestList.first(), apiKey, calendar)
                    }
                    else(displayEpicError())
                },
                onApiError = {
                    displayAPIConnectionError()
                },
                onIOException = {
                    displayIOError()
                }
                )
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

                isMultipleRequest = false
                binding.datePicker.visibility = View.GONE
                //logic on option select
                when(modes[position]) {
                    "Today\'s picture" ->
                    requestString = "https://api.nasa.gov/planetary/apod?api_key=$apiKey"
                    "Select specific date" -> {
                        datePicker.visibility = View.VISIBLE

                        datePicker.init(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ) { _, year, month, day ->
                            //add padding to numbers
                            val formattedDay = day.toString().padStart(2, '0')
                            val formattedMonth = (month + 1).toString().padStart(2, '0')
                            val selectedDate = "$year-$formattedMonth-$formattedDay"

                            //add selected date to the api request
                            requestString =
                                "https://api.nasa.gov/planetary/apod?date=$selectedDate&api_key=$apiKey"

                        }
                        datePicker.maxDate = calendar.timeInMillis
                    }

                    "Random date" ->
                        requestString = "https://api.nasa.gov/planetary/apod?count=1&api_key=$apiKey"
                    "Get a random batch" -> {
                        isMultipleRequest = true
                        requestString = "https://api.nasa.gov/planetary/apod?count=6&api_key=$apiKey"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            }

        //get intent
        val apodFromRecyclerView = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("requestItem", APIRequest::class.java)
        } else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("requestItem")
        }
        if(apodFromRecyclerView != null){
            displayAPOD(apodFromRecyclerView)
        }
    }

    private fun displayIOError() {
        runOnUiThread {
            binding.explanationTextview.text = getString(R.string.error_retrieving_data)
        }
    }

    private fun switchToRecyclerActivity(requestList: List<APIRequest>) {
        //switch activity and pass the requestList
        val recyclerIntent = Intent(this, RecyclerViewActivity::class.java)
        val bundle = Bundle()
        val requestArrayList = ArrayList(requestList)
        bundle.putParcelableArrayList("requestList", requestArrayList)
        recyclerIntent.putExtras(bundle)

        startActivity(recyclerIntent)
    }

    @SuppressLint("DefaultLocale")
    private fun displayAPOD(request: APIRequest){

        val date = request.date
        val year = date.substring(0, 4)
        val month = date.substring(5, 7)
        val day = date.substring(8, 10)

        runOnUiThread{
            binding.apodTextview.text = getString(R.string.nasa_s_astronomy_picture_of_the_day_apod)
            binding.titleTextview.text = request.title
            binding.explanationTextview.text = request.explanation
            binding.dateView.text = date

            binding.astronomyPicture.visibility = View.VISIBLE

            //check if the date is after the first EPIC image was taken
            if(year.toInt() > 2015 ||
                (year.toInt() == 2015 && month.toInt() > 7) ||
                (year.toInt() == 2015 && month.toInt() == 7 && day.toInt() >= 6)){
                binding.earthTextView.visibility = View.VISIBLE
                binding.earthButton.visibility = View.VISIBLE
            }else {
                //if not, remove the button
                resetAdditionalUI()
            }

            Glide.with(this).load(request.url).into(binding.astronomyPicture)

        }
    }

    private fun getFormattedCurrentDate(calendar : Calendar, delimiter: Char) : String{
        //format today's date as required by the api call
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //add padding to numbers
        val formattedDay = day.toString().padStart(2, '0')
        val formattedMonth = (month + 1).toString().padStart(2, '0')

        val selectedDate = "$year$delimiter$formattedMonth$delimiter$formattedDay"
        return selectedDate
    }

    private fun displayAPIConnectionError(){
        runOnUiThread{
            binding.explanationTextview.text =
                getString(R.string.failed_to_connect_to_the_api)
        }
    }

    private fun displayEPIC(request: EPICRequest, apiKey: String, calendar: Calendar){
        val date = request.date
        val year = date.substring(0, 4)
        val month = date.substring(5, 7)
        val day = date.substring(8, 10)

        var archiveDate = "$year/$month/$day"

        val imageName = request.image

        //check if the date is before first EPIC image was taken
        if(year.toInt() < 2015 ||
            (year.toInt() == 2015 && month.toInt() < 7) ||
            (year.toInt() == 2015 && month.toInt() == 7 && day.toInt() <= 6)){

            //set date to today
            archiveDate = getFormattedCurrentDate(calendar, '/')
        }
        val imageString = "https://api.nasa.gov/EPIC/archive/natural/$archiveDate/png/$imageName.png?api_key=$apiKey"

        runOnUiThread {

            Glide.with(this).load(imageString).into(binding.astronomyPicture)
            binding.apodTextview.text =
                getString(R.string.dscovr_s_earth_polychromatic_imaging_camera_epic)
            binding.titleTextview.text = getString(R.string.planet_earth)
            binding.explanationTextview.text =
                getString(R.string.here_s_what_earth_looked_like_on, date)

        }

    }

    private fun displayEpicError(){
        runOnUiThread {
            kotlin.run {
                binding.apodTextview.text =
                    getString(R.string.dscovr_s_earth_polychromatic_imaging_camera_epic)
                binding.explanationTextview.text =
                    getString(R.string.no_picture)
            }
        }
    }

    private fun resetAdditionalUI(){
        runOnUiThread{
            binding.datePicker.visibility = View.GONE
            binding.earthButton.visibility = View.GONE
            binding.earthTextView.visibility = View.GONE
        }
    }



}