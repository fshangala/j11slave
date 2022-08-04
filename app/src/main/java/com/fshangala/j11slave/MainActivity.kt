package com.fshangala.j11slave

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private var model: SlaveViewModel? = null
    private var slaveStatus:TextView? = null
    var sharedPref: SharedPreferences? = null
    var toast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        true.also {
            webView!!.settings.javaScriptEnabled = it
            webView!!.settings.domStorageEnabled = it
        }
        model = ViewModelProvider(this)[SlaveViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        slaveStatus = findViewById(R.id.slaveStatus)

        startBrowser()

        model!!.connectionStatus.observe(this) {
            toast = Toast.makeText(this,it,Toast.LENGTH_SHORT)
            toast!!.show()
        }
        model!!.automationEvents.observe(this) {
            when (it.eventName) {
                "place_bet" -> {
                    placeBet(it)
                    toast = Toast.makeText(this,it.eventArgs.toString(),Toast.LENGTH_LONG)
                    toast!!.show()
                }
                "open_bet" -> {
                    onOpenBet(it)
                    toast = Toast.makeText(this,it.eventArgs.toString(),Toast.LENGTH_LONG)
                    toast!!.show()
                }
                "confirm_bet" -> {
                    confirmBet()
                }
                "click" -> {
                    confirmBet()
                }
                else -> {
                    toast = Toast.makeText(this,it.eventName,Toast.LENGTH_LONG)
                    toast!!.show()
                }
            }
        }
        model!!.browserLoading.observe(this){
            if (it == true) {
                runOnUiThread {
                    slaveStatus!!.text = "Loading..."
                }
            } else {
                runOnUiThread {
                    slaveStatus!!.text = "Loaded!"
                }
            }
        }
        model!!.createConnection(sharedPref!!)
    }

    private fun startBrowser(){
        val url = "https://jack9.io"
        webView!!.loadUrl(url)
        webView!!.webViewClient = object : WebViewClient(){

            override fun onPageFinished(view: WebView?, url: String?) {
                model!!.browserLoading.value = false
                //SystemClock.sleep(5000)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                model!!.browserLoading.value = true
            }
        }
    }

    private fun onOpenBet(automationEvents: AutomationEvents) {
        val Obacklay = automationEvents.eventArgs[1]
        val betindex = automationEvents.eventArgs[0]

        webView!!.evaluateJavascript("document.querySelectorAll(\".odd-button.$Obacklay-color\")[$betindex].click();"){
            runOnUiThread{
                slaveStatus!!.text = it
            }
        }
    }

    private fun placeBet(automationEvents: AutomationEvents) {
        val Ostake = automationEvents.eventArgs[3]

        webView!!.evaluateJavascript("var input = document.querySelector(\"ion-input.BetPlacing__input\");\n" +
                "input.value = $Ostake;"){
            runOnUiThread{
                slaveStatus!!.text = it
            }
        }
    }

    private fun confirmBet() {
        webView!!.evaluateJavascript("document.querySelector(\"button[type='submit']\").click();") {
            runOnUiThread {
                slaveStatus!!.text = it
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.y11menu,menu)

        model!!.connected.observe(this){
            if (it){
                menu.getItem(0).setIcon(R.mipmap.reset_green_round)
            } else {
                menu.getItem(0).setIcon(R.mipmap.reset_red_round)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.preferencesBtn -> {
                openConfig()
            }
            R.id.aboutBtn -> {
                openAbout()
            }
            R.id.reloadBrowserBtn -> {
                webView!!.reload()
            }
            R.id.reconnectBtn -> {
                if(model!!.connected.value == true) {
                    model!!.disconnect()
                } else {
                    model!!.createConnection(sharedPref!!)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openConfig(){
        val intent = Intent(this,ConfigActivity::class.java)
        startActivity(intent)
    }

    private fun openAbout(){
        val intent = Intent(this,AboutActivity::class.java)
        startActivity(intent)
    }
}