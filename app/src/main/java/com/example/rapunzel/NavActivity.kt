package com.example.rapunzel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_nav.*

class NavActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        my_career_path.setOnClickListener {
            val intent = Intent(this, CareerPathActivity::class.java)
            startActivity(intent)
        }

        my_hr_manager.setOnClickListener {
            val intent = Intent(this, HRManagerActivity::class.java)
            startActivity(intent)
        }

        my_incoming_appointments.setOnClickListener {
            val intent = Intent(this, IncomingAppActivity::class.java)
            startActivity(intent)
        }

        my_past_appointments.setOnClickListener {
            val intent = Intent(this, PastAppActivity::class.java)
            startActivity(intent)
        }

        my_inbox.setOnClickListener {
            val intent = Intent(this, InboxActivity::class.java)
            startActivity(intent)
        }

        my_agenda.setOnClickListener {
            val intent = Intent(this, AgendaActivity::class.java)
            startActivity(intent)
        }

        my_financial_situation.setOnClickListener {
            val intent = Intent(this, FinanceActivity::class.java)
            startActivity(intent)
        }

        my_services.setOnClickListener {
            val intent = Intent(this, ServicesActivity::class.java)
            startActivity(intent)
        }

        my_service_locations.setOnClickListener {
            val intent = Intent(this, ServiceLocationsActivity::class.java)
            startActivity(intent)
        }

    }
}
