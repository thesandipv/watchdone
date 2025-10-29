/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.afterroot.watchdone.R

class LoginCustomView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

  private val customBack: AppCompatImageView
  private val unsplashAttribution: AppCompatTextView

  init {
    inflate(context, R.layout.login_custom_view, this).apply {
      customBack = findViewById(R.id.login_custom_background)
      unsplashAttribution = findViewById(R.id.unsplash_attribution_text)
      loadBackgroundImage()
    }
  }

  private fun loadBackgroundImage() {
/*
        val okHttpClient = ApiModule.provideOkHttpClient(
            UnsplashInterceptor(apiKey = BuildConfig.UNSPLASH_API_KEY),
            httpLoggingInterceptor = ApiModule.provideHttpLoggingInterceptor()
        )
        val retrofit = ApiModule.provideRetrofit(okHttpClient)
        val api = retrofit.create(UnsplashApi::class.java).getRandomPhotoCall("space")
        val imageLoader = ImageLoader.Builder(context).crossfade(true)
        whenBuildIs { // DEBUG
            imageLoader.logger(DebugLogger())
        }
        api.enqueue(object : retrofit2.Callback<Photo?> {
            override fun onResponse(call: Call<Photo?>, response: Response<Photo?>) {
                val photo = response.body()
                val request = ImageRequest.Builder(context)
                    .data(photo?.urls?.regular)
                    .headers(
                        Headers.headersOf(
                            "Accept-Version", "v1",
                            "Authorization", "Client-ID ${BuildConfig.UNSPLASH_API_KEY}",
                            "content-type", "application/json;charset=utf-8"
                        )
                    )
                    .target(customBack)
                    .build()
                imageLoader.build().enqueue(request)
                unsplashAttribution.apply {
                    text =
                        HtmlCompat.fromHtml(
                            "Photo by <a href=\"${photo?.user?.links?.html}?utm_source=thinkersspace&amp;utm_medium=referral\">${photo?.user?.name}</a> on <a href=\"https://unsplash.com/?utm_source=thinkersspace&amp;utm_medium=referral\">Unsplash</a>",
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                    movementMethod = LinkMovementMethod.getInstance()
                }
            }

            override fun onFailure(call: Call<Photo?>, t: Throwable) {
                logE("LoginCustomView/onFailure", "call $call, Exception: $t")
            }
        })
*/
  }
}
