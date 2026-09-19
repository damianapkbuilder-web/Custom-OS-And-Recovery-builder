package com.example

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.HoloApplicationTheme
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloDarkBackground
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      HoloApplicationTheme {
        GreetingScreenPreviewContent()
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

@androidx.compose.runtime.Composable
fun GreetingScreenPreviewContent() {
  Surface(
    color = HoloDarkBackground
  ) {
    Text(
      text = "Recovery & ROM Studio",
      color = HoloBlueLight
    )
  }
}
