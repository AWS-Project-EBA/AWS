package com.example.rapunzel

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_agenda.*
import kotlinx.android.synthetic.main.agenda_row.view.*
import okhttp3.*
import java.io.IOException
import java.lang.Integer.parseInt
import java.util.*


class AgendaActivity : AppCompatActivity(){

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        Log.d("Agenda" , "Agenda")

        supportActionBar?.title = "My Agenda"

        runOnUiThread {
            fetchAgenda()
        }

    }

    fun fetchAgenda(){

        val url = "https://l5lo98pz4j.execute-api.eu-central-1.amazonaws.com/prod/agenda?id="+(main.id).toString()

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback{

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                if(body=="{}")
                {
                    runOnUiThread( Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()
                            adapter.add(AgendaItem( "","","" ) )

                            recyclerview_agenda.adapter = adapter
                        }
                    })
                }
                else {
                    val gson = GsonBuilder().create()

                    val agenda = gson.fromJson(body, Agenda::class.java)

                    var counter = agenda.date.size

                    Log.d("Rapu", "size is: ${agenda.date.size}")

                    runOnUiThread(Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()
                            var rendezvous = arrayListOf("")
                            var daylist = arrayListOf("")
                            var monthlist = arrayListOf("")
                            var yearlist = arrayListOf("")

                            var events: MutableList<EventDay> = ArrayList()

                            val calendarView = findViewById<View>(R.id.calendarView) as CalendarView


                            for (i in 0 until counter) {
                                Log.d("Rapu", "is is: ${agenda.date[i]}")
                                adapter.add(
                                    AgendaItem(
                                        agenda.customer[i],
                                        agenda.date[i],
                                        agenda.hour[i]
                                    )
                                )
                                val year=agenda.date[i].substringBefore('-')
                                val temp=agenda.date[i].substringAfter('-')
                                var month=temp.substringBefore('-')
                                if(month.equals("01")||month.equals("02")||month.equals("03")||month.equals("04")||month.equals("05")||month.equals("06")||month.equals("07")||month.equals("08")||month.equals("09"))
                                    month=month.substringAfter('0')
                                var day=temp.substringAfter('-')
                                if(day.equals("01")||day.equals("02")||day.equals("03")||day.equals("04")||day.equals("05")||day.equals("06")||day.equals("07")||day.equals("08")||day.equals("09"))
                                    day=day.substringAfter('0')
                                rendezvous.add(agenda.customer[i]+"\n"+agenda.date[i]+"\n"+agenda.hour[i])
                                yearlist.add(year)
                                monthlist.add(month)
                                daylist.add(day)

                                var calendar = Calendar.getInstance()

                                calendar.set(parseInt(year),parseInt(month)-1,parseInt(day))

                                println(calendar.time.toString())

                                events.add(EventDay(calendar, R.drawable.tick, Color.parseColor("#228B22")))

                                calendarView.setOnDayClickListener(object : OnDayClickListener {
                                    override fun onDayClick(eventDay: EventDay) {
                                        val clickedDayCalendar = eventDay.calendar
                                        println(clickedDayCalendar.time.toString())
                                        val t=clickedDayCalendar.time.toString().substringAfter(' ')
                                        var mt=t.substringBefore(' ')
                                        if(mt.equals("Jan"))    mt="1"
                                        else if(mt.equals("Feb"))    mt="2"
                                        else if(mt.equals("Mar"))    mt="3"
                                        else if(mt.equals("Apr"))    mt="4"
                                        else if(mt.equals("May"))    mt="5"
                                        else if(mt.equals("Jun"))    mt="6"
                                        else if(mt.equals("Jul"))    mt="7"
                                        else if(mt.equals("Aug"))    mt="8"
                                        else if(mt.equals("Sep"))    mt="9"
                                        else if(mt.equals("Oct"))    mt="10"
                                        else if(mt.equals("Nov"))    mt="11"
                                        else if(mt.equals("Dec"))    mt="12"
                                        val t2=t.substringAfter(' ')
                                        var dt=t2.substringBefore(' ')
                                        if(dt.equals("01")||dt.equals("02")||dt.equals("03")||dt.equals("04")||dt.equals("05")||dt.equals("06")||dt.equals("07")||dt.equals("08")||dt.equals("09"))
                                            dt=dt.substringAfter('0')
                                        val y=t2.substringAfterLast(' ')
                                        val ad = GroupAdapter<ViewHolder>()
                                        var b:Boolean=false
                                        for(j in 1 until rendezvous.size) {
                                            if((dt.equals(daylist[j]))&&(mt.equals(monthlist[j]))&&(y.equals(yearlist[j]))){
                                                ad.add(AgendaItem(agenda.customer[j-1], agenda.date[j-1], agenda.hour[j-1]))
                                                b=true
                                            }
                                        }
                                        if(b==false){
                                            Toast.makeText(applicationContext, "No Event!" , Toast.LENGTH_LONG).show()
                                        }
                                        recyclerview_agenda.adapter = ad
                                        scroll.visibility= VISIBLE
                                        linear.visibility= VISIBLE
                                        recyclerview_agenda.visibility=VISIBLE
                                    }
                                })
                            }
                            calendarView.setEvents(events)
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

class Agenda( val date: List<String> , val hour: List<String> , val customer: List<String> ) {

}

class AgendaItem(val customer: String, val date: String, val hour: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(R.drawable.agenda).into(viewHolder.itemView.imageView_agenda)

        viewHolder.itemView.textView_agenda1.text = customer
        viewHolder.itemView.textView_agenda2.text = date
        viewHolder.itemView.textView_agenda3.text = hour

    }

    override fun getLayout(): Int {
        return R.layout.agenda_row
    }

}

