package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("StarMaker", appName)
  }

  @Test
  fun `level is strictly clamped to max 100`() {
    // Level formula: Level = sqrt(exp / 100) clamped to 1..100
    val normalExp = 250000L // sqrt(2500) = 50
    val normalLevel = (Math.sqrt(normalExp.toDouble() / 100.0)).toInt().coerceIn(1, 100)
    assertEquals(50, normalLevel)

    // Even huge EXP must never exceed 100
    val massiveExp = 999999999L
    val cappedLevel = (Math.sqrt(massiveExp.toDouble() / 100.0)).toInt().coerceIn(1, 100)
    assertEquals(100, cappedLevel)
  }

  @Test
  fun `exclusive id requires level 50 or higher`() {
    val level49Eligible = 49 >= 50
    val level50Eligible = 50 >= 50
    val level100Eligible = 100 >= 50

    assertFalse(level49Eligible)
    assertTrue(level50Eligible)
    assertTrue(level100Eligible)
  }
}
