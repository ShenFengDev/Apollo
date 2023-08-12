package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.network.Packet
import net.minecraft.network.ThreadQuickExitException
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.client.CPacketConfirmTeleport
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketChat
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketDisconnect
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketPlayerPosLook
import java.util.*

/**
 *  基于XiChenQi的代码重写的GrimFull 可以在1.12站着被打
 *  不知道为什么会报post，处理数据包的方式应该是合法的
 */

/**
 * skid by XiChenQi
 * Thanks kid(qwa)、FDPClient
 * 解决大部分 不显示 不会拉的问题，并且整合了VelocityBlink
 */

@ModuleInfo(name = "GrimVelocity", description = "GrimAC 2.3.43 release -", category = ModuleCategory.COMBAT)
class GrimVelocity : Module() {
    private var waitC03 = false
    private var canProcessNext = true
    private var nextIsTeleport = false
    private var lastReceivedTransaction : SPacketConfirmTransaction ?= null
    private val inBus = LinkedList<Packet<INetHandlerPlayClient>>()

    override fun onEnable(){
        waitC03 = false
        canProcessNext = true
        nextIsTeleport = false
        lastReceivedTransaction = null
    }
    override fun onDisable(){
        closeBlink(false)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val pw = event.packet.unwrap()
        if (pw is SPacketChat) {
            return
        } else if (pw is SPacketEntityVelocity && pw.entityID == mc.thePlayer?.entityId) {
            event.cancelEvent()
            inBus.add(pw)
            waitC03 = true
        } else if (pw is SPacketDisconnect && waitC03) {
            inBus.clear()
            waitC03 = false
        } else if (pw is SPacketPlayerPosLook && waitC03) {
            event.cancelEvent()
            inBus.add(pw)
            if (processTeleport(false)) waitC03 = false
            canProcessNext = true
            lastReceivedTransaction = null
        } else if (pw::class.java.getSimpleName().startsWith("S", true) && waitC03) {
            event.cancelEvent()
            try {
                inBus.add(pw as Packet<INetHandlerPlayClient>) // md这个地方怎么转类型都报warn气死我了
            } catch (e: Exception) {
                e.printStackTrace()
                inBus.clear()
                waitC03 = false
            }
        } else if ((pw is CPacketPlayer.Position || pw is CPacketPlayer.PositionRotation) && !nextIsTeleport && canProcessNext && waitC03) {
            if (processVelocity(false)) waitC03 = false
            if (lastReceivedTransaction != null) canProcessNext = false
        } else if (pw is CPacketConfirmTransaction) {
            val _lastReceivedTransaction = lastReceivedTransaction
            if (_lastReceivedTransaction != null && pw.windowId == _lastReceivedTransaction.windowId && pw.uid == _lastReceivedTransaction.actionNumber) {
                canProcessNext = true
                lastReceivedTransaction = null
            }
        }
        if (pw is CPacketConfirmTeleport) {
            nextIsTeleport = true
        } else if (pw::class.java.getSimpleName().startsWith("C", true) && nextIsTeleport) {
            nextIsTeleport = false
        }
    }

    private fun closeBlink(instant: Boolean) {
        if (instant) {
            while (!inBus.isEmpty()) {
                try {
                    val packetIn = inBus.poll() ?: continue
                    packetIn.processPacket(mc2?.connection ?: continue)
                } catch (e: ThreadQuickExitException) {
                    continue
                }
            }
        } else {
            while (!inBus.isEmpty()) {
                val packetIn = inBus.poll() ?: continue
                val scheduler = mc2 ?: continue
                val processor = scheduler.connection ?: continue
                scheduler.addScheduledTask(Runnable()
                {
                    packetIn.processPacket(processor)
                })
            }
        }
    }

    private fun processTeleport(instant: Boolean):Boolean {
        if (instant) {
            while (!inBus.isEmpty()) {
                try {
                    val packetIn = inBus.poll() ?: continue
                    //logTransaction(packetIn)
                    packetIn.processPacket(mc2?.connection ?: continue)
                    if (packetIn is SPacketPlayerPosLook) break
                } catch (e: ThreadQuickExitException) {
                    continue
                }
            }
        } else {
            while (!inBus.isEmpty()) {
                val packetIn = inBus.poll() ?: continue
                val scheduler = mc2 ?: continue
                val processor = scheduler.connection ?: continue
                //logTransaction(packetIn)
                scheduler.addScheduledTask(Runnable()
                {
                    packetIn.processPacket(processor)
                })
                if (packetIn is SPacketPlayerPosLook) break
            }
        }
        return inBus.isEmpty()
    }

    private fun processVelocity(instant: Boolean):Boolean {
        if (instant) {
            while (!inBus.isEmpty()) {
                try {
                    val packetIn = inBus.poll() ?: continue
                    if (packetIn is SPacketEntityVelocity && packetIn.entityID == mc.thePlayer?.entityId) break
                    logTransaction(packetIn)
                    packetIn.processPacket(mc2?.connection ?: continue)
                } catch (e: ThreadQuickExitException) {
                    continue
                }
            }
        } else {
            while (!inBus.isEmpty()) {
                val packetIn = inBus.poll() ?: continue
                if (packetIn is SPacketEntityVelocity && packetIn.entityID == mc.thePlayer?.entityId) break
                val scheduler = mc2 ?: continue
                val processor = scheduler.connection ?: continue
                logTransaction(packetIn)
                scheduler.addScheduledTask(Runnable()
                {
                    packetIn.processPacket(processor)
                })
            }
        }
        return inBus.isEmpty()
    }

    private fun logTransaction(packetIn: Packet<INetHandlerPlayClient>) {
        if (packetIn is SPacketConfirmTransaction) {
            lastReceivedTransaction = packetIn
        }
    }
}