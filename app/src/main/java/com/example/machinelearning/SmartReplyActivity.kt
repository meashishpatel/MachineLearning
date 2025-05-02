package com.example.machinelearning

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.machinelearning.databinding.ActivityMainBinding
import com.example.machinelearning.databinding.ActivitySmartReplyBinding
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplyGenerator
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage

class SmartReplyActivity : AppCompatActivity() {

    private val conversation = mutableListOf<TextMessage>()
    private lateinit var binding: ActivitySmartReplyBinding
    private lateinit var smartReplyGenerator : SmartReplyGenerator
    private var convo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySmartReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.sendBtn.setOnClickListener{
            if(binding.userReply.text.isNotEmpty()){
                convo = "$convo \n ${binding.userReply.text}"
                binding.convo.text = convo
                conversation.add(TextMessage.createForLocalUser("${binding.userReply.text}", System.currentTimeMillis()))
                binding.userReply.text.clear()
            }
            else{
                Toast.makeText(this, "Please enter the message first", Toast.LENGTH_SHORT).show()
            }
        }
        binding.remotesendBtn.setOnClickListener{
            if(binding.userReply.text.isNotEmpty()){
                convo = "$convo \n ${binding.userReply.text}"
                binding.convo.text = convo
                val userId = "friend123"
                conversation.add(TextMessage.createForRemoteUser("${binding.userReply.text}", System.currentTimeMillis(), userId))
                getSmartReply()
                binding.userReply.text.clear()
            }
            else{
                Toast.makeText(this, "Please enter the message first", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getSmartReply(){
        smartReplyGenerator = SmartReply.getClient()
        smartReplyGenerator.suggestReplies(conversation)
            .addOnSuccessListener { result ->
                if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                    Log.d("Language", "Language not supported")
                } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                    for (suggestion in result.suggestions) {
                        val replyText = suggestion.text
                        binding.botreply.text = "Bot Suggestions: $replyText"
                        binding.userReply.setText("$replyText")
                    }
                }
            }
            .addOnFailureListener {
            }
    }
}