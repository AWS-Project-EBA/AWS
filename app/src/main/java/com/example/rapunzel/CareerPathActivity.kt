package com.example.rapunzel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_career_path.*
import okhttp3.*
import java.io.IOException

class CareerPathActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_career_path)

        supportActionBar?.title = "My Career Path"

        runOnUiThread {
            fetchCareer()
        }
    }
    fun fetchCareer(){

        val url = "https://fg0l1o6kx3.execute-api.eu-central-1.amazonaws.com/prod/careerpath?id="+(main.id-123).toString()

        println(main.id.toString()+" "+main.email)

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                val gson = GsonBuilder().create()

                val career =  gson.fromJson(body, Career::class.java)

                runOnUiThread( Runnable {
                    kotlin.run {
                        Log.d("Rapu" , "is is: ${career.count}")
                        job_done.text=career.count.toString()
                        currlev.text=career.level.toString()
                        nextlev.text=career.next_level.toString()
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )


    }
}
class Career( val count: Int , val level: Int , val next_level: Int ) {

}
