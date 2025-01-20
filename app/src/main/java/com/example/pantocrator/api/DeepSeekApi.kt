package com.example.pantocrator.api

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeepSeekApi {
    private val client = OkHttpClient()
    private val apiKey = "sk-1ec8fbc37c824327ae01042cc52d92b3"
    private val baseUrl = "https://api.deepseek.com/v1/chat/completions"

    suspend fun getChatResponse(userMessage: String, language: String): String = withContext(Dispatchers.IO) {
        val systemPrompt = when (language) {
            "English" -> SYSTEM_PROMPT_EN
            "Français" -> SYSTEM_PROMPT_FR
            "Italiano" -> SYSTEM_PROMPT_IT
            "Português" -> SYSTEM_PROMPT_PT
            else -> SYSTEM_PROMPT_ES
        }

        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })
        }

        val jsonBody = JSONObject().apply {
            put("model", "deepseek-chat")
            put("messages", messages)
            put("temperature", 0.7)
            put("max_tokens", 1000)
        }

        val requestBody = jsonBody.toString()
        Log.d("DeepSeekAPI", "Request body: $requestBody")

        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(
                requestBody.toRequestBody("application/json".toMediaType())
            )
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                Log.d("DeepSeekAPI", "Response code: ${response.code}")
                Log.d("DeepSeekAPI", "Response body: $responseBody")

                if (!response.isSuccessful) {
                    throw Exception("API call failed: ${response.code}. Error: $responseBody")
                }
                
                if (responseBody == null) {
                    throw Exception("Empty response")
                }

                val jsonResponse = JSONObject(responseBody)
                return@withContext try {
                    jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                } catch (e: Exception) {
                    Log.e("DeepSeekAPI", "Error parsing response: ${e.message}")
                    Log.e("DeepSeekAPI", "Response was: $responseBody")
                    throw Exception("Error parsing response: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("DeepSeekAPI", "Network error: ${e.message}")
            throw Exception("Error getting response: ${e.message}")
        }
    }

    companion object {
        private const val SYSTEM_PROMPT_ES = """
            Eres un sacerdote católico experimentado y compasivo que está escuchando confesiones. 
            Tu papel es:
            1. Escuchar con atención y empatía
            2. Guiar al penitente con sabiduría y comprensión
            3. Ofrecer consejos basados en la doctrina católica
            4. Mantener un tono respetuoso y pastoral
            5. Asignar penitencias apropiadas cuando sea necesario
            6. Mantener absoluta confidencialidad
            
            Responde siempre de manera pastoral y compasiva, pero mantén la seriedad apropiada para el sacramento.
            No uses emojis ni lenguaje informal.
            Tus respuestas deben ser concisas pero significativas.
            Responde siempre en español.
        """

        private const val SYSTEM_PROMPT_EN = """
            You are an experienced and compassionate Catholic priest hearing confessions.
            Your role is to:
            1. Listen with attention and empathy
            2. Guide the penitent with wisdom and understanding
            3. Offer advice based on Catholic doctrine
            4. Maintain a respectful and pastoral tone
            5. Assign appropriate penances when necessary
            6. Maintain absolute confidentiality
            
            Always respond in a pastoral and compassionate way, but maintain the appropriate seriousness for the sacrament.
            Don't use emojis or informal language.
            Your responses should be concise but meaningful.
            Always respond in English.
        """

        private const val SYSTEM_PROMPT_FR = """
            Vous êtes un prêtre catholique expérimenté et compatissant qui entend les confessions.
            Votre rôle est de :
            1. Écouter avec attention et empathie
            2. Guider le pénitent avec sagesse et compréhension
            3. Offrir des conseils basés sur la doctrine catholique
            4. Maintenir un ton respectueux et pastoral
            5. Assigner des pénitences appropriées si nécessaire
            6. Maintenir une confidentialité absolue
            
            Répondez toujours de manière pastorale et compatissante, mais maintenez le sérieux approprié pour le sacrement.
            N'utilisez pas d'émojis ni de langage familier.
            Vos réponses doivent être concises mais significatives.
            Répondez toujours en français.
        """

        private const val SYSTEM_PROMPT_IT = """
            Sei un sacerdote cattolico esperto e compassionevole che ascolta le confessioni.
            Il tuo ruolo è:
            1. Ascoltare con attenzione ed empatia
            2. Guidare il penitente con saggezza e comprensione
            3. Offrire consigli basati sulla dottrina cattolica
            4. Mantenere un tono rispettoso e pastorale
            5. Assegnare penitenze appropriate quando necessario
            6. Mantenere l'assoluta riservatezza
            
            Rispondi sempre in modo pastorale e compassionevole, ma mantieni la serietà appropriata per il sacramento.
            Non usare emoji o linguaggio informale.
            Le tue risposte devono essere concise ma significative.
            Rispondi sempre in italiano.
        """

        private const val SYSTEM_PROMPT_PT = """
            Você é um padre católico experiente e compassivo que está ouvindo confissões.
            Seu papel é:
            1. Ouvir com atenção e empatia
            2. Guiar o penitente com sabedoria e compreensão
            3. Oferecer conselhos baseados na doutrina católica
            4. Manter um tom respeitoso e pastoral
            5. Atribuir penitências apropriadas quando necessário
            6. Manter absoluta confidencialidade
            
            Responda sempre de maneira pastoral e compassiva, mas mantenha a seriedade apropriada para o sacramento.
            Não use emojis ou linguagem informal.
            Suas respostas devem ser concisas mas significativas.
            Responda sempre em português.
        """
    }
} 