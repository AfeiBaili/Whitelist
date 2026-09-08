package online.afeibaili

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import online.afeibaili.config.Config
import online.afeibaili.config.ConfigLoader


/**
 * # 插件入口
 *
 * @author AfeiBaili
 * @version 2026/9/4 20:43
 */

object Whitelist : KotlinPlugin(
    JvmPluginDescription(
        id = "online.afeibaili.whitelist",
        name = "Minecraft-Whitelist",
        version = "1.0.0",
    ) {
        author("AfeiBaili")
    }) {

    val config: Config by lazy { ConfigLoader.getConfig() }

    override fun onEnable() {
        loadConfig()
        loadListener()
        logger.info("Minecraft白名单插件启用")
    }

    fun loadListener() {
        MessageListener.load()
    }

    fun loadConfig() {
        config.active()
    }
}