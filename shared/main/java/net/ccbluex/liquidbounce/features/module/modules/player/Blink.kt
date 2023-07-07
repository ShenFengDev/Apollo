/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketConfirmTransaction
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "Blink", description = "帅哥blink", category = ModuleCategory.PLAYER)
class Blink : Module() {
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private var disableLogger = false
    private val pulseValue = BoolValue("Pulse", false)
    private val pulseDelayValue = IntegerValue("PulseDelay", 1000, 500, 5000)
    private val hytValue = BoolValue("Hyt",true)


    private val pulseTimer = MSTimer()
    override fun onEnable() {
        if (mc.thePlayer == null) return
        pulseTimer.reset()
    }

    override fun onDisable() {
        if (mc.thePlayer == null) return
        blink()
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (mc.thePlayer == null || disableLogger) return
        if (packet is CPacketPlayer ||(packet is CPacketPlayerTryUseItemOnBlock&& classProvider.isItemSword(mc.thePlayer!!.heldItem))) // Cancel all movement stuff
            event.cancelEvent()
        if (packet is Position || packet is PositionRotation ||
            packet is CPacketPlayerTryUseItemOnBlock ||
            packet is CPacketAnimation ||
            packet is CPacketEntityAction || packet is CPacketUseEntity || (packet::class.java.simpleName.startsWith("C", true) && hytValue.get())
        ) {
            event.cancelEvent()
            packets.add(packet)
        }
        if (packet is CPacketConfirmTransaction && hytValue.get()) {
            event.cancelEvent()

                packets.add(packet)

        }

    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (pulseValue.get() && pulseTimer.hasTimePassed(pulseDelayValue.get().toLong())) {
            blink()
            pulseTimer.reset()
        }
    }

    override val tag: String
        get() = packets.size.toString()

    private fun blink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc2.connection!!.networkManager.sendPacket(packets.take())
            }

            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
    }
}