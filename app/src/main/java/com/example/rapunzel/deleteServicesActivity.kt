package com.example.rapunzel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_delete_services.*
import kotlinx.android.synthetic.main.activity_services.*
import kotlinx.android.synthetic.main.addserv_row.view.*
import kotlinx.android.synthetic.main.deleteserv_row.view.*
import kotlinx.android.synthetic.main.services_row.view.*
import okhttp3.*
import java.io.IOException
import java.util.*

class deleteServicesActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_services)

        runOnUiThread {
            deleteServices()

        }

    }

    fun deleteServices(){

        val url = "https://iu8v4efr3m.execute-api.eu-central-1.amazonaws.com/prod/myservices?id="+(main.id).toString()

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
                            adapter.add(deleteServiceItem(name))

                            recyclerview_deleteServices.adapter = adapter
                        }
                    })
                } else {
                    val gson = GsonBuilder().create()

                    val deleteservice = gson.fromJson(body, deleteServices::class.java)

                    var counter = deleteservice.name.size

                    Log.d("Rapu", "size is: ${deleteservice.name.size}")

                    runOnUiThread(Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()

                            for (i in 0 until counter) {
                                Log.d("Rapu", "is is: ${deleteservice.name[i]}")
                                adapter.add(deleteServiceItem(deleteservice.name[i]))
                            }

                            recyclerview_deleteServices.adapter = adapter

                            adapter.setOnItemClickListener { item, view ->

                                val deleteItem = item as deleteServiceItem
                                Log.d("Rapu" , deleteItem.name[0])

                                val deleteserviceName = deleteItem.name[0].toString()
                                //var opHold = ""
                                //var idHold = ""
                                //var serviceHold = ""

                                //val url = "https://jzj4to3daj.execute-api.eu-central-1.amazonaws.com/prod/changemyservices?services=" + deleteserviceName +"&id=" + (main.id).toString() + "&operation=1"
                                val url = "https://wuwdhnabvi.execute-api.eu-central-1.amazonaws.com/prod/changemyservices?id=" + (main.id).toString() + "&operation=-1" + "&services=" + deleteserviceName
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


                                        val intent = Intent(this@deleteServicesActivity, ServicesActivity::class.java)
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

class deleteServices( val name: List<List<String>>) {

}

class deleteServiceItem(val name: List<String>): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.services).into(viewHolder.itemView.imageView_deleteservice)

        val temp=name.toString()

        val temp2:String=temp.substringAfter(delimiter = '[')

        val modified:String=temp2.substringBeforeLast(delimiter = ']')

        viewHolder.itemView.textView_deleteservice1.text = modified

    }

    override fun getLayout(): Int {
        return R.layout.deleteserv_row
    }

}

