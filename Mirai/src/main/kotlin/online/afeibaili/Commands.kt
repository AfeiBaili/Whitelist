package online.afeibaili

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import online.afeibaili.MessageParser.send
import online.afeibaili.MessageParser.sendMessage
import online.afeibaili.Whitelist.config
import online.afeibaili.data.DataRepository
import online.afeibaili.data.DataRepository.log
import online.afeibaili.data.PlayerData
import online.afeibaili.data.PlayerInfo
import online.afeibaili.net.PlayerNetUtil
import java.io.InputStream
import java.net.URI


/**
 * # 命令类
 *
 * @author AfeiBaili
 * @version 2026/9/5 17:17
 */

object Commands {
    val commandMap = mutableMapOf<String, suspend (Array<String>, GroupMessageEvent) -> Unit>()
    val heloFunction: suspend (Array<String>, GroupMessageEvent) -> Unit =
        { params: Array<String>, event: GroupMessageEvent ->
            val message: String = commandMap.keys.joinToString("\n")
            val helpStr: String = buildString {
                appendLine("可用命令为: ")
                append(message)
            }
            sendMessage(helpStr, event)
        }

    init {
        commandMap["绑定账号"] = f@{ params, event ->
            if (params.size != 1) "绑定账号 <你的游戏ID>" send event
            val playerName: String = params[0]
            val playerInfo: PlayerInfo = runCatching {
                PlayerNetUtil.getInfo(playerName)
            }.getOrElse { return@f "无法连接网络，或找不到玩家信息: ${it.message}" send event }
            val playerQQ = event.sender.id

            runCatching {
                val image: Image = getImageMessage(playerInfo.data.player.avatar, event.subject)
                val messages: MessageChain = buildMessageChain {
                    +PlainText("已申请绑定白名单，等待管理员通过\n")
                    +PlainText("玩家信息: ${playerInfo.data.player.username}\n")
                    +PlainText("玩家QQ: $playerQQ")
                    +image
                    +PlainText("\n")
                    +PlainText("请管理员审核: \n")
                    for (id in config.admin) +At(id)
                }

                runCatching {
                    log("request bind", "user${playerQQ}, nick: ${event.sender.nick}, request bind mc name $playerName")
                    DataRepository.saveVerify(
                        PlayerData(
                            playerQQ,
                            playerInfo.data.player.avatar,
                            playerInfo.data.player.id,
                            playerInfo.data.player.username
                        )
                    )
                }.getOrElse { return@f "申请已存在: ${it.message}" send event }

                return@f sendMessage(messages, event)
            }.onFailure {
                it.printStackTrace()
                return@f "无法获取头像信息, 可能玩家不存在" send event
            }
        }
        commandMap["解除绑定"] = f@{ params, event ->
            val at: At? = event.message.find { it is At } as? At
            if (at == null) return@f "请at白名单账号以解除绑定" send event
            if (!(config.admin.contains(event.sender.id) || event.sender.id == at.target)) return@f "无法解除其他人绑定" send event
            val data: PlayerData = runCatching {
                DataRepository.deleteWhitelist(at.target)
            }.getOrElse { return@f "不在白名单中" send event }
            log("cancel bind", "operator ${at}, canceled data: $data")
            "${data.mcName}已移除白名单" send event
        }
        commandMap["批准申请"] = f@{ params, event ->
            if (!config.admin.contains(event.sender.id)) return@f "您不是审核员" send event
            val at: At? = event.message.find { it is At } as? At
            if (at == null) return@f "请at申请方以同意" send event
            val data: PlayerData = runCatching {
                DataRepository.saveWhitelist(at.target)
            }.getOrElse { return@f "申请可能不存在: ${it.message}" send event }
            log("allow request", "operator ${event.sender.id}, requestor data: $data")
            "${data.mcName}已加入白名单" send event
        }
        commandMap["拒绝申请"] = f@{ params, event ->
            if (!config.admin.contains(event.sender.id)) return@f "您不是审核员" send event
            val at: At? = event.message.find { it is At } as? At
            if (at == null) return@f "请at申请方以拒绝" send event
            val data: PlayerData? = runCatching {
                DataRepository.deleteVerify(at.target)
            }.getOrElse { null }
            log("refused request", "operator ${event.sender.id}, requestor data: $data")
            "已拒绝${data?.mcName}加入白名单" send event
        }
        commandMap["白名单列表"] = { params, event ->
            DataRepository.getWhitelistData().toMessage(event) send event
        }
        commandMap["申请列表"] = { params, event ->
            DataRepository.getVerifyListData().toMessage(event) send event
        }
        commandMap["菜单"] = heloFunction
        commandMap["help"] = heloFunction
    }
}

fun String.ln() = this + "\n"

suspend fun List<PlayerData>.toMessage(event: GroupMessageEvent): Message {
    val builder = MessageChainBuilder()

    this.forEach {
        val nick: String = event.group.members[it.id]?.nick ?: "未知昵称"
        // val image: Image = getImageMessage(it.avatar, event.subject)
        val messages: MessageChain = buildMessageChain {
            +PlainText("账号: ${it.mcName}".ln())
            +PlainText("昵称: $nick".ln())
            +PlainText("QQ: ${it.id}".ln())
            // +image
            +PlainText("UUID: ${it.mcId}".ln())
            +PlainText("申请时间: ${it.createTime}".ln())
            if (it.verifyTime != 0L) +PlainText("加入时间: ${it.verifyTime}".ln())
            +PlainText("".ln())
        }
        builder.add(messages)
    }
    return builder.build()
}

suspend fun getImageMessage(uri: String, contact: Contact): Image {
    val url = URI(uri).toURL()
    val stream: InputStream = url.openConnection().inputStream
    return stream.toExternalResource().uploadAsImage(contact)
}