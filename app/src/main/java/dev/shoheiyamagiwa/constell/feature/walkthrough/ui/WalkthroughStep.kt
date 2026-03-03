package dev.shoheiyamagiwa.constell.feature.walkthrough.ui

import androidx.annotation.StringRes

public data class WalkthroughStep(
    @param:StringRes val titleId: Int,
    @param:StringRes val descriptionId: Int,
    val showMainNode: Boolean = false,
    val showSatelliteNodes: Boolean = false,
    val showEdges: Boolean = false,
    val showLogo: Boolean = false
)