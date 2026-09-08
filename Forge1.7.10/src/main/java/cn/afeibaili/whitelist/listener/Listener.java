package cn.afeibaili.whitelist.listener;

import cn.afeibaili.whitelist.Validator;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.UUID;

import static cn.afeibaili.whitelist.Whitelist.LOG;

/**
 * # 监听器
 *
 * @author AfeiBaili
 * @version 2026/9/5 03:03
 */

public class Listener {

    @SubscribeEvent
    public void init(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        if (event.handler instanceof NetHandlerPlayServer) {
            NetHandlerPlayServer handler = (NetHandlerPlayServer) event.handler;
            GameProfile profile = handler.playerEntity.getGameProfile();
            UUID id = profile.getId();
            String name = profile.getName();
            Validator.updateList();
            boolean inWhitelist = Validator.inWhitelist(id.toString());
            if (!inWhitelist) {
                LOG.info("玩家{}不在白名单中: {}", name, id.toString());
                handler.kickPlayerFromServer(name + "\n您不在白名单内, 请在群聊中绑定正版账号");
            }
            LOG.info("白名单玩家进入: {}", name);
        }
    }
}
