package com.macaosoftware.sdui.app

import com.macaosoftware.plugin.AuthPlugin
import com.macaosoftware.sdui.app.plugin.AuthPluginIOS

actual fun Platform() : Boolean{
    return true
}
actual fun OSVersion(): String{
    return "iOS OS"
}
actual fun Plugin(): AuthPlugin{
    return AuthPluginIOS()
}