package com.github.badoualy.telegram

import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.mtproto.auth.AuthKey
import com.github.badoualy.telegram.mtproto.model.DataCenter
import com.github.badoualy.telegram.mtproto.model.MTSession
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

data class AuthData(var phone: String, var phoneHash: String, var code: String);

class FileApiStorage(title: String = "default") : TelegramApiStorage {

    val folder: Path;
    val AUTH_FILE = "auth.properties"

    init {
        folder = Paths.get("telegram", title)
        Files.createDirectories(folder)
    }

    override fun saveAuthKey(authKey: AuthKey) {
        val a = loadAuth() ?: TelegramAuth()
        a.key = authKey.key
        saveAuth(a)
    }

    override fun loadAuthKey(): AuthKey? {
        try {
            val a = loadAuth() ?: return null
            return AuthKey(a.key!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun deleteAuthKey() {
        deleteDc()
    }

    override fun saveDc(dataCenter: DataCenter) {
        val a = loadAuth() ?: TelegramAuth()
        a.dcIp = dataCenter.ip
        a.dcPort = dataCenter.port
        saveAuth(a)
    }

    override fun loadDc(): DataCenter? {
        try {
            val a = loadAuth() ?: return null
            return DataCenter(a.dcIp!!, a.dcPort!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun deleteDc() {
        try {
            Files.deleteIfExists(folder.resolve(AUTH_FILE))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun saveSession(session: MTSession?) {
    }

    override fun loadSession(): MTSession? {
        return null
    }

    fun setCodeReceived() {
        val a = loadAuth() ?: TelegramAuth()
        a.authenticated = true
        saveAuth(a)
    }

    fun isCodeReceived(): Boolean {
        val a = loadAuth() ?: return false
        return a.authenticated
    }

    fun saveAuth(auth: TelegramAuth) {
        val properties = Properties()
        properties.setProperty("key", PropertyUtils.bytesToHexString(auth.key))
        properties.setProperty("dcIp", auth.dcIp ?: "")
        properties.setProperty("dcPort", if (auth.dcPort == null) "" else auth.dcPort.toString())
        properties.setProperty("authenticated", auth.authenticated.toString())

        properties.store(Files.newOutputStream(folder.resolve(AUTH_FILE)), "telegram auth file")
    }

    fun loadAuth(): TelegramAuth? {
        try {
            val properties = Properties()
            properties.load(Files.newInputStream(folder.resolve(AUTH_FILE)))

            return TelegramAuth(
                    PropertyUtils.hexStringToBytes(properties.getProperty("key")),
                    properties.getProperty("dcIp"),
                    if (properties.getProperty("dcPort") != null && properties.getProperty("dcPort").length > 0)
                        properties.getProperty("dcPort").toInt()
                    else null,
                    properties.getProperty("authenticated").toBoolean());
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}