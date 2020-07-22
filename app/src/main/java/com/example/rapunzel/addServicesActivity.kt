package com.example.rapunzel

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_add_services.*
import kotlinx.android.synthetic.main.activity_services.*
import kotlinx.android.synthetic.main.addserv_row.view.*
import kotlinx.android.synthetic.main.services_row.view.*
import okhttp3.*
import java.io.IOException
import java.util.*

class addServicesActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_services)

        runOnUiThread {
            addServices()

        }

    }

    fun addServices(){

        val url = "https://l1cbl34bkj.execute-api.eu-central-1.amazonaws.com/prod/potentialservices?id="+(main.id).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                if (body == "{}") {
                    runOnUiThread(Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()
                            val name:List<String>
                            name= Arrays.asList("")
                            adapter.add(addServiceItem(name[0]))

                            recyclerview_addServices.adapter = adapter
                        }
                    })
                } else {
                    val gson = GsonBuilder().create()

                    val addservice = gson.fromJson(body, addServices::class.java)

                    var counter = addservice.name.size

                    Log.d("Rapu", "size is: ${addservice.name.size}")

                    runOnUiThread(Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()

                            for (i in 0 until counter) {
                                Log.d("Rapu", "is is: ${addservice.name[i]}")
                                adapter.add(addServiceItem(addservice.name[i]))
                            }

                            recyclerview_addServices.adapter = adapter

                            adapter.setOnItemClickListener { item, view ->

                                val addItem = item as addServiceItem
                                Log.d("Rapu" , addItem.name)

                                val serviceName = addItem.name.toString()
                                var opHold = ""
                                var idHold = ""
                                var serviceHold = ""

                                //val url = "https://jzj4to3daj.execute-api.eu-central-1.amazonaws.com/prod/changemyservices?services=" + serviceName +"&id=" + (main.id).toString() + "&operation=1"
                                val url = "https://wuwdhnabvi.execute-api.eu-central-1.amazonaws.com/prod/changemyservices?id=" + (main.id).toString() + "&operation=1" + "&services=" + serviceName
                                val formBody: RequestBody = FormBody.Builder()
                                    .build()

                                val request: Request = Request.Builder()
                                    .url(url)
                                    .post(formBody)
                                    .build()

                                val call = client.newCall(request)
                                call.enqueue(object : Callback {

                                    override fun onResponse(call: Call, response: Response) {
                                        Log.d("Rapu" , "dene " + response.message.toString() + "is")


                                        val intent = Intent(this@addServicesActivity, ServicesActivity::class.java)
                                        startActivity(intent)
                                    }

                                    override fun onFailure(call: Call, e: IOException) {
                                        Log.d("Rapu" , e.message)
                                    }

                                })

                            }

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


class addServices( val name: List<String>) {

}

class addServiceItem(val name: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.services).into(viewHolder.itemView.imageView_addservice)

        val temp=name.toString()

        viewHolder.itemView.textView_addservice1.text = temp

    }

    override fun getLayout(): Int {
        return R.layout.addserv_row
    }

}