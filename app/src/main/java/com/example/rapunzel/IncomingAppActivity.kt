package com.example.rapunzel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_incoming_app.*
import kotlinx.android.synthetic.main.incapp_row.view.*
import okhttp3.*
import java.io.IOException

class IncomingAppActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_app)
        supportActionBar?.title = "My Incoming Appointments"

        runOnUiThread {
            fetchIncApp()
        }

    }

    fun fetchIncApp(){

        val url = "https://usek0py9b6.execute-api.eu-central-1.amazonaws.com/prod/myincomingappointments?id="+(main.id).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                if(body=="{}")
                {
                    runOnUiThread( Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()
                            adapter.add(IncAppItem( "","","","" ) )

                            recyclerview_incapp.adapter = adapter
                        }
                    })
                }
                else {
                    val gson = GsonBuilder().create()

                    val incapp = gson.fromJson(body, IncApp::class.java)

                    var counter = incapp.date.size

                    Log.d("Rapu", "size is: ${incapp.date.size}")

                    runOnUiThread(Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()

                            for (i in 0 until counter) {
                                Log.d("Rapu", "is is: ${incapp.date[i]}")
                                adapter.add(
                                    IncAppItem(
                                        incapp.customer[i],
                                        incapp.date[i],
                                        incapp.hour[i],
                                        incapp.address[i]
                                    )
                                )
                            }
                            recyclerview_incapp.adapter = adapter
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

class IncApp( val date: List<String> , val hour: List<String> , val customer: List<String> , val address: List<String> ) {

}

class IncAppItem(val customer: String, val date: String, val hour: String , val address: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.inap).into(viewHolder.itemView.imageView_incapp)

        viewHolder.itemView.textView_incapp1.text = customer
        viewHolder.itemView.textView_incapp2.text = date
        viewHolder.itemView.textView_incapp3.text = hour
        viewHolder.itemView.textView_incapp4.text = address

    }

    override fun getLayout(): Int {
        return R.layout.incapp_row
    }

}
