package com.donsdirectory.mobile.model

/**
 * Created by Deepak Kumar on 31/08/2019
 */

class KontactPickerItem {
    var debugMode = false
    var themeResId: Int? = null
    var textBgColor: Int? = null

    //TODO: change permission error text
    var permissionDeniedTitle = "Contact Permission Request"
    var permissionDeniedMsg = "Please allow us to show contacts."
}