package com.donsdirectory.mobile.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Kumar on 25/05/2019
 */

@Parcelize
data class Contact(
    var contactId: String? = null,
    var contactName: String? = null,
    var contactNumber: String? = null,
    var isSelected: Boolean = false,
    var contactNumberList: ArrayList<String> = arrayListOf(),
    var photoUri: String? = null,
    var contactCompany: String? = null,
    var emailLabel: String? = null,
    var emailType: String? = null,
    var emailAddress: String? = null

) : Parcelable