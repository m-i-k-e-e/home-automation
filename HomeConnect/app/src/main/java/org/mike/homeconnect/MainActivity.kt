package org.mike.homeconnect

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

private const val BASE_URL = "http://192.168.1.10"
private const val SLOW_PATH = "/slow"
private const val NORMAL_PATH = "/normal"
private const val FAST_PATH = "/fast"

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseDatabase.getInstance().getReference("vmc/fan/0/speed")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    Toast.makeText(this@MainActivity, "${p0.value}", Toast.LENGTH_LONG).show()
                    val queue = Volley.newRequestQueue(this@MainActivity)
                    val url = "$BASE_URL${if (p0.value == 0L) {
                        SLOW_PATH
                    } else {
                        if (p0.value == 1L) {
                            NORMAL_PATH
                        } else {
                            FAST_PATH
                        }
                    }}"

                    val stringRequest = StringRequest(
                        Request.Method.GET, url,
                        Response.Listener { response ->
                            tv_status.text = response.toString()

                        },
                        Response.ErrorListener {
                            tv_status.text = "${it.message}"
                        })
                    queue.add(stringRequest)
                }
            })
    }
}
