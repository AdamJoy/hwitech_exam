package com.example.hwitech_exam.ui.stock

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.hwitech_exam.MainActivity
import com.example.hwitech_exam.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 股票排序 BottomSheet UI 測試。
 *
 * 驗證：
 * 1. 排序按鈕可點擊。
 * 2. BottomSheet 正常顯示。
 * 3. ASC / DESC 選項存在。
 * 4. 選擇 ASC 後可保留選取狀態。
 */
@RunWith(AndroidJUnit4::class)
class StockSortUiTest {

    @get:Rule
    val activityRule =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun selectAscending_shouldKeepSelectedState() {

        // Act：點擊排序按鈕
        onView(withId(R.id.btnSort))
            .perform(click())

        // Assert：確認兩個排序選項顯示
        onView(withId(R.id.radioDescending))
            .check(matches(isDisplayed()))

        onView(withId(R.id.radioAscending))
            .check(matches(isDisplayed()))

        // Act：選擇升序
        onView(withId(R.id.radioAscending))
            .perform(click())

        // Act：重新開啟 BottomSheet
        onView(withId(R.id.btnSort))
            .perform(click())

        // Assert：確認升序仍然勾選
        onView(withId(R.id.radioAscending))
            .check(matches(isChecked()))

        // Assert：降序未勾選
        onView(withId(R.id.radioDescending))
            .check(matches(isNotChecked()))
    }
}