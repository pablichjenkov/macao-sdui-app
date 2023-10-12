package com.macaosoftware.sdui.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.macaosoftware.component.AndroidComponentRender
import com.macaosoftware.platform.AndroidBridge
import com.macaosoftware.sdui.app.data.SduiRemoteService
import com.macaosoftware.sdui.app.sdui.SduiComponentFactory
import com.pablichj.incubator.amadeus.Database
import com.pablichj.incubator.amadeus.storage.DriverFactory
import com.pablichj.incubator.amadeus.storage.createDatabase
import kotlinx.coroutines.launch
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val database = createDatabase(DriverFactory(this@MainActivity))

            // Init Koin after getting the database instance
            val storageModule = module { single<Database> { database } }
            /*startKoin {
                modules(storageModule)
            }*/
            val koinRootContainer = koinApplication {
                printLogger()
                modules(storageModule)
            }
            val sduiComponentFactory = SduiComponentFactory(koinRootContainer)

            val rootComponentJson = SduiRemoteService.getRootJson()
            val rootComponent = sduiComponentFactory.getComponentInstanceOf(rootComponentJson)
            val androidBridge = AndroidBridge()

            setContent {
                AndroidComponentRender(
                    rootComponent = rootComponent,
                    androidBridge = androidBridge,
                    onBackPress = { finishAffinity() }
                )
            }
        }
    }
}
