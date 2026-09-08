package online.afeibaili.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import online.afeibaili.Whitelist.config
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * # 数据读写管理
 *
 * @author AfeiBaili
 * @version 2026/9/4 21:04
 */

object DataRepository {
    val conf = config
    val dataDir = conf.whitelistDataDir
    val verifyDir = conf.whitelistVerifyDir
    val whitelist = conf.whitelistPath
    val jsonMap = ObjectMapper().registerKotlinModule()
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val logPrinter = PrintWriter(FileWriter(conf.operationLog), true)

    init {
        val dataFile: File = dataDir.toFile()
        dataFile.also { if (!it.exists()) dataFile.mkdirs() }
        val verifyFile: File = verifyDir.toFile()
        verifyFile.also { if (!it.exists()) verifyFile.mkdirs() }
    }

    /**
     * ## 发起申请
     * @exception IllegalArgumentException 如果申请已存在
     */
    fun saveVerify(verifyData: PlayerData) = synchronized(this) {
        verifyData.createTime = System.currentTimeMillis()
        val file = File(verifyDir, verifyData.id.toString())
        val dataFile = File(dataDir, verifyData.toString())
        if (dataFile.exists()) throw IllegalArgumentException("已绑定账号, 请解绑")
        if (file.exists()) throw IllegalArgumentException("申请已存在")
        file.writeText(jsonMap.writerWithDefaultPrettyPrinter().writeValueAsString(verifyData))
        log("request", verifyData)
    }


    /**
     * ## 拒绝申请
     */
    fun deleteVerify(id: Long): PlayerData = synchronized(this) {
        val file = File(verifyDir, id.toString())
        if (!file.exists()) throw IllegalArgumentException("申请不存在")
        val json: String = file.readText()
        file.delete()
        return jsonMap.readValue(json, PlayerData::class.java).also {
            log("refused", it)
        }
    }


    /**
     * ## 获取申请数据
     * @exception IllegalArgumentException 如果申请不存在
     */
    fun getVerifyFile(id: Long): File = synchronized(this) {
        val file = File(verifyDir, id.toString())
        if (!file.exists()) throw IllegalArgumentException("申请不存在")
        return file
    }


    /**
     * ## 加入白名单
     * @exception RuntimeException 如果解析数据失败
     */
    fun saveWhitelist(id: Long): PlayerData = synchronized(this) {
        val file: File = getVerifyFile(id)
        val data: PlayerData = jsonMap.readValue(file, PlayerData::class.java)
        data.verifyTime = System.currentTimeMillis()
        file.delete()
        val dataFile = File(dataDir, id.toString())
        dataFile.writeText(jsonMap.writerWithDefaultPrettyPrinter().writeValueAsString(data))
        saveWhitelistFile()
        log("join list", data)
        return data
    }


    /**
     * ## 移除白名单
     * @exception IllegalArgumentException 如果白名单不存在
     */
    fun deleteWhitelist(id: Long): PlayerData = synchronized(this) {
        val file = File(dataDir, id.toString())
        if (!file.exists()) throw IllegalArgumentException("白名单不存在")
        val json: String = file.readText()
        file.delete()
        saveWhitelistFile()
        return jsonMap.readValue(json, PlayerData::class.java).also {
            log("remove list", it)
        }
    }

    fun saveWhitelistFile() {
        val whitelistFile: List<File>? = getWhitelistFile()
        val list: String = whitelistFile?.joinToString("\n") { it.name } ?: ""
        whitelist.toFile().writeText(list)
    }

    fun getWhitelistData(): List<PlayerData> {
        val files: List<File>? = getWhitelistFile()
        if (files == null) return listOf()
        return files.toPlayerList()
    }

    fun getVerifyListData(): List<PlayerData> {
        val files: List<File>? = getVerifyListFile()
        if (files == null) return listOf()
        return files.toPlayerList()
    }

    fun getWhitelistFile(): List<File>? = dataDir.toFile().listFiles()?.toList()
    fun getVerifyListFile(): List<File>? = verifyDir.toFile().listFiles()?.toList()

    fun String.toFile() = File(this)
    fun List<File>.toPlayerList(): List<PlayerData> {
        return this.mapNotNull {
            runCatching {
                jsonMap.readValue(it, PlayerData::class.java)
            }.getOrNull()
        }
    }

    fun log(op: String, message: Any) {
        logPrinter.println("[${op}] ${getDateTime()} /: $message")
    }

    fun getDateTime(): String = LocalDateTime.now().format(formatter)
}