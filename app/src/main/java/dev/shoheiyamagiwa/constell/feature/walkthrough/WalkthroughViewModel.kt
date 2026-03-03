package dev.shoheiyamagiwa.constell.feature.walkthrough

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.feature.walkthrough.ui.WalkthroughStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

public class WalkthroughViewModel : ViewModel() {
    companion object {
        val WALKTHROUGH_STEPS = listOf(
            WalkthroughStep(
                titleId = R.string.walkthrough_heading_content_forgotten,
                descriptionId = R.string.walkthrough_desc_content_forgotten,
                showMainNode = true, showSatelliteNodes = false, showEdges = false, showLogo = false
            ),
            WalkthroughStep(
                titleId = R.string.walkthrough_heading_content_feature,
                descriptionId = R.string.walkthrough_desc_content_feature,
                showMainNode = true, showSatelliteNodes = true, showEdges = false, showLogo = false
            ),
            WalkthroughStep(
                titleId = R.string.walkthrough_heading_content_feature2,
                descriptionId = R.string.walkthrough_desc_content_feature2,
                showMainNode = true, showSatelliteNodes = true, showEdges = true, showLogo = false
            ),
            WalkthroughStep(
                titleId = R.string.walkthrough_heading_content_discover,
                descriptionId = R.string.walkthrough_desc_content_discover,
                showMainNode = true, showSatelliteNodes = true, showEdges = true, showLogo = true
            )
        )
    }

    private val _currentStep = MutableStateFlow(value = WALKTHROUGH_STEPS[0])
    public val currentStep: StateFlow<WalkthroughStep> = _currentStep.asStateFlow()

    /**
     * Get a walkthrough step by a page number
     */
    public fun getStepByPage(page: Int): WalkthroughStep {
        return WALKTHROUGH_STEPS[page]
    }

    /**
     * Update a walkthrough step by a page number
     */
    public fun updateStep(pageIndex: Int = 0) {
        _currentStep.value = WALKTHROUGH_STEPS[pageIndex]
    }

    /**
     * Check if the current step is the last step or not
     */
    public fun isLastStep(): Boolean {
        return _currentStep.value == WALKTHROUGH_STEPS.last()
    }

    /**
     * Get the total number of walkthrough steps
     */
    public fun getTotalSteps(): Int {
        return WALKTHROUGH_STEPS.size
    }
}

/**
 * A factory function to create WalkthroughViewModel instances
 *
 * @author shohei-yamagiwa
 */
public val walkthroughViewModelFactory = viewModelFactory() {
    initializer {
        WalkthroughViewModel()
    }
}