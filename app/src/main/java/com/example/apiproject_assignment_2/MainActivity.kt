package com.example.apiproject_assignment_2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apiproject_assignment_2.databinding.ActivityMainBinding

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

        binding.goButton.setOnClickListener{
            getPicture()
        }
    }

    private fun getPicture(){
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY"
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String>{
            response ->
            // Display the first 500 characters of the response string.
            binding.explanationTextview.text = "Response is: ${response}"
        },
        Response.ErrorListener {binding.explanationTextview.text = "That didn't work"})


        queue.add(stringRequest)
    }

}