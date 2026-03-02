package dev.shoheiyamagiwa.constell.feature.walkthrough.ui

public data class WalkthroughStep(
    val title: String = "",
    val description: String = "",
    val showMainNode: Boolean = false,
    val showSatelliteNodes: Boolean = false,
    val showEdges: Boolean = false,
    val showLogo: Boolean = false
)