package com.example.fileselecterapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CloudCallback  {

    private val FILE_PICKER_REQUEST_CODE = 1

    private lateinit var CTview: TextView
    private lateinit var Signview: TextView
    private lateinit var DATASEND: ByteArray
    private var startTime: Long = 0
    private var responseData: String =""
    private var dataSent = false
    private var COUNT = 0



    private val KeyBoss =  CryptoManager()
    val cloudservice = Cloud()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CTview = findViewById(R.id.CTMCCtext)
        Signview = findViewById(R.id.SignMCCtext)

        val chooseFileButton = findViewById<Button>(R.id.SignMCCbutton)
        chooseFileButton.setOnClickListener {
            openFilePicker()
        }

    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val checkcount = data?.clipData?.itemCount
            if (data?.clipData != null /*&& checkcount!! > 1*/) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    // Process each selected file URI as needed
                    uri?.let {
                        val fileContent = FileHelper.uriToByteArray(this, it)

                        //OTP
                        val key = KeyBoss.generateRandomKey(fileContent!!.size)
                        startTime = System.nanoTime()
                        val encryptedContent = KeyBoss.oneTimePadEncrypt(fileContent, key)
                        val decryptedContent = KeyBoss.oneTimePadDecrypt(encryptedContent!!, key)

                        val CiKeyDATA = Header().GenerateDATA(encryptedContent, key)
                        DATASEND = CiKeyDATA

                        val Head = Header().GenHeader(key.size)
                        //First data sending

                        lifecycleScope.launch {
                            //val data = "{\"Head\": \"$Head\", \"Data\": \"$CiKeyDATA\"}"
                            //val data = "{\"Head\": \"THIS IS HEADER\", \"Data\": \"THIS IS KEY\"}"
                            //val data = "123456789001122"
                            val data = "0" + Head + CiKeyDATA.contentToString()
                            //val dataJSON = JSONObject(data)
                            val url =
                                "https://asia-southeast1-neat-veld-421803.cloudfunctions.net/function-1"
                            cloudservice.sendDataAndWaitForResponse(data, url, this@MainActivity)

                            //Log.d("CheckParallel","THe number is $COUNT")
                            COUNT+=1
                        }
                    }
                }
            } else if (data?.data != null ) {
                // If only one file is selected, process the single file URI
                startTime = System.nanoTime()
                val uri = data.data
                uri.let {
                    val fileContent = it?.let { it1 -> FileHelper.uriToByteArray(this, it1) }

                    val key = KeyBoss.generateRandomKey(fileContent!!.size)

                    val encryptedContent = KeyBoss.oneTimePadEncrypt(fileContent, key)
                    val decryptedContent = KeyBoss.oneTimePadDecrypt(encryptedContent!!, key)

                    val CiKeyDATA = Header().GenerateDATA(encryptedContent, key)
                    DATASEND = CiKeyDATA

                    val Head = Header().GenHeader(key.size)

                    lifecycleScope.launch {
                        //val data = "{\"Head\": \"$Head\", \"Data\": \"$CiKeyDATA\"}"
                        //val data = "{\"Head\": \"THIS IS HEADER\", \"Data\": \"THIS IS KEY\"}"
                        //val data = "123456789001122"
                        val data = "0" + Head + CiKeyDATA.contentToString()
                        //val dataJSON = JSONObject(data)
                        val url =
                            "https://asia-southeast1-neat-veld-421803.cloudfunctions.net/function-1"
                        cloudservice.sendDataAndWaitForResponse(data, url, this@MainActivity)

                        //Log.d("CheckParallel","THe number is $COUNT")
                        COUNT+=1
                    }
                }
            }
        }
        /*
                // Handle selected file URI
            val selectedFileUri = data?.data

            // Evaluate test
            val time = Evaluate().measureTimeMillis {



                /*
                // Use the selected file URI as needed
                selectedFileUri?.let {

                    //Read content to ByteArray
                    val fileContent = FileHelper.uriToByteArray(this, it)

                    //OTP
                    val key = KeyBoss.generateRandomKey(fileContent!!.size)

                    val encryptedContent = KeyBoss.oneTimePadEncrypt(fileContent, key)
                    val decryptedContent = KeyBoss.oneTimePadDecrypt(encryptedContent!!, key)

                    val CiKeyDATA = Header().GenerateDATA(encryptedContent, key)
                    DATASEND = CiKeyDATA

                    /*
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
                    */


                    val Head = Header().GenHeader(key.size)
                    //First data sending

                    lifecycleScope.launch {
                        //val data = "{\"Head\": \"$Head\", \"Data\": \"$CiKeyDATA\"}"
                        //val data = "{\"Head\": \"THIS IS HEADER\", \"Data\": \"THIS IS KEY\"}"
                        //val data = "123456789001122"
                        val data = "0"+Head + CiKeyDATA.contentToString()
                        //val dataJSON = JSONObject(data)
                        val url =
                            "https://asia-southeast1-neat-veld-421803.cloudfunctions.net/function-1"
                        cloudservice.sendDataAndWaitForResponse(data, url, this@MainActivity)
                    }


                    //Log.d("Check", "------Finish All process------")

                    //Sending Data to cloud
                    //val Send2Cloud = Header().encapsulation(encryptedContent,key)
                    //Toast.makeText(this@MainActivity,"$Send2Cloud",Toast.LENGTH_SHORT).show()


