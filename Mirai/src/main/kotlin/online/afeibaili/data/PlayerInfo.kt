package online.afeibaili.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


/**
 * # 玩家信息数据
 *
 * @author AfeiBaili
 * @version 2026/9/5 01:27
 */

@JsonIgnoreProperties(ignoreUnknown = true)
class PlayerInfo {
    val code = ""
    val message = ""
    val data = Data()
    val success = false

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Data {
        val player = Player()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Player {
        val username = ""
        val id = ""

        @JsonProperty(value = "raw_id")
        val rawId = ""
        val avatar = ""
    }
}