package net.ccbluex.liquidbounce.features.module.modules.combat

import me.utils.PacketUtils
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.*
import sun.audio.AudioPlayer.player
import kotlin.math.sqrt

@ModuleInfo(name = "GrimVelocity",description = "Build By Robin",category = ModuleCategory.COMBAT)
class GrimVelocity:Module() {
    var canCancel = false
    val onlyGround = BoolValue("OnlyGround",true)
    val onlyHurt = BoolValue("OnlyHurt",false)

    override fun onEnable() {
        canCancel = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val player = mc.thePlayer!!
        val packet = event.packet
        val packetEntityVelocity = packet.asSPacketEntityVelocity()
        if ((onlyGround.get() && !mc2.player.onGround) || (onlyHurt.get() && mc2.player.hurtTime == 0) || mc2.player.isDead || mc2.player.isInWater)
            return

        if (classProvider.isSPacketEntityVelocity(packet)) {
            if (mc2.world.getEntityByID(packetEntityVelocity.entityID) != mc.thePlayer)
                return

            event.cancelEvent()
            packetEntityVelocity.motionX = 0
            packetEntityVelocity.motionY = 0
            packetEntityVelocity.motionZ = 0
            mc2.connection!!.sendPacket(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.START_SNEAKING))
            PacketUtils.sendPacketNoEvent(
                CPacketPlayer.PositionRotation(
                    player.posX, player.posY, player.posZ,
                    player.rotationYaw, player.rotationPitch, player.onGround
                )
            )
            canCancel = true
        }else if (canCancel){
            mc2.connection!!.sendPacket(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.STOP_SNEAKING))
        }

        if (packet is SPacketPlayerPosLook){
            val x = packet.x - mc.thePlayer?.posX!!
            val y = packet.y - mc.thePlayer?.posY!!
            val z = packet.z - mc.thePlayer?.posZ!!
            val diff = sqrt(x * x + y * y + z * z)
            if (diff <= 10) {
                PacketUtils.sendPacketNoEvent(
                    CPacketPlayer.PositionRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        player.rotationPitch,
                        player.onGround
                    )
                )
            }
        }

                if (classProvider.isSPacketPlayerPosLook(event.packet) && canCancel) {
                    val packet = event.packet.asSPacketPosLook()
                    event.cancelEvent()
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerLook(packet.yaw,packet.pitch,mc.thePlayer!!.onGround))
                    canCancel = false
                }
                if (canCancel && packet is SPacketPlayerPosLook) {
                    event.cancelEvent()
                    PacketUtils.sendPacketNoEvent(
                        CPacketPlayer.PositionRotation(
                            packet.x,
                            packet.y,
                            packet.z,
                            packet.getYaw(),
                            packet.getPitch(),
                            true
                        )
                    )
                }
    }
}