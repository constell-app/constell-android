package dev.shoheiyamagiwa.constell.feature.walkthrough

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

public class WalkthroughViewModelTest {
    private lateinit var viewModel: WalkthroughViewModel

    @Before
    public fun setup() {
        viewModel = WalkthroughViewModel()
    }

    @Test
    public fun `initial state is the first walkthrough step`() {
        assertEquals(WalkthroughViewModel.WALKTHROUGH_STEPS[0], viewModel.currentStep.value)
    }

    @Test
    public fun `getStepByPage returns correct step`() {
        val step = viewModel.getStepByPage(1)
        assertEquals(WalkthroughViewModel.WALKTHROUGH_STEPS[1], step)
    }

    @Test
    public fun `updateStep updates currentStep`() {
        viewModel.updateStep(pageIndex = 2)
        assertEquals(WalkthroughViewModel.WALKTHROUGH_STEPS[2], viewModel.currentStep.value)
    }

    @Test
    public fun `isLastStep returns true only for the last step`() {
        // Initially not last step
        assertFalse(viewModel.isLastStep())

        // Update to last step
        val lastPageIndex = WalkthroughViewModel.WALKTHROUGH_STEPS.size - 1
        viewModel.updateStep(lastPageIndex)
        assertTrue(viewModel.isLastStep())
    }

    @Test
    public fun `getTotalSteps returns correct size`() {
        assertEquals(WalkthroughViewModel.WALKTHROUGH_STEPS.size, viewModel.getTotalSteps())
    }
}
