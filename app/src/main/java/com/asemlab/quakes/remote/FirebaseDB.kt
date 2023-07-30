package com.asemlab.quakes.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.asemlab.quakes.BuildConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

object FirebaseDB {

    private val database = Firebase.database
    private val TAG = "FirebaseDB"
    val forceUpdate = MutableLiveData<Boolean>()

    fun getVersion() {
        val myRef = database.getReference("version")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {

                    val version = dataSnapshot.getValue<Int>() ?: -1
                    forceUpdate.postValue(version > BuildConfig.VERSION_CODE)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to convert value. ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }

}