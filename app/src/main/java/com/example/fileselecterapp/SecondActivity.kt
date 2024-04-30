package com.example.fileselecterapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import android.app.Activity
import android.content.Intent
import android.util.Log

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View


class SecondActivity : AppCompatActivity() {

    val startTime = System.nanoTime()
    private val FILE_PICKER_REQUEST_CODE = 1
    private lateinit var signtext: TextView
    private lateinit var ciphertext: TextView
    private lateinit var Signbutton: Button
    private lateinit var DATASEND: ByteArray

    private val KeyBoss =  CryptoManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        ciphertext = findViewById(R.id.CTMBtext)
        signtext = findViewById(R.id.SignMBtext)
        Signbutton = findViewById(R.id.button)

        limitTextView(signtext,20)
        limitTextView(ciphertext,10)


        Signbutton.setOnClickListener {
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

            // Evaluate test
            val time = Evaluate().measureTimeMillis {

                // Use the selected file URI as needed
                selectedFileUri?.let {

                    //Read content to ByteArray
                    val fileContent = FileHelper.uriToByteArray(this, it)


                    //OTP
                    val key = KeyBoss.generateRandomKey(fileContent!!.size)

                    val encryptedContent = KeyBoss.oneTimePadEncrypt(fileContent, key)

                    val CiKeyDATA = Header().GenerateDATA(encryptedContent!!, key)

                    //Hash - MD
                    val MDigest = KeyBoss.hashByteArrayWithSHA256(CiKeyDATA)

                    //ECC -Signature
                    val alias_ecc = "my_ecc_key"
                    val keyecc = KeyBoss.generateECCKeyPair(this,alias_ecc)
                    val Sign = KeyBoss.signData(keyecc.private, MDigest)

                    //RSA
                    val alias_rsa = "my_rsa_key"
                    KeyBoss.generateAndStoreRSAKeyPair(this,alias_rsa)
                    val SendDATA = concatenateByteArrays(Sign, CiKeyDATA)
                    val FinalData = KeyBoss.encryptLargeDataWithRSA(alias_rsa, SendDATA)


                    Log.d("Check", "------Finish All process------")
                    signtext.text = byteArrayToHex(Sign)
                    ciphertext.text = byteArrayToHex(SendDATA)
                    //Sending Data to cloud
                    //val Send2Cloud = Header().encapsulation(encryptedContent,key)
                    //Toast.makeText(this@MainActivity,"$Send2Cloud",Toast.LENGTH_SHORT).show()

                }
            }
            val test = "123456789"
            val gg = test.substring(4)
            val ge = test.substring(0,5)
            Log.d("Check","This is test0,5: $ge")
            Log.d("Check","This is test4: $gg")
            Log.d("Check","------The usage time is: $time ------")
        }
    }
}

fun byteArrayToHex(byteArray: ByteArray): String {
    val hexChars = CharArray(byteArray.size * 2)
    for (i in byteArray.indices) {
        val v = byteArray[i].toInt() and 0xFF
        hexChars[i * 2] = "0123456789ABCDEF"[v shr 4]
        hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
    }
    return String(hexChars)
}


fun limitTextView(textView: TextView, maxLength: Int) {
    textView.movementMethod = LinkMovementMethod.getInstance()
    val originalText = textView.text
    if (originalText.length > maxLength) {
        val trimmedText = originalText.subSequence(0, maxLength)
        val spannableStringBuilder = SpannableStringBuilder(trimmedText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle click if needed
            }
        }
        spannableStringBuilder.append("...")
        spannableStringBuilder.setSpan(
            clickableSpan,
            maxLength,
            spannableStringBuilder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableStringBuilder
    }
}