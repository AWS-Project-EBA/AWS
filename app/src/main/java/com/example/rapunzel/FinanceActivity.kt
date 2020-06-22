package com.example.rapunzel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_career_path.*
import kotlinx.android.synthetic.main.activity_finance.*
import okhttp3.*
import java.io.IOException

class FinanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)

        supportActionBar?.title = "My Financial Situation"

        runOnUiThread {
            fetchFinance()
        }
    }
    fun fetchFinance(){

        val url = "https://8fa1fnj3d6.execute-api.eu-central-1.amazonaws.com/prod/financialsituation?id=12"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("Rapu", body!!)

                val gson = GsonBuilder().create()

                val finance =  gson.fromJson(body, Finance::class.java)

                runOnUiThread( Runnable {
                    kotlin.run {
                        Log.d("Rapu" , "is is: ${finance.monthly_financial}")
                        monrev.text=finance.monthly_financial.toString()
                        yearrev.text=finance.yearly_financial.toString()
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }

        } )


    }
}
class Finance( val monthly_financial: Int , val yearly_financial: Int) {

}
