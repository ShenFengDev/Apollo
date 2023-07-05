package net.ccbluex.liquidbounce.features.module.modules.combat

import me.utils.PacketUtils
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketClientStatus
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.*
import java.util.*
import kotlin.math.sqrt

@ModuleInfo(name = "GrimVelocity",description = "recode by Robin",category = ModuleCategory.COMBAT)
class GrimVelocity:Module() {
    val onlyGround = BoolValue("OnlyGround",true)
    val onlyHurt = BoolValue("OnlyHurt",false)




    private val inbus = LinkedList<Packet<INetHandlerPlayServer>>()
    private val outbus = LinkedList<Packet<INetHandlerPlayClient>>()
    private var runCancel = false
    private var runCancel2 = false




    @EventTarget
    fun onPacket(event: PacketEvent) {
        val player = mc.thePlayer!!
        val packet = event.packet
        val packetEntityVelocity = packet.asSPacketEntityVelocity()
        if ((onlyGround.get() && !mc.thePlayer!!.onGround) || (onlyHurt.get() && mc.thePlayer!!.hurtTime == 0) || mc.thePlayer!!.isDead || mc.thePlayer!!.isInWater) {
            return
        }

        if (classProvider.isSPacketEntityVelocity(packet)) {
            if (mc.theWorld!!.getEntityByID(packetEntityVelocity.entityID) != mc.thePlayer) {
                return
            }
            packetEntityVelocity.motionX = 0
            packetEntityVelocity.motionY = 0
            packetEntityVelocity.motionZ = 0
            event.cancelEvent()

            mc.thePlayer!!.sendQueue.addToSendQueue(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.START_SNEAKING) as IPacket)
            PacketUtils.sendPacketNoEvent(
                CPacketPlayer.PositionRotation(
                    player.posX, player.posY, player.posZ,
                    player.rotationYaw, player.rotationPitch, player.onGround
                )
            )
            mc.thePlayer!!.sendQueue.addToSendQueue(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.STOP_SNEAKING) as IPacket)
            runCancel = true
        }
        if (packet is SPacketPlayerPosLook &&runCancel){
            event.cancelEvent()
            runCancel2 = true
        }
        if (packet is SPacketPlayerPosLook && runCancel2){
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
        if(runCancel&&(packet is SPacketConfirmTransaction || packet is CPacketClientStatus || packet is CPacketConfirmTransaction)){
            event.cancelEvent()
            ClientUtils.displayChatMessage("CancelPacket")
            runCancel = false
        }
        if(!runCancel){
            inbus.add(SPacketConfirmTransaction() as Packet<INetHandlerPlayServer>)
            outbus.add(CPacketClientStatus() as Packet<INetHandlerPlayClient>)
            outbus.add(CPacketConfirmTransaction() as Packet<INetHandlerPlayClient>)
            ClientUtils.displayChatMessage("ReSendS32")
            return
        }
        runCancel = false
        runCancel2 = false



    }
    override fun onEnable() {
        runCancel = false
    }
}