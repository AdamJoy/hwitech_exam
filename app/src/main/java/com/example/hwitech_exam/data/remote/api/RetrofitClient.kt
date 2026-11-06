package com.example.hwitech_exam.data.remote.api

import com.example.hwitech_exam.BuildConfig
import com.example.hwitech_exam.data.remote.TwseApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient
 *
 * 負責建立與管理 Retrofit 網路連線實例。
 *
 * 主要功能：
 * 1. 統一管理臺灣證券交易所 OpenAPI Base URL。
 * 2. 使用 OkHttpClient 處理 HTTP 網路請求。
 * 3. 設定連線、讀取與寫入逾時時間。
 * 4. 使用 GsonConverterFactory 解析 JSON 回應。
 * 5. 在 Debug 模式下輸出 API Request / Response Log。
 * 6. 提供 TwseApiService 給 Repository 呼叫。
 *
 * 採用 Kotlin object 單例模式，避免重複建立
 * Retrofit 與 OkHttpClient 實例。
 */
object RetrofitClient {

    /**
     * 臺灣證券交易所 OpenAPI Base URL
     *
     * 注意：Base URL 結尾必須包含 "/"
     */
    private const val BASE_URL =
        "https://openapi.twse.com.tw/v1/"

    /**
     * HTTP Logging Interceptor
     *
     * Debug：記錄 Request / Response 詳細內容
     * Release：關閉 Logging
     */
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * OkHttpClient
     *
     * 統一設定網路連線參數。
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Retrofit
     *
     * 負責將 API Interface 轉換為可執行的網路請求。
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * TwseApiService
     *
     * 提供 Repository 呼叫三支股票 API：
     *
     * 1. 本益比、殖利率、股價淨值比
     * 2. 收盤價、月平均價
     * 3. 每日成交資訊
     */
    val apiService: TwseApiService by lazy {
        retrofit.create(TwseApiService::class.java)
    }
}