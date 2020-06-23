package com.example.rapunzel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_service_locations.*
import kotlinx.android.synthetic.main.servloc_row.view.*
import okhttp3.*
import java.io.IOException

class ServiceLocationsActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_locations)
        supportActionBar?.title = "My Service Locations"

        runOnUiThread {
            fetchServiceLocations()
        }
    }
    fun fetchServiceLocations(){

        val url = "https://hdq5zzm6ya.execute-api.eu-central-1.amazonaws.com/prod/myserviceslocations?id="+(main.id).toString()

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
                            adapter.add(ServiceLocItem( "" ) )

                            recyclerview_servicelocs.adapter = adapter
                        }
                    })
                }

                else{
                    val gson = GsonBuilder().create()

                    val serviceloc =  gson.fromJson(body, ServiceLoc::class.java)

                    var counter = serviceloc.location.size

                    Log.d("Rapu" , "size is: ${serviceloc.location.size}")

                    runOnUiThread( Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()

                            for(i in 0 until counter){
                                Log.d("Rapu" , "is is: ${serviceloc.location[i]}")
                                adapter.add(ServiceLocItem( serviceloc.location[i] ) )
                            }

                            recyclerview_servicelocs.adapter = adapter
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

class ServiceLoc( val location: List<String>) {

}

class ServiceLocItem(val location: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val temp:String=location

        val district=temp.substringBefore(delimiter = ' ')

        val temp2=temp.substringAfter(delimiter = ' ')

        val city=temp2.substringBefore(delimiter = ' ')

        val country=temp2.substringAfter(delimiter = ' ')

        Picasso.get().load(R.drawable.sloc).into(viewHolder.itemView.imageView_serviceloc)

        viewHolder.itemView.textView_serviceloc1.text = district

        Picasso.get().load(R.drawable.sloc).into(viewHolder.itemView.imageView_serviceloc2)

        viewHolder.itemView.textView_serviceloc2.text = city

        Picasso.get().load(R.drawable.sloc).into(viewHolder.itemView.imageView_serviceloc3)

        viewHolder.itemView.textView_serviceloc3.text = country

    }

    override fun getLayout(): Int {
        return R.layout.servloc_row
    }

}
