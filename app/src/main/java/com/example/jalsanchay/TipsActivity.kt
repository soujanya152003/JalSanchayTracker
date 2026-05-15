package com.example.jalsanchay

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jalsanchay.databinding.ActivityTipsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class TipsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTipsBinding

    // PASTE YOUR GEMINI API KEY HERE
    private val apiKey = "AIzaSyBCNXXSsLuDifPby3QOpr-YZlYXN6jrrOY"
    private val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetTips.setOnClickListener {
            getTipsFromGemini()
        }
    }

    private fun getTipsFromGemini() {
        binding.progressBarTips.visibility = View.VISIBLE
        binding.tvTipsResult.text = getString(R.string.tips_fetching)
        binding.btnGetTips.isEnabled = false

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    callGeminiApi()
                }
                binding.tvTipsResult.text = result
            } catch (e: Exception) {
                binding.tvTipsResult.text = getString(R.string.tips_error, e.message ?: "Unknown Error")
                Toast.makeText(this@TipsActivity, "Failed to fetch tips", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBarTips.visibility = View.GONE
                binding.btnGetTips.isEnabled = true
            }
        }
    }

    private fun callGeminiApi(): String {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        val prompt = """
            Give me 8 practical tips for improving rainwater harvesting at home in India.
            Cover topics like: roof maintenance, first flush diverters, storage tanks,
            filtration, runoff optimization, and seasonal planning.
            Format each tip with a number and clear explanation.
            Keep each tip concise (2-3 lines).
        """.trimIndent()

        val body = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }.toString()

        val writer = OutputStreamWriter(connection.outputStream)
        writer.write(body)
        writer.flush()

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            reader.forEachLine { response.append(it) }

            val jsonResponse = JSONObject(response.toString())
            return jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } else {
            val errorStream = connection.errorStream
            val errorResponse = if (errorStream != null) {
                val reader = BufferedReader(InputStreamReader(errorStream))
                val response = StringBuilder()
                reader.forEachLine { response.append(it) }
                response.toString()
            } else {
                "No error message from API"
            }
            throw Exception("API Error $responseCode: ${errorResponse.take(100)}...")
        }
    }
}
