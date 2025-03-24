package com.example.apiproject_assignment_2.json

import com.example.apiproject_assignment_2.models.APIRequest
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken


class JSONHandler {


    fun processAPODSingle(jsonElement: JsonElement) : APIRequest{
            // API returns a single JSON Object
            val request = Gson().fromJson(jsonElement, APIRequest::class.java)
            return request
    }


     inline fun <reified T> processArray(jsonElement: JsonElement): List<T> {
        val listType = object : TypeToken<List<T>>() {}.type
        return Gson().fromJson(jsonElement, listType)
    }
}