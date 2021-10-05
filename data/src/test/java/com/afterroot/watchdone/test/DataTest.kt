package com.afterroot.watchdone.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
abstract class DataTest {
    @get:Rule(order = 0)
    val hiltRule: HiltAndroidRule by lazy { HiltAndroidRule(this) }

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val testScope = TestCoroutineScope()

    @Before
    fun init() {
        hiltRule.inject()
    }
}
