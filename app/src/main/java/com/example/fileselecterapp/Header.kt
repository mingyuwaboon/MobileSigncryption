package com.example.fileselecterapp

class Header {

    companion object {
        var COUNTHID = 0
    }

    internal fun encapsulation(Ciphertext: ByteArray,Key: ByteArray): ByteArray? {
        val KeySize = Key.size
        val splitIndex = KeySize / 2
        val FrontKey = Key.copyOfRange(0, splitIndex)
        val TailKey = Key.copyOfRange(splitIndex, Key.size)

        val ConcatCipherKey = FrontKey + Ciphertext + TailKey

        val Header = GenerateHeader(COUNTHID,KeySize)

        if(COUNTHID < 255){
            COUNTHID += 1
        } else {
            COUNTHID = 0
        }

        val SendData = Header?.plus(ConcatCipherKey)

        return SendData
    }

    internal fun GenerateHeader(FID: Int,KeySize: Int): ByteArray? {
        val StrHeader = PaddingToString(FID,KeySize)

        return StrHeader.toByteArray()
    }

    fun PaddingToString(ID: Int,KeySize: Int): String {

        var IDstr = ID.toString(16)
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

        return  ConcatHeader

    }
}