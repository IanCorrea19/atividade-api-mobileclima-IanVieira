package com.example.atividade_api_mobileclima_ianvieira

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var etWord: TextInputEditText
    private lateinit var btnSearch: Button
    private lateinit var tvWordResult: TextView
    private lateinit var tvPhonetic: TextView
    private lateinit var tvPartOfSpeech: TextView
    private lateinit var tvDefinition: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Conectando os elementos da tela
        etWord = findViewById(R.id.etWord)
        btnSearch = findViewById(R.id.btnSearch)
        tvWordResult = findViewById(R.id.tvWordResult)
        tvPhonetic = findViewById(R.id.tvPhonetic)
        tvPartOfSpeech = findViewById(R.id.tvPartOfSpeech)
        tvDefinition = findViewById(R.id.tvDefinition)

        btnSearch.setOnClickListener {
            val word = etWord.text.toString().trim()

            // Validação de campo vazio
            if (word.isEmpty()) {
                Toast.makeText(this, "Please, type a word!", Toast.LENGTH_SHORT).show()
            } else {
                // Truque visual: Botão vermelho e bloqueado durante a busca
                btnSearch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E53935"))
                btnSearch.text = "Searching..."
                btnSearch.isEnabled = false

                buscarPalavra(word)
            }
        }
    }

    // Função Auxiliar: Restaura a cor do botão
    private fun restaurarBotao() {
        btnSearch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#673AB7")) // Roxo padrão
        btnSearch.text = "Search Word"
        btnSearch.isEnabled = true
    }

    private fun buscarPalavra(word: String) {
        // A URL da API gratuita de Dicionário
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/${word.lowercase()}"
        val queue = Volley.newRequestQueue(this)

        // Atenção: Esta API retorna um Array JSON na raiz, por isso usamos JsonArrayRequest
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    // Pega o primeiro item do array (a palavra principal)
                    val firstEntry = response.getJSONObject(0)

                    // Extrai a Palavra e a Fonética
                    val wordText = firstEntry.getString("word")
                    val phonetic = firstEntry.optString("phonetic", "Phonetic not available")

                    // Entra na lista de significados (Meanings)
                    val meanings = firstEntry.getJSONArray("meanings")
                    val firstMeaning = meanings.getJSONObject(0)

                    // Extrai a Classe Gramatical (Noun, Verb, Adjective...)
                    val partOfSpeech = firstMeaning.getString("partOfSpeech")

                    // Extrai a primeira Definição
                    val definitions = firstMeaning.getJSONArray("definitions")
                    val definitionText = definitions.getJSONObject(0).getString("definition")

                    // Exibe tudo na tela
                    tvWordResult.text = wordText.replaceFirstChar { it.uppercase() }
                    tvPhonetic.text = phonetic
                    tvPartOfSpeech.text = "Part of Speech: ${partOfSpeech.uppercase()}"
                    tvDefinition.text = "Definition: $definitionText"

                    restaurarBotao() // Sucesso! Volta o botão ao normal

                } catch (e: Exception) {
                    Toast.makeText(this, "Error reading dictionary data.", Toast.LENGTH_SHORT).show()
                    restaurarBotao()
                }
            },
            { error ->
                // O Volley joga erro 404 se a palavra não existir no dicionário
                Toast.makeText(this, "Word not found! Try another one.", Toast.LENGTH_LONG).show()

                // Limpa a tela se a palavra não existir
                tvWordResult.text = "Word not found"
                tvPhonetic.text = ""
                tvPartOfSpeech.text = ""
                tvDefinition.text = ""

                restaurarBotao() // Volta o botão ao normal
            }
        )
        queue.add(request)
    }
}