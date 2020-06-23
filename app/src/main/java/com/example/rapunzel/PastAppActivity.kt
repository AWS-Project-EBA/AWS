package com.example.rapunzel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_agenda.*
import kotlinx.android.synthetic.main.activity_past_app.*
import kotlinx.android.synthetic.main.agenda_row.view.*
import kotlinx.android.synthetic.main.pastapp_row.view.*
import okhttp3.*
import java.io.IOException

class PastAppActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_app)

        //https://zvgmt952a0.execute-api.eu-central-1.amazonaws.com/prod/mypastappointments?id=135

        runOnUiThread {
            fetchPastApp()
        }


    }

    fun fetchPastApp(){

        val url = "https://zvgmt952a0.execute-api.eu-central-1.amazonaws.com/prod/mypastappointments?id="+(main.id-123).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                val gson = GsonBuilder().create()

                val pastapp =  gson.fromJson(body, PastApp::class.java)

                var counter = pastapp.date.size

                Log.d("Rapu" , "size is: ${pastapp.date.size}")

                runOnUiThread( Runnable {
                    kotlin.run {
                        val adapter = GroupAdapter<ViewHolder>()

                        for(i in 0 until counter){
                            Log.d("Rapu" , "is is: ${pastapp.date[i]}")
                            adapter.add(PastAppItem( pastapp.customer[i] , pastapp.date[i] , pastapp.hour[i] , pastapp.address[i] ) )
                        }

                        recyclerview_pastapp.adapter = adapter
                    }
                })



                //Log.d("Rapu" , agenda.dates!!.size.toString()   )
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )


    }

}

class PastApp( val date: List<String> , val hour: List<String> , val customer: List<String> , val address: List<String> ) {

}

class PastAppItem(val customer: String, val date: String, val hour: String , val address: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.pastap).into(viewHolder.itemView.imageView_pastapp)

        viewHolder.itemView.textView_pastapp1.text = customer
        viewHolder.itemView.textView_pastapp2.text = date
        viewHolder.itemView.textView_pastapp3.text = hour
        viewHolder.itemView.textView_pastapp4.text = address

    }

    override fun getLayout(): Int {
        return R.layout.pastapp_row
    }

}
