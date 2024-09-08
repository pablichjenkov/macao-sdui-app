package com.macaosoftware.sdui.app

import androidx.compose.ui.window.ComposeUIViewController
import com.macaosoftware.app.MacaoKoinApplication
import com.macaosoftware.app.MacaoKoinApplicationState
import com.macaosoftware.app.StartupTaskRunnerDefault
import com.macaosoftware.plugin.AppTheme
import com.macaosoftware.plugin.account.AccountPlugin
import com.macaosoftware.plugin.account.AccountPluginWrapperBase
import com.macaosoftware.plugin.account.createAccountPlugin
import com.macaosoftware.sdui.app.startup.ComposeAppRootComponentInitializer
import com.macaosoftware.sdui.app.startup.DatabaseMigrationStartupTask
import com.macaosoftware.sdui.app.startup.LaunchDarklyStartupTask
import com.macaosoftware.sdui.app.startup.SdkXyzStartupTask
import platform.UIKit.UIViewController

fun buildDemoMacaoApplication(
    iosBridge: IosBridge
): UIViewController = ComposeUIViewController {

    val startupTasks = listOf(
        DatabaseMigrationStartupTask(),
        LaunchDarklyStartupTask(),
        SdkXyzStartupTask()
    )
    val applicationState = MacaoKoinApplicationState(
        rootKoinModuleInitializer = IosKoinModuleInitializer(iosBridge),
        startupTaskRunner = StartupTaskRunnerDefault(startupTasks),
        rootComponentInitializer = ComposeAppRootComponentInitializer()
    )

    AppTheme {
        MacaoKoinApplication(applicationState = applicationState)
    }
}

fun createPlatformBridge(
    accountPlugin: AccountPlugin
): IosBridge {
    
    return IosBridge(accountPlugin = accountPlugin)
}

fun createPlatformBridge2(
    accountPluginWrapperBase: AccountPluginWrapperBase
): IosBridge {

    val accountPlugin = createAccountPlugin(accountPluginWrapperBase)
    return IosBridge(accountPlugin = accountPlugin)
}