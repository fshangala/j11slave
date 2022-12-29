package com.fshangala.j11slave

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)
        val betSite = BetSite()
        val betSitesAdapter:ArrayAdapter<CharSequence> = ArrayAdapter(this,android.R.layout.simple_spinner_item,betSite.sites)
        betSitesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val hostIP = findViewById<EditText>(R.id.inputHostIp)
        hostIP.setText(sharedPref.getString("hostIp","192.168.43.145"))

        val hostPort = findViewById<EditText>(R.id.inputHostPort)
        hostPort.setText(sharedPref.getInt("hostPort",8000).toString())

        val hostCode = findViewById<EditText>(R.id.inputCode)
        hostCode.setText(sharedPref.getString("hostCode","sample"))

        val betSiteSpinner = findViewById<Spinner>(R.id.betSiteSpinner)
        betSiteSpinner.adapter = betSitesAdapter
        val selectedBetSite = betSitesAdapter.getPosition(sharedPref!!.getString("betSite","jack9"))
        betSiteSpinner.setSelection(selectedBetSite)
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)
        val betSite = BetSite()
        val betSitesAdapter:ArrayAdapter<CharSequence> = ArrayAdapter(this,android.R.layout.simple_spinner_item,betSite.sites)
        betSitesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val hostIP = findViewById<EditText>(R.id.inputHostIp)
        hostIP.post {
            hostIP.setText(sharedPref.getString("hostIp", "192.168.43.145"))
        }

        val hostPort = findViewById<EditText>(R.id.inputHostPort)
        hostPort.post {
            hostPort.setText(sharedPref.getInt("hostPort", 8000).toString())
        }

        val hostCode = findViewById<EditText>(R.id.inputCode)
        hostCode.post {
            hostCode.setText(sharedPref.getString("hostCode", "sample"))
        }

        val betSiteSpinner = findViewById<Spinner>(R.id.betSiteSpinner)
        betSiteSpinner.post {
            betSiteSpinner.adapter = betSitesAdapter
            val selectedBetSite = betSitesAdapter.getPosition(sharedPref!!.getString("betSite","jack9"))
            betSiteSpinner.setSelection(selectedBetSite)
        }
    }

    fun savePreferences(view: View){
        val hostIP = findViewById<EditText>(R.id.inputHostIp)
        val hostPort = findViewById<EditText>(R.id.inputHostPort)
        val hostCode = findViewById<EditText>(R.id.inputCode)
        val betSite = findViewById<Spinner>(R.id.betSiteSpinner)
        val sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)
        val editSharedPref = sharedPref.edit()
        editSharedPref.putString("hostIp",hostIP.text.toString())
        editSharedPref.putInt("hostPort",hostPort.text.toString().toInt())
        editSharedPref.putString("hostCode",hostCode.text.toString())
        editSharedPref.putString("betSite",betSite.selectedItem.toString())
        editSharedPref.apply()
        openMain()
        //finish()
    }

    private fun openMain(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}