package com.example.fileselecterapp

class Header {

    companion object {
        var COUNTHID = 0
    }

    //GenDATA
    fun GenerateDATA(Ciphertext: ByteArray, Key: ByteArray): ByteArray {
        val KeySize = Key.size
        val splitIndex = KeySize / 2
        val FrontKey = Key.copyOfRange(0, splitIndex)
        val TailKey = Key.copyOfRange(splitIndex, Key.size)

        val ConcatCipherKey = FrontKey + Ciphertext + TailKey

        return ConcatCipherKey
    }

    //ConvertStr2Byte
    internal fun HeaderStr2Byte(KeySize: Int): ByteArray? {
        val StrHeader = GenHeader(KeySize)

        return StrHeader.toByteArray()
    }

    //GenHeader
    fun GenHeader(KeySize: Int): String {

        var IDstr = COUNTHID.toString(16)
        var Keystr = KeySize.toString(16)

        val PadTimeID = 2 - IDstr.length
        val PadTimeKey = 8 - Keystr.length

        if(PadTimeID != 0) {
            IDstr = "0"+IDstr
        }

        if(PadTimeKey != 0) {
            Keystr = "0".repeat(PadTimeKey) + Keystr
        }

        val Hsize = "0C" // dec = 12
        val ConcatHeader = IDstr + Hsize + Keystr

        if(COUNTHID < 255){
            COUNTHID += 1
        } else {
            COUNTHID = 0
        }

        return  ConcatHeader

    }
}