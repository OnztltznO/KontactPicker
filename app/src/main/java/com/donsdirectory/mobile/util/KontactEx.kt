package com.donsdirectory.mobile.util

import android.app.Activity
import android.provider.ContactsContract
import com.donsdirectory.mobile.model.Contact
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.onComplete

/**
 * Created by Deepak Kumar on 23/08/2019
 */

class KontactEx {

    fun getAllContacts(activity: Activity?, onCompleted: (ArrayList<Contact>) -> Unit) {
        val startTime = System.currentTimeMillis()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Organization.COMPANY
        )

        val contactMap = mutableMapOf<String, Contact>()
        val cr = activity?.contentResolver
        doAsyncResult {
            cr?.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                null, null, null
            )?.use {
                val idIndex = it.getColumnIndex(ContactsContract.Data.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                val companyIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)

                var id: String
                var name: String
                var number: String
                var email: String
                var company: String

                while (it.moveToNext()) {
                    val contacts = Contact()
                    id = it.getLong(idIndex).toString()
                    name = it.getString(nameIndex)
                    number = it.getString(numberIndex).replace(" ", "")
                    email = it.getString(emailIndex)
                    company = it.getString(companyIndex)

                    contacts.contactId = id
                    contacts.contactName = name
                    contacts.contactNumber = number
                    contacts.contactNumberList = arrayListOf(number)
                    contacts.contactEmail = email
                    contacts.contactEmailList = arrayListOf(email)
                    contacts.contactCompany = company
                    contacts.contactCompanyList = arrayListOf(company)

                    if (contactMap[id] != null) {
                        val numberList = contactMap[id]?.contactNumberList!!
                        if (!numberList.contains(number))
                            numberList.add(number)
                        contacts.contactNumberList = numberList
                        val emailList:ArrayList<String> = contactMap[id]?.contactEmailList!!
                        if (!emailList.contains(email))
                            emailList.add(email)
                        contacts.contactEmailList = emailList
                        val companyList:ArrayList<String> = contactMap[id]?.contactCompanyList!!
                        if(!companyList.contains(company))
                            companyList.add(company)
                        contacts.contactCompanyList = companyList


                    } else {
                        contactMap[id] = contacts
                    }
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