//                    Decryptfile.text = responseData //Decrypt
//                    fileview.text = it.toString() // Convert Uri to String
//                    MGview.text = MDigest.contentToString()  // MG
//                    Signview.text = Sign.contentToString() //Sign
//                    contentview.text = FinalData.contentToString() // Content in file
                    //Contextfile.text = FileHelper.readFileContent(this, it) // read to represent content
                }

            }
            */
            val test = "123456789"
            val gg = test.substring(4)
            val ge = test.substring(0,5)
            Log.d("Check","This is test0,5: $ge")
            Log.d("Check","This is test4: $gg")
            Log.d("Check","------The usage time is: $time ------")
        } */
    }

    override fun onResponse(response: String) {
        // Use the response here
        responseData = response

        Log.d("Check", response)
        //val jsonObject = JSONObject(response)
        //responseData = jsonObject.getString("key")
        if (!dataSent && responseData != "") {
            // Send data to the cloud again
            val HeadSign = responseData.substring(0, 12)
            val MG = responseData.substring(12).toByteArray()

            val alias_ecc = "my_ecc_key"
            val keyecc = KeyBoss.generateECCKeyPair(this, alias_ecc)
            val Sign = KeyBoss.signData(keyecc.private, MG)


            lifecycleScope.launch {
                val data = "1$HeadSign${Sign.contentToString()}"
                val url = "https://asia-southeast1-neat-veld-421803.cloudfunctions.net/function-1"
                cloudservice.sendDataAndWaitForResponse(data, url, this@MainActivity)
            }
            dataSent = true
            Signview.text = Sign.contentToString()//byteArrayToHex(Sign) //Sign
        }
        else {
            // dataSent = true then set response
            CTview.text = responseData//stringToHex(responseData)
            val EndTime = System.nanoTime()
            val elapsedTimeNano = EndTime - startTime
            Log.d("Check","\"------The usage time is: ${elapsedTimeNano / 1_000_000} ------")
        }
    }



}
fun stringToHex(input: String): String {
    val stringBuilder = StringBuilder()
    for (char in input) {
        val hex = Integer.toHexString(char.toInt())
        stringBuilder.append(hex)
    }
    return stringBuilder.toString()
}

fun concatenateByteArrays(array1: ByteArray, array2: ByteArray): ByteArray {
    return array1 + array2
}




