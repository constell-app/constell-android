package dev.shoheiyamagiwa.constell.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
public class MainDispatcherRule(val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) : TestWatcher() {
    public override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    public override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
