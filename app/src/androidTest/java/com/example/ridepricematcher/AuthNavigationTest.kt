package com.example.ridepricematcher

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.ridepricematcher.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class AuthNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginScreen_isDisplayed() {
        composeTestRule.onNodeWithText("Ride Price Matcher").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
    }

    @Test
    fun navigateToSignup() {
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.onNodeWithText("Create Account").assertExists()
    }

    @Test
    fun navigateToForgotPassword() {
        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.onNodeWithText("Reset Password").assertExists()
    }
}
