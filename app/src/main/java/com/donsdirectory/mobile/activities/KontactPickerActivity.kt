package com.donsdirectory.mobile.activities

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.donsdirectory.mobile.R
import com.donsdirectory.mobile.adapters.KontactsAdapter
import com.donsdirectory.mobile.lib.RC_READ_CONTACTS
import com.donsdirectory.mobile.model.Contact
import com.donsdirectory.mobile.util.*
import kotlinx.android.synthetic.main.activity_kontact_picker.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.yesButton

/**
 * Created by Deepak Kumar on 25/05/2019
 */

class KontactPickerActivity : AppCompatActivity() {
    private var myKontacts: ArrayList<Contact>? = arrayListOf()
    private var kontactsAdapter: KontactsAdapter? = null
    private var debugMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kontact_picker)

//        initToolbar()

        kontactsAdapter =
            KontactsAdapter(myKontacts)
//            { contact, _, view ->
//                onItemClick(contact, view)
//            }
        recycler_view.init(this)
        recycler_view.adapter = kontactsAdapter
        checkPermission()

        add_new_contact_button.setOnClickListener {
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                // Sets the MIME type to match the Contacts Provider
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }
            startActivity(intent)
            kontactsAdapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.kontact_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val search = menu.findItem(R.id.action_search)?.actionView as SearchView

        search.queryHint = getString(R.string.SearchHint)
        search.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                filterContacts(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        search.setOnCloseListener {
            return@setOnCloseListener true
        }

        menu.findItem(R.id.action_search)
            ?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                    animateToolbar()
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                    myKontacts?.let { kontactsAdapter?.updateList(it) }
                    val typedValue = TypedValue()
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                    val color = typedValue.data
                    supportActionBar?.setBackgroundDrawable(color.toDrawable())
                    return true
                }
            })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.action_search -> log("Search")
            else -> super.onOptionsItemSelected(item!!)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun animateToolbar() {
        val backgroundColorAnimator = ObjectAnimator.ofObject(
            supportActionBar, "backgroundColor", ArgbEvaluator(), 0x008577, 0xffffff
        )
        backgroundColorAnimator.duration = 200
        backgroundColorAnimator.start()
    }

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    private fun filterContacts(text: String) {
        val tempList = arrayListOf<Contact>()
        for (contact in this.myKontacts!!) {
            val name = contact.contactName
            val number = contact.contactNumber
            if (name?.contains(text, true) == true || number?.contains(text) == true) {
                tempList.add(contact)
            }
        }
        kontactsAdapter?.updateList(tempList)
    }

    private fun checkPermission() {
        val contactReadPermission = isGranted(Manifest.permission.READ_CONTACTS)
        if (contactReadPermission) {
            loadContacts()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("KPA", "Permission requested")
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), RC_READ_CONTACTS)
        }
    }

    @Suppress("SameParameterValue")
    private fun isGranted(permission: String): Boolean {
        val perm = ContextCompat.checkSelfPermission(this, permission)
        return perm == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the contacts-related task you need to do.
                loadContacts()
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                alert(
                    title = KontactPickerUI.getPickerItem().permissionDeniedTitle,
                    message = KontactPickerUI.getPickerItem().permissionDeniedMsg
                ) {
                    yesButton { checkPermission() }
                }.show()
            }
            return
        }
    }

    private fun loadContacts() {
        myKontacts?.clear()
        progress_bar.show()
        val startTime = System.currentTimeMillis()
        KontactEx().getAllContacts(this) {
            myKontacts?.addAll(it)
            val fetchingTime = System.currentTimeMillis() - startTime
            if (debugMode) {
                longToast("Fetching Completed in $fetchingTime ms")
                log("Fetching Completed in $fetchingTime ms")
            }
            progress_bar.hide()
            kontactsAdapter?.notifyDataSetChanged()
        }
    }
}