package com.example.rapunzel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_service_locations.*
import kotlinx.android.synthetic.main.activity_time_select.*
import kotlinx.android.synthetic.main.servloc_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class ServiceLocationsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    val main=Singleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_locations)
        supportActionBar?.title = "My Service Locations"

        var spinnerArray = ArrayList<String>()

        GlobalScope.launch(context = Dispatchers.IO) {
            fetchCities(spinnerArray)
            delay(6000)
            println("Here after a delay of 6 seconds")

            launch(context= Dispatchers.Main){
                runOnUiThread {
                    fetchServiceLocations(spinnerArray)
                }
            }
        }
    }
    fun fetchServiceLocations(spinnerArray: ArrayList<String>){

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
                            adapter.add(ServiceLocItem( "", "","") )

                            recyclerview_servicelocs.adapter = adapter
                        }
                    })
                }

                else{
                    val gson = GsonBuilder().serializeNulls().create()

                    val serviceloc =  gson.fromJson(body, ServiceLoc::class.java)

                    var counter = serviceloc.location.size

                    Log.d("Rapu" , "size is: ${serviceloc.location.size}")

                    runOnUiThread( Runnable {
                        kotlin.run {
                            val adapter = GroupAdapter<ViewHolder>()
                            var start:String=""
                            var end:String=""

                            var spinnerArraydist = ArrayList<String>()

                            var selectedcity:String=""

                            var pos:Int=0

                            var selecteddist:String=""

                            var posdist:Int=0

                            var selectedday:String="01"

                            var posday:Int=0
                            var selectedmonth:String="01"

                            var posmonth:Int=0
                            var selectedyear:String="2020"

                            var posyear:Int=0
                            var selecteddaydel:String="01"

                            var posdaydel:Int=0
                            var selectedmonthdel:String="01"

                            var posmonthdel:Int=0
                            var selectedyeardel:String="2020"

                            var posyeardel:Int=0

                            for(i in 0 until counter){
                                Log.d("Rapu" , "is is: ${serviceloc.location[i]}")
                                adapter.add(ServiceLocItem( serviceloc.location[i], serviceloc.start_date[i], serviceloc.end_date[i] ) )
                            }

                            recyclerview_servicelocs.adapter = adapter

                            val aa = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, spinnerArray)

                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnercities.adapter = aa

                            println(spinnerArray[3])

                            spinnercities.onItemSelectedListener = object : OnItemSelectedListener {
                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                    val city = parentView!!.getItemAtPosition(position).toString()
                                    selectedcity=city
                                    pos=position

                                    Toast.makeText(applicationContext, "City: " + selectedcity, Toast.LENGTH_SHORT).show()

                                    GlobalScope.launch(context = Dispatchers.IO) {
                                        fetchDistricts(spinnerArraydist,city)
                                        delay(1000)
                                        println("Here after a delay of 5 seconds")

                                        launch(context= Dispatchers.Main){
                                            runOnUiThread {
                                                spinner2fill(spinnerArraydist)
                                            }
                                        }
                                    }
                                    spinnerdistrict!!.onItemSelectedListener = object :
                                        AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                            val district = parentView!!.getItemAtPosition(position).toString()
                                            selecteddist=district
                                            posdist=position
                                            Toast.makeText(applicationContext, "District: " + selecteddist, Toast.LENGTH_SHORT).show()
                                            spinnerday!!.onItemSelectedListener = object :
                                                AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                                    val day = parentView!!.getItemAtPosition(position).toString()
                                                    selectedday=day
                                                    posday=position
                                                    Toast.makeText(applicationContext, "Day: " + selectedday, Toast.LENGTH_SHORT).show()

                                                }

                                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                                    // your code here
                                                }
                                            }
                                            spinnermonth!!.onItemSelectedListener = object :
                                                AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                                    val month = parentView!!.getItemAtPosition(position).toString()
                                                    selectedmonth=month
                                                    posmonth=position
                                                    Toast.makeText(applicationContext, "Month: " + selectedmonth, Toast.LENGTH_SHORT).show()

                                                }

                                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                                    // your code here
                                                }
                                            }
                                            spinneryear!!.onItemSelectedListener = object :
                                                AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                                    val year = parentView!!.getItemAtPosition(position).toString()
                                                    selectedyear=year
                                                    posyear=position
                                                    Toast.makeText(applicationContext, "Year: " + selectedyear, Toast.LENGTH_SHORT).show()

                                                }

                                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                                    // your code here
                                                }
                                            }
                                            spinnerdaydel!!.onItemSelectedListener = object :
                                                AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                                    val day = parentView!!.getItemAtPosition(position).toString()
                                                    selecteddaydel=day
                                                    posdaydel=position
                                                    Toast.makeText(applicationContext, "Day: " + selecteddaydel, Toast.LENGTH_SHORT).show()

                                                }

                                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                                    // your code here
                                                }
                                            }
                                            spinnermonthdel!!.onItemSelectedListener = object :
                                                AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                                    val month = parentView!!.getItemAtPosition(position).toString()
                                                    selectedmonthdel=month
                                                    posmonthdel=position
                                                    Toast.makeText(applicationContext, "Month: " + selectedmonthdel, Toast.LENGTH_SHORT).show()

                                                }

                                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                                    // your code here
                                                }
                                            }
                                            spinneryeardel!!.onItemSelectedListener = object :
                                                AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                                                    val year = parentView!!.getItemAtPosition(position).toString()
                                                    selectedyeardel=year
                                                    posyeardel=position
                                                    Toast.makeText(applicationContext, "Year: " + selectedyeardel, Toast.LENGTH_SHORT).show()

                                                }

                                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                                    // your code here
                                                }
                                            }
                                        }

                                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                                            // your code here
                                        }
                                    }
                                }

                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                    // your code here
                                }
                            }
                            val buttonclick:Button=findViewById(R.id.addDateServ)
                            val buttonclick2:Button=findViewById(R.id.delDateServ)

                            buttonclick.setOnClickListener {
                                Toast.makeText(this@ServiceLocationsActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                                val daydel=selecteddaydel
                                val day=selectedday
                                val monthdel=selectedmonthdel
                                val month=selectedmonth
                                val yeardel=selectedyeardel
                                val year=selectedyear
                                val start_date=day+"-"+month+"-"+year
                                val end_date=daydel+"-"+monthdel+"-"+yeardel
                                val operation:String="1"
                                val url = "https://e99wa204i9.execute-api.eu-central-1.amazonaws.com/prod/changemylocations?id="+(main.id).toString()+"&start_date="+start_date+"&end_date="+end_date+"&district_name="+selecteddist+"&city_name="+selectedcity+"&operation=1"
                                val formBody: RequestBody = FormBody.Builder()
                                    .add("id", (main.id).toString())
                                    .add("start_date", start_date)
                                    .add("end_date", end_date)
                                    .add("district_name", selecteddist)
                                    .add("city_name", selectedcity)
                                    .add("operation", operation)
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
                            buttonclick2.setOnClickListener {
                                Toast.makeText(this@ServiceLocationsActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                                val daydel=selecteddaydel
                                val day=selectedday
                                val monthdel=selectedmonthdel
                                val month=selectedmonth
                                val yeardel=selectedyeardel
                                val year=selectedyear
                                val start_date=day+"-"+month+"-"+year
                                val end_date=daydel+"-"+monthdel+"-"+yeardel
                                val operation:String="-1"
                                val url = "https://e99wa204i9.execute-api.eu-central-1.amazonaws.com/prod/changemylocations?id="+(main.id).toString()+"&start_date="+start_date+"&end_date="+end_date+"&district_name="+selecteddist+"&city_name="+selectedcity+"&operation=-1"
                                val formBody: RequestBody = FormBody.Builder()
                                    .add("id", (main.id).toString())
                                    .add("start_date", start_date)
                                    .add("end_date", end_date)
                                    .add("district_name", selecteddist)
                                    .add("city_name", selectedcity)
                                    .add("operation", operation)
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
                    })
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )


    }

    fun spinner2fill(spinnerArraydist: ArrayList<String>){
        val aa2 = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, spinnerArraydist)

        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerdistrict.adapter = aa2
    }

    fun fetchCities(spinnerArray:ArrayList<String>){
        val cityurl = "https://ukwzbvs4xk.execute-api.eu-central-1.amazonaws.com/prod/getcities"

        val requestcity = Request.Builder().url(cityurl).build()

        val clientcity = OkHttpClient()

        clientcity.newCall(requestcity).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                if(body=="{}")
                {
                    runOnUiThread( Runnable {
                        kotlin.run {
                            spinnerArray.add("")
                        }
                    })
                }

                else{
                    val gson = GsonBuilder().serializeNulls().create()

                    val cities =  gson.fromJson(body, Cities::class.java)

                    var counter2 = cities.cities.size

                    Log.d("Rapu" , "size is: ${cities.cities.size}")

                            for(i in 0 until counter2){
                                Log.d("Rapu" , "is is: ${cities.cities[i]}")
                                spinnerArray.add(cities.cities[i])
                            }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )
    }

    fun fetchDistricts(spinnerArraydist:ArrayList<String>,city:String){
        val disturl = "https://ukwzbvs4xk.execute-api.eu-central-1.amazonaws.com/prod/getdistricts?city_name="+city

        val requestdist = Request.Builder().url(disturl).build()

        val clientdist = OkHttpClient()



        clientdist.newCall(requestdist).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                if(body=="{}")
                {
                    runOnUiThread( Runnable {
                        kotlin.run {
                            spinnerArraydist.add("")
                        }
                    })
                }

                else{

                    spinnerArraydist.clear()

                    val gson = GsonBuilder().serializeNulls().create()

                    val districts =  gson.fromJson(body, Districts::class.java)

                    var counter3 = districts.districts.size

                    Log.d("Rapu" , "size is: ${districts.districts.size}")

                    runOnUiThread( Runnable {
                        kotlin.run {

                            for(i in 0 until counter3){
                                Log.d("Rapu" , "is is: ${districts.districts[i]}")
                                spinnerArraydist.add(districts.districts[i])
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
        TODO("Not yet implemented")
    }

}

class Cities( val cities: List<String>) {

}

class Districts( val districts: List<String>) {

}

class ServiceLoc( val location: List<String>, val start_date: List<String>, val end_date: List<String>) {

}

class ServiceLocItem(val location: String, val start_date: String, val end_date: String): Item<ViewHolder>() {

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

        Picasso.get().load(R.drawable.sloc).into(viewHolder.itemView.imageView_serviceloc4)

        viewHolder.itemView.textView_serviceloc4.text = start_date

        Picasso.get().load(R.drawable.sloc).into(viewHolder.itemView.imageView_serviceloc5)

        viewHolder.itemView.textView_serviceloc5.text = end_date

    }

    override fun getLayout(): Int {
        return R.layout.servloc_row
    }

}
