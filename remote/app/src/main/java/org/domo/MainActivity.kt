package org.domo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.main_layout.*

private const val BASE_URL = "http://192.168.1.10"

class MainActivity : AppCompatActivity() {

    private var currentSpeed = MutableLiveData<FanSpeed>()
    private lateinit var queue: RequestQueue

//    private val clickListener = View.OnClickListener { view ->
//        appCompatTextView.isActivated = view == appCompatTextView
//        appCompatTextView2.isActivated = view == appCompatTextView2
//        appCompatTextView3.isActivated = view == appCompatTextView3
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        queue = Volley.newRequestQueue(this)

//        appCompatTextView.setOnClickListener(clickListener)
//        appCompatTextView2.setOnClickListener(clickListener)
//        appCompatTextView3.setOnClickListener(clickListener)

        currentSpeed.observe(this, Observer {
            appCompatTextView.isActivated = it == FanSpeed.SLOW
            appCompatTextView2.isActivated = it == FanSpeed.NORMAL
            appCompatTextView3.isActivated = it == FanSpeed.FAST
        })


        appCompatTextView.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("vmc/fan/0/speed").setValue(
                FanSpeed.SLOW.speed
            ) { databaseError, _ ->
                if (databaseError != null) {
                    localExecute(FanSpeed.SLOW)
                }
            }
        }
        appCompatTextView2.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("vmc/fan/0/speed").setValue(
                FanSpeed.NORMAL.speed
            ) { databaseError, _ ->
                if (databaseError != null) {
                    localExecute(FanSpeed.NORMAL)
                }
            }
        }
        appCompatTextView3.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("vmc/fan/0/speed").setValue(
                FanSpeed.FAST.speed
            ) { databaseError, _ ->
                if (databaseError != null) {
                    localExecute(FanSpeed.FAST)
                }
            }
        }

        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                currentSpeed.value = FanSpeed.fromInt(p0.child("vmc/fan/0/speed").value as Long)
            }
        })


        if (FirebaseAuth.getInstance().currentUser == null) {

            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                0
            )
        }
    }

    private fun localExecute(fanSpeed: FanSpeed) {
        currentSpeed.value = fanSpeed
        Toast.makeText(this, fanSpeed.path, Toast.LENGTH_LONG).show()
    }
}