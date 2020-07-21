package com.donsdirectory.mobile.lib

import android.app.Activity
import android.content.Intent
import com.donsdirectory.mobile.activities.KontactPickerActivity
import com.donsdirectory.mobile.model.Contact
import com.donsdirectory.mobile.model.KontactPickerItem
import com.donsdirectory.mobile.model.MyContacts
import com.donsdirectory.mobile.util.KontactEx
import com.donsdirectory.mobile.util.KontactPickerUI

/**
 * Created by Deepak Kumar on 25/05/2019
 */

class KontactPicker {

    fun startPickerForResult(activity: Activity?, item: KontactPickerItem, requestCode: Int) {
        KontactPickerUI.setPickerUI(item)
        val intent = Intent(activity, KontactPickerActivity::class.java)
        activity.let {
            it?.startActivityForResult(intent, requestCode)
        }
    }

    companion object {
        /**
         * returns selected list of MyContacts
         */
        fun getSelectedKontacts(data: Intent?): ArrayList<MyContacts>? {
            return data?.getParcelableArrayListExtra<MyContacts>(EXTRA_SELECTED_CONTACTS)
        }

        /**
         * Get All contacts with name, phone number and photoUri
         */
        fun getAllKontactsWithUri(
            activity: Activity?,
            onSuccess: (ArrayList<Contact>) -> Unit
        ) {
            val item = KontactPickerItem()
            KontactPickerUI.setPickerUI(item)
            KontactEx().getAllContacts(activity) {
                onSuccess.invoke(it)
            }
        }
    }

}