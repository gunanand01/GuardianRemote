package com.guardian.remote

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.guardian.remote.receivers.GuardianAdminReceiver

class MainActivity : Activity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Firebase Initialize karein
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // 2. Device Admin Setup
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, GuardianAdminReceiver::class.java)

        // 3. UI Design (Dashboard)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 100, 40, 40)
            setBackgroundColor(Color.parseColor("#121212")) // Dark Theme
        }

        val title = TextView(this).apply {
            text = "Guardian Remote\nSetup Dashboard"
            textSize = 28f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, 50)
        }

        // Button 1: Device Admin
        val adminBtn = Button(this).apply {
            text = "Activate Device Admin (Uninstall Block)"
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            setOnClickListener { activateAdmin() }
        }

        // Button 2: Firebase Auth
        val authBtn = Button(this).apply {
            text = "Connect Device to Firebase"
            setBackgroundColor(Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            setOnClickListener { connectToFirebase() }
        }

        layout.addView(title)
        layout.addView(adminBtn)
        layout.addView(authBtn)

        setContentView(layout)
    }

    private fun activateAdmin() {
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Required to block uninstallation.")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Device Admin is already active!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToFirebase() {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                Toast.makeText(this, "Connected! UID: $userId", Toast.LENGTH_LONG).show()
                
                // Database mein initial data save karna
                if (userId != null) {
                    val deviceRef = database.reference.child("devices").child(userId)
                    deviceRef.child("status").setValue("online")
                    deviceRef.child("battery").setValue("85%")
                }
            } else {
                Toast.makeText(this, "Failed to connect: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
