package dev.shoheiyamagiwa.constell.feature.walkthrough

import androidx.lifecycle.ViewModel
import dev.shoheiyamagiwa.constell.feature.walkthrough.ui.WalkthroughStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

public class WalkthroughViewModel : ViewModel() {
    private val _currentStep = MutableStateFlow(value = WalkthroughStep())
    public val currentStep: StateFlow<WalkthroughStep> = _currentStep.asStateFlow()

    public fun updateStep(pageIndex: Int = 0) {
        _currentStep.update {
            it.copy() // TODO: Implement the update logic properly
        }
    }
}