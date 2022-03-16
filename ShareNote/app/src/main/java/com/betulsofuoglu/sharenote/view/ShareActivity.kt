package com.betulsofuoglu.sharenote.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.betulsofuoglu.sharenote.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_share.*
import java.util.*


class ShareActivity : AppCompatActivity() {

    val db = Firebase.firestore
    val storage = Firebase.storage
    private lateinit var auth: FirebaseAuth
    var selectImage: Uri? = null
    var selectBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        auth = Firebase.auth
    }

    fun share(view: View) {

        if (selectImage != null) {

            val reference = storage.reference
            val uuid = UUID.randomUUID()
            var imageName = "${uuid}.jpg"

            val imageReference = reference.child("images").child(imageName)

            imageReference.putFile(selectImage!!).addOnCompleteListener { task ->
                //URL alınacak

                val uploadedImageReference = reference.child("images").child(imageName)
                uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    saveDatabase(downloadUrl)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            saveDatabase(null)
        }
    }

    private fun saveDatabase(downloadUrl: String?) {

        val sharedComment = sharingText.text.toString()
        val username = Firebase.auth.currentUser!!.displayName.toString()
        val date = Timestamp.now()


        val shareMap = hashMapOf<String, Any>()
        shareMap.put("sharedComment", sharedComment)
        shareMap.put("username", username)
        shareMap.put("date", date)

        if (downloadUrl != null) {
            shareMap.put("imageUrl", downloadUrl)
        }

        db.collection("Shares").add(shareMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    fun addImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin verilmemiş, izin istenmesi gerekir
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )

        } else {
            //izin verilmiş, galeriye gidilebilir

            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //izin verildi
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectImage = data.data
            imageView.visibility = View.VISIBLE;

            if (selectImage != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source =
                        ImageDecoder.createSource(this.contentResolver, selectImage!!)
                    selectBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(selectBitmap)

                } else {
                    selectBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, selectImage)
                    imageView.setImageBitmap(selectBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}