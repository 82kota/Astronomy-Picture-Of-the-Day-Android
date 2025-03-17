package com.example.apiproject_assignment_2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apiproject_assignment_2.databinding.ActivityRecyclerViewBinding

class RecyclerViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = binding.recyclerView


        val bundle = intent.extras

        //get data from the api request parcel and add it to the list
        val arrayRequestList : ArrayList<APIRequest>? =
            bundle?.getParcelableArrayList("requestList")//non-deprecated function is only android 33 and above

        val apodList: MutableList<APIRequest> = arrayRequestList?.toMutableList() ?: mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)

        //set adapter
        val adapter = RecyclerAdapter(apodList)
        recyclerView.adapter = adapter

    }


}