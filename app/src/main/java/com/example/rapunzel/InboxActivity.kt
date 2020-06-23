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
import kotlinx.android.synthetic.main.activity_inbox.*
import kotlinx.android.synthetic.main.inbox_row.view.*
import okhttp3.*
import java.io.IOException

class InboxActivity : AppCompatActivity() {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        runOnUiThread {
            fetchInbox()
        }
    }

    fun fetchInbox() {

        val url = "https://lan7xw8ug4.execute-api.eu-central-1.amazonaws.com/prod/myinbox?id="+(main.id-123).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback{

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu" , "url for inbox is: $url")
                Log.d("Rapu", body!!)

                val gson = GsonBuilder().create()

                val inbox = gson.fromJson(body, Inbox::class.java)

                var counter = inbox.date.size

                runOnUiThread( Runnable {
                    kotlin.run {
                        val adapter = GroupAdapter<ViewHolder>()

                        for(i in 0 until counter){
                            Log.d("Rapu" , "is is: ${inbox.date[i]}")
                            adapter.add(InboxItem( inbox.title[i] , inbox.body[i] , inbox.date[i] ) )
                        }

                        recyclerview_inbox.adapter = adapter
                    }
                })

                //Log.d("Rapu" , agenda.dates!!.size.toString()   )
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        })

    }
}

class Inbox( val title: List<String> , val body: List<String> , val date: List<String> ) {

}

class InboxItem(val title: String, val body: String, val date: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.inbox).into(viewHolder.itemView.imageView_inbox)

        viewHolder.itemView.textView_inbox1.text = title
        viewHolder.itemView.textView_inbox2.text = body
        viewHolder.itemView.textView_inbox3.text = date

    }

    override fun getLayout(): Int {
        return R.layout.inbox_row
    }

}
