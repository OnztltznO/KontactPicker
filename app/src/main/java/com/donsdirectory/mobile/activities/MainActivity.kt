package com.donsdirectory.mobile.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.donsdirectory.mobile.R
import com.donsdirectory.mobile.adapters.KontactsAdapter
import com.donsdirectory.mobile.databinding.ActivityMainBinding
import com.donsdirectory.mobile.lib.KontactPicker
import com.donsdirectory.mobile.model.Contact
import com.donsdirectory.mobile.model.KontactPickerItem
import com.donsdirectory.mobile.util.init

/**
 * Created by Deepak Kumar on 25/05/2019
 */

class MainActivity : AppCompatActivity() {
    private var myContacts: ArrayList<Contact>? = ArrayList()
    private var contactsAdapter: KontactsAdapter? = null

    private val debugModeCheck = MutableLiveData<Boolean>()
    private val imageModeGroup = MutableLiveData<Int>()
    private val selectionTickViewGroup = MutableLiveData<Int>()
    private val selectionModeGroup = MutableLiveData<Int>()
    private var colorDefault: Int? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        binding = DataBindingUtil.setContentView(this,
//            R.layout.activity_main
//        )
//        binding.activity = this
//        binding.lifecycleOwner = this

        contactsAdapter =
            KontactsAdapter(myContacts)
        binding.recyclerView.init(applicationContext)
        binding.recyclerView.adapter = contactsAdapter

        binding.kontactPickerBtn.setOnClickListener { openKontactPicker() }
        binding.getAllKontactBtn.setOnClickListener { showAllKontacts() }
//        binding.btnColorPicker.setOnClickListener { openColorPicker() }
    }

    private fun showAllKontacts() {
        val startTime = System.currentTimeMillis()
//        binding.progressBar.show()
        myContacts?.clear()
        contactsAdapter?.updateList(myContacts!!)
        KontactPicker.getAllKontactsWithUri(this) {
//            binding.progressBar.hide()
            for (contact in it) {
                myContacts?.add(
                    Contact(
                        contact.contactName,
                        contact.contactNumber
//                        contact.photoUri
                    )
                )
            }
            contactsAdapter?.updateList(myContacts!!)

            val fetchingTime = System.currentTimeMillis() - startTime
            Log.d("Main","Fetching Completed in $fetchingTime ms")
        }
    }

    private fun openKontactPicker() {
        val item = KontactPickerItem().apply {
            debugMode = debugModeCheck.value ?: false
            //            textBgColor = ContextCompat.getColor(this@MainActivity, R.color.colorBlue100)
            colorDefault?.let { textBgColor = it }
            themeResId = R.style.CustomTheme
        }

        KontactPicker()
            .startPickerForResult(this, item, 3000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 3000) {
            val list = KontactPicker.getSelectedKontacts(data)
            myContacts = arrayListOf()
            if (list != null) {
                for (contact in list) {
                    myContacts?.add(
                        Contact(
                            contact.contactName,
                            contact.contactNumber
                        )
                    )
                }
            }
            contactsAdapter?.updateList(myContacts!!)
        }
    }

}