package com.donsdirectory.mobile.util

import android.app.Activity
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import com.donsdirectory.mobile.model.Contact
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.onComplete

/**
 * Created by Deepak Kumar on 23/08/2019
 */

class KontactEx {

//    private fun getEmails(activity: Activity?, contactId: Int? = null) {
//        val emails = SparseArray<ArrayList<Email>>()
//        val uri = Email.CONTENT_URI
//        val projection = arrayOf(
//            ContactsContract.Data.RAW_CONTACT_ID,
//            Email.DATA,
//            Email.TYPE,
//            Email.LABEL
//        )
//
//        val selection = "${ContactsContract.Data.RAW_CONTACT_ID} = ?"
//        val selectionArgs = arrayOf(contactId.toString())
//        val cr = activity?.contentResolver
//
//        var cursor: Cursor? = null
//
//        cr?.query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
//            null, null, null
//        )?.use {
//            if (cursor?.moveToFirst()!!) {
//                do {
//                    val id = cursor.getIntValue(ContactsContract.Data.RAW_CONTACT_ID)
//                    val email =
//                        cursor.getStringValue(Email.DATA)
//                            ?: continue
//                    val type = cursor.getIntValue(Email.TYPE)
//                    val label =
//                        cursor.getStringValue(Email.LABEL)
//                            ?: ""
//
//                    if (emails[id] == null) {
//                        emails.put(id, ArrayList())
//
//                    }
//                    emails[id]!!.add(Email(email, type, label))
//
//                } while (cursor.moveToNext())
//            }
//
//
//            return emails
//        }
//    }

    fun getAllContacts(activity: Activity?, onCompleted: (ArrayList<Contact>) -> Unit) {
        val startTime = System.currentTimeMillis()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            Email.ADDRESS,
            Email.TYPE,
            Email.LABEL,
            ContactsContract.CommonDataKinds.Organization.COMPANY
        )

        val contactMap = mutableMapOf<String, Contact>()
        val cr = activity?.contentResolver
        doAsyncResult {
            cr?.query(
                ContactsContract.Data.CONTENT_URI, projection,
                null, null, null
            )?.use {

//                it.getLong(it.getColumnIndex()).toString()


                Log.d("KontactEx","REACHED WHILE LOOP KONTACTEX")
                while (it.moveToNext()) {
                    val contacts = Contact()
                    contacts.contactId = it.getLong(it.getColumnIndex(ContactsContract.Data.CONTACT_ID)).toString()
                    contacts.contactName = it.getLong(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).toString()
                    contacts.contactNumber = it.getLong(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).toString()
//                contacts.contactNumberList = arrayListOf(number)
                    contacts.emailAddress = it.getLong(it.getColumnIndex(Email.ADDRESS)).toString()
                    contacts.emailType = it.getLong(it.getColumnIndex(Email.TYPE)).toString()
                    contacts.emailLabel = it.getLong(it.getColumnIndex(Email.LABEL)).toString()
//                    contacts.contactCompany = ContactsContract.CommonDataKinds.Organization.COMPANY


                    Log.d("KontactEx","Contact Id: " + contacts.contactId)
                    Log.d("KontactEx","Contact Name: " + contacts.contactName)
                    Log.d("KontactEx","Contact Phone Number: " + contacts.contactNumber.toString())
                    Log.d("KontactEx","Contact Email: " + contacts.emailType)

//                    contactMap[id] = contacts
//                    Log.d("KontactEx","Contact Company: " + contacts.contactCompany)

//                    if (contactMap[id] != null) {
//                        val numberList = contactMap[id]?.contactNumberList!!
//                        if (!numberList.contains(number))
//                            numberList.add(number)
//                        contacts.contactNumberList = numberList
//                        val emailList:ArrayList<String> = contactMap[id]?.contactEmailList!!
//                        if (!emailList.contains(email))
//                            emailList.add(email)
//                        contacts.contactEmailList = emailList
//                        val companyList:ArrayList<String> = contactMap[id]?.contactCompanyList!!
//                        if(!companyList.contains(company))
//                            companyList.add(company)
//                        contacts.contactCompanyList = companyList
//
//
//                    } else {

//                    }
                }
                it.close()
            }
            onComplete {
                val fetchingTime = System.currentTimeMillis() - startTime
                log("Fetching Completed in $fetchingTime ms")
                onCompleted.invoke(filterContactsFromMap(contactMap))
            }
            return@doAsyncResult
        }
    }

    private fun filterContactsFromMap(contactMap: MutableMap<String, Contact>): ArrayList<Contact> {
        val myKontacts: ArrayList<Contact> = arrayListOf()
        val phoneList = arrayListOf<String>()
        contactMap.entries.forEach {
            val contact = it.value

            contact.contactNumberList.forEach { number ->
                if (!phoneList.contains(number)) {
                    val newContact = Contact(
                        contact.contactId,
                        contact.contactName,
                        number,
                        false,
                        contact.contactNumberList
                    )
                    myKontacts.add(newContact)
                    phoneList.add(number)
                }
            }
        }
        myKontacts.sortBy {
            it.contactName
        }
        return myKontacts
    }

}