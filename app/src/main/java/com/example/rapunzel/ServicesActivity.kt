package com.example.rapunzel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_services.*
import kotlinx.android.synthetic.main.services_row.view.*
import okhttp3.*
import java.io.IOException

class ServicesActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        supportActionBar?.title = "My Services"

        runOnUiThread {
            fetchServices()
        }
    }
    fun fetchServices(){

        val url = "https://iu8v4efr3m.execute-api.eu-central-1.amazonaws.com/prod/myservices?id="+(main.id-123).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                val gson = GsonBuilder().create()

                val service =  gson.fromJson(body, Services::class.java)

                var counter = service.name.size

                Log.d("Rapu" , "size is: ${service.name.size}")

                runOnUiThread( Runnable {
                    kotlin.run {
                        val adapter = GroupAdapter<ViewHolder>()

                        for(i in 0 until counter){
                            Log.d("Rapu" , "is is: ${service.name[i]}")
                            adapter.add(ServiceItem( service.name[i] ) )
                        }

                        recyclerview_services.adapter = adapter
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )


    }
}

class Services( val name: List<List<String>>) {

}

class ServiceItem(val name: List<String>): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.services).into(viewHolder.itemView.imageView_service)

        val temp=name.toString()

        val temp2:String=temp.substringAfter(delimiter = '[')

        val modified:String=temp2.substringBeforeLast(delimiter = ']')

        viewHolder.itemView.textView_service1.text = modified

    }

    override fun getLayout(): Int {
        return R.layout.services_row
    }

}
