package com.bytza.getcontact

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    lateinit var textContactName: TextView
    lateinit var textContactUri: TextView
    lateinit var btnGetContactName: Button
    lateinit var btnGetContact: Button
    lateinit var textHour: TextView
    lateinit var textMin: TextView
    lateinit var btnSetAlarm: Button

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            Toast.makeText(this, "granted", Toast.LENGTH_LONG)
        }
        else {
            Toast.makeText(this, "denied", Toast.LENGTH_LONG)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initControls()

        btnGetContact.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
            }
            resultContact.launch(intent)
        }



        btnSetAlarm.setOnClickListener() {
            permission.launch(Manifest.permission.SET_ALARM)
            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Test GetContact")
            intent.putExtra(AlarmClock.EXTRA_HOUR, textHour.text.toString().toInt())
            intent.putExtra(AlarmClock.EXTRA_MINUTES, textMin.text.toString().toInt())
            startActivity(intent)
        }
    }

    fun initControls() {
        textContactName = findViewById(R.id.txtContactName)
        textContactUri = findViewById(R.id.txtContactUri)
        btnGetContact = findViewById(R.id.btnGetContact)
        textHour = findViewById(R.id.txtHour)
        textMin = findViewById(R.id.txtMin)
        btnSetAlarm = findViewById(R.id.btnSetAlarm)
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {}
            else -> {
                permission.launch(Manifest.permission.READ_CONTACTS)
            }
        }

    }
    var resultContact = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        var projection = arrayOf<String>(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        if (it.resultCode == Activity.RESULT_OK) {
            var address: Uri? = it.data?.data
            textContactUri.setText(address.toString())
            var phone: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            // var cursor = address?.let { it1 -> contentResolver.query(it1, null, null, null, null) }
            var cursor = contentResolver.query(address!!, null, null, null, null)
            // var cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    var index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val name = cursor.getString(index).toString()
                    textContactName.setText(name)
                }
            }

        }
    }


}