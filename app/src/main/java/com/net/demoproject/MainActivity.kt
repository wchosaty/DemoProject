package com.net.demoproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.net.demoproject.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink

class MainActivity : AppCompatActivity() {
    private val TAG = "TAG ${javaClass.simpleName}"
    private val url = "http://10.0.2.2:8080/CoroutineGson/GsonTest"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel : TestViewModel by viewModels()
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        var j = JsonObject()
        j.addProperty("a", "30")
        j.addProperty("b", "45")
        binding.btUTF8.setOnClickListener {
            toOutStream(j.toString())
        }

        binding.btGson.setOnClickListener {
            toPostGson(j.toString())
        }
    }
    fun toOutStream(s: String) {
        val requestBody = object : RequestBody() {
            override fun contentType() = "text/x-markdown; charset=utf-8".toMediaType()
            override fun writeTo(sink: BufferedSink) {
                sink.writeUtf8(s) // 範例 s = {"a":"30" , "b":"45" }
                sink.writeUtf8("7654") // 多串"7654"
            }
        }
        var client = OkHttpClient.Builder().build()
        var request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        CoroutineScope(Dispatchers.IO).launch{
            client.newCall(request).execute().use { response: Response ->
                if(response.isSuccessful) {
                    Log.d(TAG,"back : "+response.body?.string() )

                }else{
                    Log.d(TAG,"Fail : "+response.message)
                }
            }
        }
    }

    fun toPostGson(s: String){
        val requestBody = s
        var client = OkHttpClient.Builder().build()
        var request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("text/x-markdown; charset=utf-8".toMediaType()))
            .build()
        CoroutineScope(Dispatchers.IO).launch{
            val call = client.newCall(request).execute()
            call.body?.let {
                Log.d(TAG,"GsonBack :"+it.string())
            }
        }
    }
}