package com.macaosoftware.sdui.app.viewmodel.amadeus.factory

import com.macaosoftware.component.viewmodel.ComponentViewModelFactory
import com.macaosoftware.component.viewmodel.StateComponent
import com.macaosoftware.sdui.app.viewmodel.amadeus.viewmodel.AHomeScreenViewModel


class AHomeViewModelFactory(

) : ComponentViewModelFactory<AHomeScreenViewModel> {
    override fun create(component: StateComponent<AHomeScreenViewModel>): AHomeScreenViewModel {
        return AHomeScreenViewModel(component)
    }
}
