package com.fahamutech.fahamupay.business.services

import com.fahamutech.fahamupay.business.models.Message
import com.fahamutech.fahamupay.business.models.SendMessageRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface FahamupayService {
    @POST("depa/{code}/mobile/validate")
    fun sendMessages(
        @Body body: SendMessageRequest,
        @Path("code") code: String
    ): Call<Array<Message>>
}

private fun getRetrofit(code: String, secret: String): Retrofit {
    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor { chain ->
        val request: Request = chain.request().newBuilder()
            .addHeader("authorization", "$code:$secret")
            .build()
        chain.proceed(request)
    }
    httpClient.callTimeout(5, TimeUnit.MINUTES)
    httpClient.connectTimeout(5, TimeUnit.MINUTES)
    httpClient.readTimeout(5, TimeUnit.MINUTES)
    httpClient.writeTimeout(5, TimeUnit.MINUTES)
    return Retrofit.Builder()
        .baseUrl("https://fahamupay-faas.bfast.fahamutech.com/")
        .client(httpClient.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

suspend fun makeMessageSyncRequest(
    body: SendMessageRequest,
    code: String,
    secret: String
): Array<Message> {
    val retrofit = getRetrofit(code, secret)
    val service = retrofit.create(FahamupayService::class.java)
//    return try{
    return service.sendMessages(body, code).await()
//    }catch (e: Throwable){
//        null
//    }
}