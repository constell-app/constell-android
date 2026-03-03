package dev.shoheiyamagiwa.constell.feature.walkthrough

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.feature.walkthrough.ui.WalkthroughStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

public val walkthroughData = listOf(
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

public class WalkthroughViewModel : ViewModel() {
    private val _currentStep = MutableStateFlow(value = walkthroughData[0])
    public val currentStep: StateFlow<WalkthroughStep> = _currentStep.asStateFlow()

    /**
     * Update a walkthrough step by a page number
     */
    public fun updateStep(pageIndex: Int = 0) {
        _currentStep.value = walkthroughData[pageIndex];
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