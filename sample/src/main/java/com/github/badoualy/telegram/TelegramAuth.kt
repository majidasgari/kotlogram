package com.github.badoualy.telegram

data class TelegramAuth(var key: ByteArray? = null,
                        var dcIp: String? = null,
                        var dcPort: Int? = null,
                        var authenticated: Boolean = false)