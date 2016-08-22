package com.github.badoualy.telegram

object PropertyUtils {

    fun hexStringToBytes(string: String?): ByteArray {
        if (string == null) return byteArrayOf();
        val len = string.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(string[i], 16) shl 4)
                    + Character.digit(string[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun bytesToHexString(bytes: ByteArray?): String {
        if (bytes == null) return "";
        val builder = StringBuilder()
        for (b in bytes) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }
}