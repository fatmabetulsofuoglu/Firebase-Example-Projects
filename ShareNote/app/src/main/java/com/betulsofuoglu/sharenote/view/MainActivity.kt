package com.betulsofuoglu.sharenote.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.betulsofuoglu.sharenote.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val currentUser = auth.currentUser

        if(currentUser != null){
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun Register(view: View){

        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        val username = usernameText.text.toString()

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                //kullanıcı adını güncelle

                val currentUser = Firebase.auth.currentUser
                val profileUpdatesRequest = userProfileChangeRequest {
                    displayName = username
                }

                if(currentUser != null){
                    currentUser.updateProfile(profileUpdatesRequest)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(applicationContext,"Profil Kullanıcı Adı Eklendi", Toast.LENGTH_LONG).show()
                            }
                        }
                }

                val intent = Intent(this, LandingPage::class.java)
                startActivity(intent)
                finish()

            }
            }.addOnFailureListener {exception ->
            Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }

    fun Login(view:View){

        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val currentUser = auth.currentUser?.displayName.toString()
                    Toast.makeText(applicationContext,"Hoşgeldin: ${currentUser}", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, LandingPage::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

    }
}