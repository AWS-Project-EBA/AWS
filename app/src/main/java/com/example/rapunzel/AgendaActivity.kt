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
import kotlinx.android.synthetic.main.agenda_row.view.*
import okhttp3.*
import java.io.IOException

class AgendaActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        Log.d("Agenda" , "Agenda")

        supportActionBar?.title = "My Agenda"

        runOnUiThread {
            fetchAgenda()
        }

        //fetchAgenda()

    }

    fun fetchAgenda(){

        val url = "https://l5lo98pz4j.execute-api.eu-central-1.amazonaws.com/prod/agenda?id="+(main.id-123).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback{

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                val gson = GsonBuilder().create()

                val agenda =  gson.fromJson(body, Agenda::class.java)

                var counter = agenda.date.size

                Log.d("Rapu" , "size is: ${agenda.date.size}")

                runOnUiThread( Runnable {
                    kotlin.run {
                        val adapter = GroupAdapter<ViewHolder>()

                        for(i in 0 until counter){
                            Log.d("Rapu" , "is is: ${agenda.date[i]}")
                            adapter.add(AgendaItem( agenda.customer[i] , agenda.date[i] , agenda.hour[i] ) )
                        }

                        recyclerview_agenda.adapter = adapter
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

class Agenda( val date: List<String> , val hour: List<String> , val customer: List<String> ) {

}

class AgendaItem(val customer: String, val date: String, val hour: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.hr).into(viewHolder.itemView.imageView_agenda)

        viewHolder.itemView.textView_agenda1.text = customer
        viewHolder.itemView.textView_agenda2.text = date
        viewHolder.itemView.textView_agenda3.text = hour

    }

    override fun getLayout(): Int {
        return R.layout.agenda_row
    }

}
