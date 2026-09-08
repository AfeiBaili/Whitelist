package online.afeibaili

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import online.afeibaili.Commands.commandMap
import online.afeibaili.Whitelist.config


/**
 * # 消息解析器
 *
 * @author AfeiBaili
 * @version 2026/9/4 22:06
 */

object MessageParser {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun parse(msg: String, event: GroupMessageEvent) {
        if (!msg.startsWith(config.commandStart)) return

        val split: List<String> = msg.split("\\s".toRegex())
        val commandString = split.first().removePrefix(config.commandStart)
        val function: (suspend (Array<String>, GroupMessageEvent) -> Unit)? = commandMap[commandString]
        if (function == null) return
        scope.launch { function(split.drop(1).toTypedArray(), event) }
    }

    fun sendMessage(msg: String, event: GroupMessageEvent) {
        scope.launch { event.subject.sendMessage(msg) }
    }

    fun sendMessage(msg: Message, event: GroupMessageEvent) {
        scope.launch { event.subject.sendMessage(msg) }
    }

    infix fun String.send(event: GroupMessageEvent) {
        sendMessage(this, event)
    }

    infix fun Message.send(event: GroupMessageEvent) {
        sendMessage(this, event)
    }
}