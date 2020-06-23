package com.example.rapunzel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_hrmanager.*
import kotlinx.android.synthetic.main.hr_row.view.*
import okhttp3.*
import java.io.IOException

class HRManagerActivity : AppCompatActivity() {

    val main=Singleton.instance

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hrmanager)

        supportActionBar?.title = "My HR Manager"


        adapter.add(HRItem())
        adapter.add(HRItem())
        adapter.add(HRItem())
        adapter.add(HRItem())

        recyclerview_nav.adapter = adapter

        fetchJson()

    }

    fun fetchJson(){
        println("Attempting to fetch")

        val url = "https://fg0l1o6kx3.execute-api.eu-central-1.amazonaws.com/prod/careerpath?id="+(main.id).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        })

    }
}

class HRItem: Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.hr).into(viewHolder.itemView.imageView)

    }

    override fun getLayout(): Int {
        return R.layout.hr_row
    }

}


