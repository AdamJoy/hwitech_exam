package com.example.hwitech_exam.ui.stock

import android.content.pm.ActivityInfo
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.hwitech_exam.MainActivity
import com.example.hwitech_exam.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 股票列表螢幕旋轉 UI 測試。
 *
 * 驗證：
 * 直向：單欄
 * 橫向：雙欄
 */
@RunWith(AndroidJUnit4::class)
class StockOrientationUiTest {

    @get:Rule
    val activityRule =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun rotateScreen_shouldChangeGridSpanCount() {

        // Arrange：設定直向
        activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        // Assert：直向應該為單欄
        checkSpanCount(1)

        // Act：切換橫向
        activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        // Assert：橫向應該為雙欄
        checkSpanCount(2)

        // Act：切換回直向
        activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        // Assert：恢復單欄
        checkSpanCount(1)
    }

    /**
     * 檢查 RecyclerView 的 GridLayoutManager 欄數。
     */
    private fun checkSpanCount(expectedCount: Int) {

        onView(withId(R.id.recyclerViewStocks))
            .check(ViewAssertion { view, error ->

                if (error != null) {
                    throw error
                }

                val recyclerView = view as RecyclerView

                val layoutManager =
                    recyclerView.layoutManager as GridLayoutManager

                assertEquals(
                    expectedCount,
                    layoutManager.spanCount
                )
            })
    }
}