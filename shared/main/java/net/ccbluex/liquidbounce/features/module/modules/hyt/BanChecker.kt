package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.minecraft.network.play.server.SPacketChat
import java.util.regex.Pattern

@ModuleInfo(name = "HytBanChecker", description = "Hyt", category = ModuleCategory.HYT)
class BanChecker : Module(){

    var ban = 0
    @EventTarget
    fun onPacket(event : PacketEvent){
        val packet = event.packet.unwrap()
        if(packet is SPacketChat){
            val matcher = Pattern.compile("玩家(.*?)在本局游戏中行为异常").matcher(packet.chatComponent.unformattedText)
            if(matcher.find()){
                ban ++
                val banname = matcher.group(1)
                LiquidBounce.hud.addNotification(Notification("BanChecker","$banname was banned. (banned:$ban)",NotifyType.INFO, animeTime = 1000))
                mc.thePlayer!!.sendChatMessage("@ $banname  主播你怎么ban了啊 这都ban了" + ban + "个人了 ")
            }
        }
    }
}