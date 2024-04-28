package com.example.fileselecterapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val FILE_PICKER_REQUEST_CODE = 1
    private lateinit var fileview: TextView
    private lateinit var Contextfile: TextView
    private lateinit var Decryptfile: TextView

    private val KeyBoss =  CryptoManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fileview = findViewById(R.id.fileviewuri)
        Contextfile = findViewById(R.id.Contextview)
        Decryptfile = findViewById(R.id.Decryptview)

        val chooseFileButton = findViewById<Button>(R.id.selectbutton)
        chooseFileButton.setOnClickListener {
            openFilePicker()
        }


    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        // intent.type = "*/*" // Set MIME type to all files
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // Handle selected file URI
            val selectedFileUri = data?.data

            // Use the selected file URI as needed
            selectedFileUri?.let {

                val fileContent = FileHelper.uriToByteArray(this, it)
                val key = KeyBoss.generateRandomKey(fileContent!!.size)
                val encryptedContent = KeyBoss.oneTimePadEncrypt(fileContent, key)
                val decryptedContent = KeyBoss.oneTimePadDecrypt(encryptedContent!!, key)
                val Sendingdata = Header().encapsulation(encryptedContent,key)
                Toast.makeText(this@MainActivity,"$Sendingdata",Toast.LENGTH_SHORT).show()


                Decryptfile.text = decryptedContent.toString()
                fileview.text = it.toString() // Convert Uri to String
                Contextfile.text = encryptedContent?.contentToString()  // represent byte
                //Contextfile.text = FileHelper.readFileContent(this, it) // read to represent content
            }
        }
    }

}