/*
class MainActivity : AppCompatActivity(), CloudCallback  {

    val startTime = System.nanoTime()
    private val FILE_PICKER_REQUEST_CODE = 1
    private lateinit var fileview: TextView
    private lateinit var MGview: TextView
    private lateinit var Decryptfile: TextView
    private lateinit var Signview: TextView
    private lateinit var contentview: TextView
    private lateinit var DATASEND: ByteArray
    private var responseData: String =""
    private var dataSent = false



    private val KeyBoss =  CryptoManager()
    val cloudservice = Cloud()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fileview = findViewById(R.id.fileviewuri)
        MGview = findViewById(R.id.MGview)
        Decryptfile = findViewById(R.id.Decryptview)
        Signview = findViewById(R.id.SignView)
        contentview = findViewById(R.id.Contentview)

        val chooseFileButton = findViewById<Button>(R.id.selectbutton)
        chooseFileButton.setOnClickListener {
            openFilePicker()
        }

    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
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
                    val decryptedContent = KeyBoss.oneTimePadDecrypt(encryptedContent!!, key)

                    val CiKeyDATA = Header().GenerateDATA(encryptedContent, key)
                    DATASEND = CiKeyDATA

                    /*
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
                    */


                    val Head = Header().GenHeader(key.size)
                    //First data sending

                    lifecycleScope.launch {
                        //val data = "{\"Head\": \"$Head\", \"Data\": \"$CiKeyDATA\"}"
                        //val data = "{\"Head\": \"THIS IS HEADER\", \"Data\": \"THIS IS KEY\"}"
                        //val data = "123456789001122"
                        val data = "0"+Head + CiKeyDATA.contentToString()
                        //val dataJSON = JSONObject(data)
                        val url =
                            "https://asia-southeast1-neat-veld-421803.cloudfunctions.net/function-1"
                        cloudservice.sendDataAndWaitForResponse(data, url, this@MainActivity)
                    }


                    //Log.d("Check", "------Finish All process------")

                    //Sending Data to cloud
                    //val Send2Cloud = Header().encapsulation(encryptedContent,key)
                    //Toast.makeText(this@MainActivity,"$Send2Cloud",Toast.LENGTH_SHORT).show()


//                    Decryptfile.text = responseData //Decrypt
//                    fileview.text = it.toString() // Convert Uri to String
//                    MGview.text = MDigest.contentToString()  // MG
//                    Signview.text = Sign.contentToString() //Sign
//                    contentview.text = FinalData.contentToString() // Content in file
                    //Contextfile.text = FileHelper.readFileContent(this, it) // read to represent content
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

    override fun onResponse(response: String) {
        // Use the response here
        responseData = response

        Log.d("Response5556", response)
        //val jsonObject = JSONObject(response)
        //responseData = jsonObject.getString("key")
        if (!dataSent && responseData != "") {
            // Send data to the cloud again
            val HeadSign = responseData.substring(0, 12)
            val MG = responseData.substring(12).toByteArray()

            val alias_ecc = "my_ecc_key"
            val keyecc = KeyBoss.generateECCKeyPair(this, alias_ecc)
            val Sign = KeyBoss.signData(keyecc.private, MG)


            lifecycleScope.launch {
                val data = "1$HeadSign${Sign.contentToString()}"
                val url = "https://asia-southeast1-neat-veld-421803.cloudfunctions.net/function-1"
                cloudservice.sendDataAndWaitForResponse(data, url, this@MainActivity)
            }
            dataSent = true
            MGview.text = MG.contentToString()  // MG
            Signview.text = Sign.contentToString() //Sign
            Log.d("Check", "------Finish All process------")
        }
        else {
            // dataSent = true then set response
            Decryptfile.text = responseData //Decrypt
            //fileview.text = it.toString() // Convert Uri to String
            //contentview.text = responseData // Content in file
            val EndTime = System.nanoTime()
            val elapsedTimeNano = EndTime - startTime
            Log.d("check","\"------The usage time is: ${elapsedTimeNano / 1_000_000} ------")
        }
    }



}
*/
