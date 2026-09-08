package online.afeibaili.net

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import online.afeibaili.data.PlayerInfo
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


/**
 * # 获取玩家信息
 *
 * @author AfeiBaili
 * @version 2026/9/5 01:37
 */

object PlayerNetUtil {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val client = HttpClient.newHttpClient()
    private val jsonMap = ObjectMapper().registerKotlinModule()

    fun getInfo(playerName: String): PlayerInfo {
        val request: HttpRequest =
            HttpRequest.newBuilder().uri(URI("https://playerdb.co/api/player/minecraft/$playerName")).GET().build()
        val body = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        return jsonMap.readValue(body, PlayerInfo::class.java)
    }
}