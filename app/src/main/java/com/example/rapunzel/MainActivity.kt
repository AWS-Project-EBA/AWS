package com.example.rapunzel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_in_button.setOnClickListener {

            val email:String = username_login.text.toString()
            val intent = Intent(this, NavActivity::class.java)

            GlobalScope.launch(context = Dispatchers.IO) {
                println("launched coroutine 1")
                fetchLogin(email)
                delay(5000)
                println("Here after a delay of 5 seconds")

                launch(context=Dispatchers.Main){
                    println("launched coroutine 2")

                    println(main.id.toString() + " " + main.email)
                    if(main.id!=-1 && (main.email.equals(email))) {
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }


        }
    }
    fun fetchLogin(email:String){

        val url = "https://hyoybru9mb.execute-api.eu-central-1.amazonaws.com/prod/login?email="+email

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                val gson = GsonBuilder().create()

                val login =  gson.fromJson(body, Login::class.java)

                runOnUiThread( Runnable {
                    kotlin.run {
                        Log.d("Rapu" , "is is: ${login.id}")
                        main.id=login.id
                        main.email=email
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )


    }
}
class Login( val id: Int) {

}
