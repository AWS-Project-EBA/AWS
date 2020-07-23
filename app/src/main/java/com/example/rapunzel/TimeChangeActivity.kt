package com.example.rapunzel

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_time_select.*
import kotlinx.android.synthetic.main.time_rows.view.*
import okhttp3.*
import okhttp3.FormBody
import java.io.IOException


class TimeChangeActivity:AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_select)
        supportActionBar?.title = "My Weekly Working Hours"


        var starthourlist = ArrayList<String>()
        var startminlist = ArrayList<String>()
        var endhourlist = ArrayList<String>()
        var endminlist = ArrayList<String>()

        runOnUiThread {
            fetchTimes(starthourlist,startminlist,endhourlist,endminlist)
        }
    }
    fun fetchTimes(starthourlist:ArrayList<String>, startminlist: ArrayList<String>, endhourlist: ArrayList<String>, endminlist: ArrayList<String>){

        val url = "https://yqjk77y5p8.execute-api.eu-central-1.amazonaws.com/prod/mycalendar?id="+(main.id).toString()

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
                            adapter.add(TimesItem( "", "","" ) )

                            recyclerview_times.adapter = adapter
                        }
                    })
                } else {
                    val gson = GsonBuilder().create()

                    val times = gson.fromJson(body, Times::class.java)

                    var counter = times.day.size

                    Log.d("Rapu", "size is: ${times.day.size}")

                    runOnUiThread(Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()

                            var day_list = ArrayList<String>()

                            var startlist = ArrayList<String>()

                            var endlist = ArrayList<String>()

                            var spinnerArray = ArrayList<String>()

                            for(i in 0 until counter){
                                Log.d("Rapu" , "is is: ${times.day[i]}")
                                var endTime:String
                                endTime=times.end_time[i]
                                if(times.end_time[i].endsWith('\n'))
                                    endTime=times.end_time[i].substringBefore('\n')
                                adapter.add(TimesItem( times.day[i] , times.start_time[i], endTime) )
                                spinnerArray.add(times.day[i])
                                day_list.add(times.day[i])
                                startlist.add(times.start_time[i])
                                endlist.add(endTime)
                                var starter:String
                                starter=times.start_time[i]
                                var start_hour:String=starter.substringBefore('.')
                                var start_min:String=starter.substringAfter('.')
                                if(start_hour.length==1){
                                    start_hour="0"+start_hour
                                }
                                starthourlist.add(start_hour)
                                startminlist.add(start_min)

                                var ender:String
                                ender=endTime
                                var end_hour:String=ender.substringBefore('.')
                                var end_min:String=ender.substringAfter('.')
                                if(end_hour.length==1){
                                    end_hour="0"+end_hour
                                }
                                endhourlist.add(end_hour)
                                endminlist.add(end_min)
                            }

                            var selectedday:String = ""
                            var addselectedday:String = "Pazartesi"
                            var pos:Int=0

                            recyclerview_times.adapter = adapter

                            val aa = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, spinnerArray)

                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner.adapter = aa

                            spinner.onItemSelectedListener = object : OnItemSelectedListener {
                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                    val day = parentView!!.getItemAtPosition(position).toString()
                                    selectedday=day
                                    pos=position
                                    val staho: EditText = findViewById(R.id.startinghour)
                                    val stamin: EditText = findViewById(R.id.startingmin)
                                    val endho: EditText = findViewById(R.id.endinghour)
                                    val endminute: EditText = findViewById(R.id.endingmin)
                                    staho.setText(starthourlist[position])
                                    stamin.setText(startminlist[position])
                                    endho.setText(endhourlist[position])
                                    endminute.setText(endminlist[position])
                                    Toast.makeText(applicationContext, "Start : " + starthourlist[position]+ " "+ startminlist[position]
                                        +" "+endhourlist[position]+" " + endminlist[position], Toast.LENGTH_SHORT).show()
                                }

                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                    // your code here
                                }
                            }
                            spinneradd.onItemSelectedListener = object : OnItemSelectedListener {
                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                    val day = parentView!!.getItemAtPosition(position).toString()
                                    addselectedday=day
                                }

                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                    // your code here
                                }
                            }
                            val buttonclick:Button=findViewById(R.id.timebutton)
                            val buttonclick2:Button=findViewById(R.id.timebutton2)
                            val buttonclick3:Button=findViewById(R.id.timebutton3)

                            buttonclick.setOnClickListener {
                                Toast.makeText(this@TimeChangeActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                                val staho: EditText = findViewById(R.id.startinghour)
                                val stamin: EditText = findViewById(R.id.startingmin)
                                val endho: EditText = findViewById(R.id.endinghour)
                                val endminute: EditText = findViewById(R.id.endingmin)
                                val sh:Int=staho.text.toString().toInt()
                                val eh:Int=endho.text.toString().toInt()
                                val sm:Int=stamin.text.toString().toInt()
                                val em:Int=endminute.text.toString().toInt()
                                val start_time:String = staho.text.toString() + "." + stamin.text.toString()
                                val end_time:String = endho.text.toString() + "." + endminute.text.toString()
                                val day = selectedday
                                var start_time_del:String=startlist[pos]
                                var end_time_del:String=endlist[pos]
                                var count:Int=0
                                var shlist = ArrayList<Int>()
                                var ehlist = ArrayList<Int>()
                                var smlist = ArrayList<Int>()
                                var emlist = ArrayList<Int>()
                                var b:Boolean=false
                                for(i in 0 until counter) {
                                    if(day_list[i].equals(day)&&!(startlist[i].equals(start_time_del)&&endlist[i].equals(end_time_del))){
                                        count+=1
                                        shlist.add(startlist[i].substringBefore('.').toInt())
                                        ehlist.add(endlist[i].substringBefore('.').toInt())
                                        smlist.add(startlist[i].substringAfter('.').toInt())
                                        emlist.add(endlist[i].substringAfter('.').toInt())
                                    }
                                }
                                for(i in 0 until count) {
                                    if((eh>=ehlist[i]&&(sh<ehlist[i]||(sh==ehlist[i]&&sm<emlist[i])))||(eh<=ehlist[i]&&(eh>shlist[i]||(eh==shlist[i]&&em>smlist[i])))){
                                        b=true
                                    }
                                }
                                if((sh>eh)||((sh==eh)&&(sm>=em))||(b)){
                                    Toast.makeText(this@TimeChangeActivity, "The time is not valid. Please enter a valid time interval", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    day_list.removeAt(pos)
                                    startlist.removeAt(pos)
                                    endlist.removeAt(pos)
                                    val delurl = "https://nsiqczxoba.execute-api.eu-central-1.amazonaws.com/prod/changemycalendar?id="+(main.id).toString()+"&start_time="+start_time_del+"&end_time="+end_time_del+"&day="+day+"&operation=-1"

                                    val formBody: RequestBody = FormBody.Builder()
                                        .add("day", day_list.toString())
                                        .add("start_time", startlist.toString())
                                        .add("end_time", endlist.toString())
                                        .build()
                                    val request: Request = Request.Builder()
                                        .url(delurl)
                                        .post(formBody)
                                        .build()
                                    val call = client.newCall(request)
                                    call.enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            println(response)
                                        }
                                    })
                                    day_list.add(pos,day)
                                    startlist.add(pos,start_time)
                                    endlist.add(pos,end_time)
                                    val url = "https://nsiqczxoba.execute-api.eu-central-1.amazonaws.com/prod/changemycalendar?id="+(main.id).toString()+"&start_time="+start_time+"&end_time="+end_time+"&day="+day+"&operation=1"
                                    val formBody_up: RequestBody = FormBody.Builder()
                                        .add("day", day_list.toString())
                                        .add("start_time", startlist.toString())
                                        .add("end_time", endlist.toString())
                                        .build()
                                    val request_up: Request = Request.Builder()
                                        .url(url)
                                        .post(formBody_up)
                                        .build()
                                    val call_up = client.newCall(request_up)
                                    call_up.enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            println(response)
                                        }
                                    })
                                }

                            }
                            buttonclick2.setOnClickListener {
                                Toast.makeText(this@TimeChangeActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                                var start_time:String=startlist[pos]
                                var end_time:String=endlist[pos]
                                val day=selectedday
                                day_list.removeAt(pos)
                                startlist.removeAt(pos)
                                endlist.removeAt(pos)
                                val url = "https://nsiqczxoba.execute-api.eu-central-1.amazonaws.com/prod/changemycalendar?id="+(main.id).toString()+"&start_time="+start_time+"&end_time="+end_time+"&day="+day+"&operation=-1"
                                val formBody: RequestBody = FormBody.Builder()
                                    .add("day", day_list.toString())
                                    .add("start_time", startlist.toString())
                                    .add("end_time", endlist.toString())
                                    .build()
                                val request: Request = Request.Builder()
                                    .url(url)
                                    .post(formBody)
                                    .build()
                                val call = client.newCall(request)
                                call.enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        println(response)
                                    }
                                })
                            }
                            buttonclick3.setOnClickListener {
                                Toast.makeText(this@TimeChangeActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                                val staho: EditText = findViewById(R.id.startinghouradd)
                                val stamin: EditText = findViewById(R.id.startingminadd)
                                val endho: EditText = findViewById(R.id.endinghouradd)
                                val endminute: EditText = findViewById(R.id.endingminadd)
                                val start_time:String = staho.text.toString() + "." + stamin.text.toString()
                                val end_time:String = endho.text.toString() + "." + endminute.text.toString()
                                val sh:Int=staho.text.toString().toInt()
                                val eh:Int=endho.text.toString().toInt()
                                val sm:Int=stamin.text.toString().toInt()
                                val em:Int=endminute.text.toString().toInt()
                                val day = addselectedday
                                var count:Int=0
                                var shlist = ArrayList<Int>()
                                var ehlist = ArrayList<Int>()
                                var smlist = ArrayList<Int>()
                                var emlist = ArrayList<Int>()
                                var b:Boolean=false
                                for(i in 0 until counter) {
                                    if(day_list[i]==day){
                                        count+=1
                                        shlist.add(startlist[i].substringBefore('.').toInt())
                                        ehlist.add(endlist[i].substringBefore('.').toInt())
                                        smlist.add(startlist[i].substringAfter('.').toInt())
                                        emlist.add(endlist[i].substringAfter('.').toInt())
                                    }
                                }
                                for(i in 0 until count) {
                                    if((eh>=ehlist[i]&&(sh<ehlist[i]||(sh==ehlist[i]&&sm<emlist[i])))||(eh<=ehlist[i]&&(eh>shlist[i]||(eh==shlist[i]&&em>smlist[i])))){
                                        b=true
                                    }
                                }
                                if((sh>eh)||((sh==eh)&&(sm>=em))||(b)){
                                    Toast.makeText(this@TimeChangeActivity, "The time is not valid. Please enter a valid time interval", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    for(i in 0 until counter) {
                                        day_list.add(addselectedday)
                                        startlist.add(start_time)
                                        endlist.add(end_time)
                                    }
                                    val url = "https://nsiqczxoba.execute-api.eu-central-1.amazonaws.com/prod/changemycalendar?id="+(main.id).toString()+"&start_time="+start_time+"&end_time="+end_time+"&day="+day+"&operation=1"
                                    val formBody: RequestBody = FormBody.Builder()
                                        .add("day", day_list.toString())
                                        .add("start_time", startlist.toString())
                                        .add("end_time", endlist.toString())
                                        .build()
                                    val request: Request = Request.Builder()
                                        .url(url)
                                        .post(formBody)
                                        .build()
                                    val call = client.newCall(request)
                                    call.enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            println(response)
                                        }
                                    })
                                }
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //Toast.makeText(parent!!.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
    }
}

class Times( val day: List<String>, val start_time: List<String>, val end_time: List<String>) {

}

class TimesItem(val day: String, val start_time: String, val end_time: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.wwh).into(viewHolder.itemView.imageView_times)

        viewHolder.itemView.textView_timerows.text = day
        viewHolder.itemView.textView_timerows2.text = start_time
        viewHolder.itemView.textView_timerows3.text = end_time

    }

    override fun getLayout(): Int {
        return R.layout.time_rows
    }

}