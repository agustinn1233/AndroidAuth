package com.agustinf1233.authapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup
        setup()
        // Session
        session()
    }


    private fun session() {
        val prefs : SharedPreferences.Editor = getSharedPreferences(R.string.prefsValue.toString(), Context.MODE_PRIVATE).edit()
        val email = prefs.putString("email", null)
        val provider = prefs.putString("provider", null)

        if(email != null && provider != null) {
            showHome(email.toString(), ProviderType.GOOGLE)
        }
        prefs.apply()
    }

    private fun setup() {

        // Variables
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnGoogleAuth = findViewById<Button>(R.id.btnGoogleAuth)

        title = "Autentication"


        btnRegister.setOnClickListener {
            if(etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                // Firebase
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        btnSignIn.setOnClickListener {
            if(etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                // Firebase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        btnGoogleAuth.setOnClickListener {
            // Configuracion

            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Acetar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email : String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            try {
                if(account != null ) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful) {
                            if(account.email != null ) {
                                showHome(account.email.toString(), ProviderType.GOOGLE)
                            } else {
                                showAlert()
                            }
                        }
                    }
                }

            } catch (e: ApiException) {
                showAlert()
            }

        }
    }
}