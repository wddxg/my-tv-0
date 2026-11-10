package com.lizongying.mytv0.requests

import android.util.Log
import com.lizongying.mytv0.MyTVApplication
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.conscrypt.Conscrypt
import java.security.Security
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

object HttpClient {
    const val TAG = "HttpClient"

    val okHttpClient: OkHttpClient by lazy {
        getSafeOkHttpClient()
    }

    private fun getSafeOkHttpClient(): OkHttpClient {
        // Init Conscrypt
        val conscrypt = Conscrypt.newProvider()
        // Add as provider
        Security.insertProviderAt(conscrypt, 1)
        // OkHttp 3.12.x
        // ConnectionSpec.COMPATIBLE_TLS = TLS1.0
        // ConnectionSpec.MODERN_TLS = TLS1.0 + TLS1.1 + TLS1.2 + TLS 1.3
        // ConnectionSpec.RESTRICTED_TLS = TLS 1.2 + TLS 1.3
        //
        // 本版本明确信任 HTTP：OkHttp 只会使用 connectionSpecs 中列出的连接规格，
        // 若只保留 TLS 规格，所有 http:// 的视频源/EPG/配置地址都会被直接拒绝。
        // 把 CLEARTEXT 放到首位即可放行明文请求，同时保留从新到旧的 TLS 兼容性。
        val connectionSpecs = listOf(
            ConnectionSpec.CLEARTEXT,
            ConnectionSpec.RESTRICTED_TLS,
            ConnectionSpec.MODERN_TLS,
            ConnectionSpec.COMPATIBLE_TLS,
        )
        val okHttpBuilder = OkHttpClient.Builder()
            .connectionSpecs(connectionSpecs)

        val userAgentInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 4.4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36"
                )
                .build()
            chain.proceed(requestWithUserAgent)
        }

        try {
            val tm = InternalX509TrustManager(MyTVApplication.getInstance().applicationContext)
            val sslContext = SSLContext.getInstance("TLS", conscrypt)
            sslContext.init(null, arrayOf(tm), null)
            okHttpBuilder.sslSocketFactory(InternalSSLSocketFactory(sslContext.socketFactory), tm)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up OkHttpClient", e)
        }

        return okHttpBuilder.dns(DnsCache()).retryOnConnectionFailure(true)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor).build()
    }
}