package com.fahamutech.fahamupay.business.services

import com.fahamutech.fahamupay.business.models.Message
import com.fahamutech.fahamupay.business.models.SendMessageRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface FahamupayService {
    @POST("depa/{code}/mobile/validate")
    fun sendMessages(
        @Body body: SendMessageRequest,
        @Path("code") code: String
    ): Call<Array<Message>>

    @GET("depa/{code}/mobile/sync")
    fun existsMessagesHashes(@Path("code") code: String): Call<Array<String>>
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

suspend fun getRemoteMessagesHashes(code: String, secret: String): Array<String>{
    val retrofit = getRetrofit(code,secret)
    val service = retrofit.create(FahamupayService::class.java)
    return service.existsMessagesHashes(code).await()
}