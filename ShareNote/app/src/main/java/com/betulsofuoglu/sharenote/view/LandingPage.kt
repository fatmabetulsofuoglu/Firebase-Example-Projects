package com.betulsofuoglu.sharenote.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.betulsofuoglu.sharenote.R
import com.betulsofuoglu.sharenote.adapter.PostAdapter
import com.betulsofuoglu.sharenote.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_landing_page.*

class LandingPage : AppCompatActivity() {

    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    var sharedList = ArrayList<Post>()
    private lateinit var recyclerViewAdapter: PostAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (item.itemId == R.id.share) {
            var intent = Intent(this, ShareActivity::class.java)
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // Initialize Firebase Auth
        auth = Firebase.auth
        firebaseDataSet()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = PostAdapter(sharedList)
        recyclerView.adapter = recyclerViewAdapter
    }

    fun firebaseDataSet() {
        db.collection("Shares").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents
                        sharedList.clear()

                        for (document in documents) {
                            val username = document.get("username") as String?
                            val sharedComment = document.get("sharedComment") as String?
                            val imageUrl = document.get("imageUrl") as String?


                            var installPost = Post(username, sharedComment, imageUrl)

                            sharedList.add(installPost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}