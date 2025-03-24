package com.example.apiproject_assignment_2.network

import com.example.apiproject_assignment_2.json.JSONHandler
import com.example.apiproject_assignment_2.models.APIRequest
import com.example.apiproject_assignment_2.models.EPICRequest
import com.google.gson.JsonParser
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class APICall {


     private fun connectToAPI(requestString: String): HttpsURLConnection {
        val url = URL(requestString)
        val connection = url.openConnection() as HttpsURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.requestMethod = "GET"
        connection.connect()
        return connection
    }

    fun fetchAPOD(
        requestString: String,
        onSingleRequest: (APIRequest) -> Unit,
        onMultipleRequest: (List<APIRequest>) -> Unit,
        onApiError: () -> Unit,
        onIOException: (IOException) -> Unit,
    ) {

        val thread = Thread{
            try{
                //todo:refactor (combine) the shared part of the methods
                val connection = connectToAPI(requestString)

                if(connection.responseCode == HttpsURLConnection.HTTP_OK){

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                    val jsonElement = JsonParser.parseReader(inputStreamReader)

                    val jsonHandler = JSONHandler()

                    if (jsonElement.isJsonObject){
                        val request = jsonHandler.processAPODSingle(jsonElement)
                        onSingleRequest(request)
                    }else if(jsonElement.isJsonArray){
                        val requestList = jsonHandler.processArray<APIRequest>(jsonElement)
                        onMultipleRequest(requestList)
                    }

                    inputStreamReader.close()
                    inputSystem.close()

                }else{
                    onApiError()
                }
            } catch (e: IOException){
                onIOException(e)
            }
        }
        thread.start()
    }

    fun fetchEPIC(requestString: String,
                  onSuccess: (List<EPICRequest>) -> Unit,
                  onApiError: () -> Unit,
                  onIOException: (IOException) -> Unit){
        val thread = Thread {
            try {
                val connection = connectToAPI(requestString)
                if (connection.responseCode == HttpsURLConnection.HTTP_OK) {

                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                    val jsonElement = JsonParser.parseReader(inputStreamReader)
                    val jsonHandler = JSONHandler()

                    val requestList = jsonHandler.processArray<EPICRequest>(jsonElement)
                    onSuccess(requestList)

                    inputStreamReader.close()
                    inputSystem.close()

                }else{
                    onApiError()
                }
            } catch (e: IOException){
                onIOException(e)
            }
        }
        thread.start()
    }


}