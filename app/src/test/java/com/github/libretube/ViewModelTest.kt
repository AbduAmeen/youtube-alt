package com.github.libretube

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.junit4.MockKRule
import org.junit.Rule

open class ViewModelTest {
    @get:Rule
    val taskExecuteRule = InstantTaskExecutorRule()
    @get:Rule
    val mockkRule = MockKRule(this)
    @get:Rule
    val coroutineRule = MainCoroutineRule()
}
