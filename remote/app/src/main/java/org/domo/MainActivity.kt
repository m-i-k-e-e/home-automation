package org.domo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        queue = Volley.newRequestQueue(this)

        currentSpeed.observe(this, Observer {
            speed_message.text = it.name.toLowerCase().capitalize()
        })

        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                currentSpeed.value = FanSpeed.fromInt(p0.child("vmc/fan/0/speed").value as Long)
            }
        })

        if (FirebaseAuth.getInstance().currentUser == null) {
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                0
            )
        }

        faster.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("vmc/fan/0/speed").setValue(
                currentSpeed.value!!.next().speed
            ) { databaseError, _ ->
                local_mode.visibility = View.INVISIBLE
                if (databaseError != null) {
                    local_mode.visibility = View.VISIBLE
                    localExecute(currentSpeed.value!!.next())
                }
            }
        }

        slower.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("vmc/fan/0/speed").setValue(
                currentSpeed.value!!.previous().speed
            ) { databaseError, _ ->
                local_mode.visibility = View.INVISIBLE
                if (databaseError != null) {
                    local_mode.visibility = View.VISIBLE
                    localExecute(currentSpeed.value!!.previous())
                }
            }
        }
    }

    private fun localExecute(fanSpeed: FanSpeed) {
        queue.add(StringRequest(Request.Method.GET, BASE_URL + fanSpeed.path,{}, null))
        currentSpeed.value = fanSpeed
    }
}