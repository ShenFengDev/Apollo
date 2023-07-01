/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.minecraft.network.play.client.CPacketClientStatus
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketKeepAlive


@ModuleInfo(name = "BlinkHelper", description = "Blink Reborn In HYT By WaWa",category = ModuleCategory.MISC)
class BlinkHelper : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if ((packet is CPacketKeepAlive || packet is CPacketClientStatus) && !(mc.thePlayer!!.isDead || mc.thePlayer!!.health <= 0)
        ) {
            event.cancelEvent()
        }
        if (packet is CPacketConfirmTransaction){
            event.cancelEvent()
        }
    }

    override fun onDisable() {
        val blink = Blink()
        super.onDisable()
    }
}
