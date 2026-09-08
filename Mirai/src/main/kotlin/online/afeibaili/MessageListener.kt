package online.afeibaili

import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import online.afeibaili.Whitelist.config


/**
 * # 消息监听器
 *
 * @author AfeiBaili
 * @version 2026/9/4 21:09
 */

object MessageListener {
    val groups get() = config.groups

    fun load() {
        GlobalEventChannel.filter { e ->
            if (e !is GroupMessageEvent) return@filter false
            groups.forEach { if (e.group.id == it) return@filter true }
            false
        }.subscribeAlways<GroupMessageEvent> { event ->
            MessageParser.parse(event.message.contentToString(), event)
        }
    }
}