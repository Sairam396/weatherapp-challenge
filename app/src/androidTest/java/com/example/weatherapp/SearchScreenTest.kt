package com.example.weatherapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    // Launch the real MainActivity so it renders the NavGraph -> SearchScreen.
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun searchScreen_showsBasicUi() {
        // Top bar title
        rule.onNodeWithText("Weather").assertIsDisplayed()

        // Buttons (these are normal Text nodes, so no special handling needed)
        rule.onNodeWithText("Search").assertIsDisplayed()
        rule.onNodeWithText("Use Location").assertIsDisplayed()

        // In Material3, TextField labels sometimes appear in the unmerged semantics tree,
        // so we set useUnmergedTree = true.
        rule.onNodeWithText(
            "Enter US city (ex: Austin or Austin, TX)",
            useUnmergedTree = true
        ).assertIsDisplayed()
    }
}
