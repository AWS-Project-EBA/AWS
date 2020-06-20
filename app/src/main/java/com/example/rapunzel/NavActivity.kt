package com.example.rapunzel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_gridview.view.*
import kotlinx.android.synthetic.main.activity_nav.*


class NavActivity : AppCompatActivity() {
    var adapter: TaAdapter? = null
    var TasList = ArrayList<Tas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        // load transactions
        TasList.add(Tas("My Career Path", R.drawable.career))
        TasList.add(Tas("My HR Manager", R.drawable.hr))
        TasList.add(Tas("My Incoming Appointments", R.drawable.inap))
        TasList.add(Tas("My Past Appointments",R.drawable.pastap))
        TasList.add(Tas("My Inbox", R.drawable.inbox))
        TasList.add(Tas("My Agenda", R.drawable.agenda))
        TasList.add(Tas("My Financial Situation", R.drawable.finance))
        TasList.add(Tas("My Services", R.drawable.services))
        TasList.add(Tas("My Service Locations", R.drawable.sloc))
        adapter = TaAdapter(this, TasList)

        gvTas.adapter = adapter

        gvTas.setOnItemClickListener(OnItemClickListener { parent, v, position, id ->
            when (position) {
                0 ->{
                    val intent = Intent(this, CareerPathActivity::class.java)
                    startActivity(intent)
                }
                1 ->{
                    val intent = Intent(this, HRManagerActivity::class.java)
                    startActivity(intent)
                }
                2 ->{
                    val intent = Intent(this, IncomingAppActivity::class.java)
                    startActivity(intent)
                }
                3 ->{
                    val intent = Intent(this, PastAppActivity::class.java)
                    startActivity(intent)
                }
                4 ->{
                    val intent = Intent(this, InboxActivity::class.java)
                    startActivity(intent)
                }
                5 ->{
                    val intent = Intent(this, AgendaActivity::class.java)
                    startActivity(intent)
                }
                6 ->{
                    val intent = Intent(this, FinanceActivity::class.java)
                    startActivity(intent)
                }
                7 ->{
                    val intent = Intent(this, ServicesActivity::class.java)
                    startActivity(intent)
                }
                8 ->{
                    val intent = Intent(this, ServiceLocationsActivity::class.java)
                    startActivity(intent)
                }
            }
        })

    }
    class TaAdapter : BaseAdapter {
        var TasList = ArrayList<Tas>()
        var context: Context? = null

        constructor(context: Context, TasList: ArrayList<Tas>) : super() {
            this.context = context
            this.TasList = TasList
        }

        override fun getCount(): Int {
            return TasList.size
        }

        override fun getItem(position: Int): Any {
            return TasList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val Ta = this.TasList[position]

            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var TaView = inflator.inflate(R.layout.activity_gridview, null)
            TaView.imgTa.setImageResource(Ta.image!!)
            TaView.TaName.text = Ta.name!!

            return TaView
        }
    }
}
