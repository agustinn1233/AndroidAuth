package com.agustinf1233.authapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Bundle
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        // Setup
        if(email != null && provider != null) {
            setup(email, provider)
        }


        // Guardado de datos

        // Aunque la app se detenga se guardan los datos en las SharedPreferences.
        val prefs : SharedPreferences.Editor = getSharedPreferences(R.string.prefsValue.toString(), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setup(email: String, provider: String) {
        // Variables
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvPassword = findViewById<TextView>(R.id.tvProvider)
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)

        title = "Inicio"
        tvEmail.text = email
        tvPassword.text = provider

        // Borrado de datos
        val prefs : SharedPreferences.Editor = getSharedPreferences(R.string.prefsValue.toString(), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}