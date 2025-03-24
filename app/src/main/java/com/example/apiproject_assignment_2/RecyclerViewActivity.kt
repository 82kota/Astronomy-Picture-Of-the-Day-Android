package com.example.apiproject_assignment_2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apiproject_assignment_2.adapters.RecyclerAdapter
import com.example.apiproject_assignment_2.databinding.ActivityRecyclerViewBinding
import com.example.apiproject_assignment_2.models.APIRequest

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
        val arrayRequestList : ArrayList<APIRequest>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelableArrayList("requestList", APIRequest::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle?.getParcelableArrayList("requestList")
        }

        val apodList: MutableList<APIRequest> = arrayRequestList?.toMutableList() ?: mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)

        //set adapter
        val adapter = RecyclerAdapter(apodList) {
            //onClick behaviour
            apodItem ->
            switchToMainActivity(apodItem)
        }
        recyclerView.adapter = adapter

    }

    private fun switchToMainActivity(apodItem : APIRequest){
        //switch activity and pass the APIRequest item
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("requestItem", apodItem)

        startActivity(intent)
    }

}