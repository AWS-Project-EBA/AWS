package com.example.rapunzel

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.TextView.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(){

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_in_button.setOnClickListener {

            val email:String = username_login.text.toString()
            val intent = Intent(this, NavActivity::class.java)

            GlobalScope.launch(context = Dispatchers.IO) {
                println("launched coroutine 1")
                println(main.id.toString() + " " + main.email)
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
                    else{
                        showHide(wrongCred)
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

                if(login.message=="Internal server error")
                    main.id=-1
                else {
                    runOnUiThread(Runnable {
                        kotlin.run {
                            Log.d("Rapu", "is is: ${login.id}")
                            main.id = login.id
                            main.email = email
                            val temp=email.substringBefore(delimiter = '@')
                            main.name=temp
                        }
                    })
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )


    }
}

class Login(val message:String, val id: Int) {

}

fun showHide(view:TextView) {
    if (view.visibility == TextView.INVISIBLE)
        view.visibility = VISIBLE
    else{
        if(view.currentTextColor==Color.parseColor("#FFFFFF"))
            view.setTextColor(Color.parseColor("#00FF00"))
        else
            view.setTextColor(Color.parseColor("#FFFFFF"))
    }

}
