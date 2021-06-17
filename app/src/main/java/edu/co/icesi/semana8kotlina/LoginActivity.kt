package edu.co.icesi.semana8kotlina

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )


        loginBtn.setOnClickListener {
            val user = userET.text.toString()
            val i = Intent(this, MapsActivity::class.java)
            i.putExtra("user", user)
            startActivity(i)
        }
    }

}