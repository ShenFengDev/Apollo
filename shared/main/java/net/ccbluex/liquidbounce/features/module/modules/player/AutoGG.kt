package net.ccbluex.liquidbounce.features.module.modules.player

import me.sound.SoundPlayer
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.SPacketChat

@ModuleInfo(name = "AutoGG", category = ModuleCategory.PLAYER, description = "idk")
class AutoGG : Module() {
    private val textValue = TextValue("ClientName", "Apollo")
    private val msgValue = TextValue("Message","")
    var totalPlayed = 0

    val KillAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(mc.thePlayer == null) return
        val packet = event.packet.unwrap()

        if (packet is SPacketChat) {
            val text = packet.chatComponent.unformattedText


            if (text.contains("游戏开始", true) && !text.contains(":", true)) {
                totalPlayed++
                mc.thePlayer!!.sendChatMessage("@我正在使用"+textValue.get())
            }
            if (text.contains("      喜欢      一般      不喜欢", true)) {
                LiquidBounce.hud.addNotification(Notification(name,"Game Over", NotifyType.INFO))
                SoundPlayer().playSound(SoundPlayer.SoundType.VICTORY, LiquidBounce.moduleManager.toggleVolume);


                    mc.thePlayer!!.sendChatMessage("["+textValue.get()+"] GG  $msgValue")




                SoundPlayer().playSound(SoundPlayer.SoundType.VICTORY, LiquidBounce.moduleManager.toggleVolume);

                Recorder.totalPlayed++
            }else if (text.contains("你现在是观察者状态. 按E打开菜单.", true)) {
                LiquidBounce.hud.addNotification(Notification(name,"Game Over", NotifyType.INFO))

                    mc.thePlayer!!.sendChatMessage("["+textValue.get()+"] GG  $msgValue")


                //SoundPlayer().playSound(SoundPlayer.SoundType.VICTORY, LiquidBounce.moduleManager.toggleVolume);

                Recorder.totalPlayed++
            }else if (text.contains("[起床战争] Game 结束！感谢您的参与！", true)) {
                LiquidBounce.hud.addNotification(Notification(name,"Game Over", NotifyType.INFO))


                    mc.thePlayer!!.sendChatMessage("["+textValue.get()+"] GG  $msgValue")

                SoundPlayer().playSound(SoundPlayer.SoundType.VICTORY, LiquidBounce.moduleManager.toggleVolume);



                Recorder.totalPlayed++
            }
        }
    }

}