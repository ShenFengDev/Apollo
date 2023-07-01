package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import me.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketChat
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketEntity
import net.minecraft.network.play.server.SPacketEntity.S15PacketEntityRelMove
import net.minecraft.network.play.server.SPacketEntity.S16PacketEntityLook
import net.minecraft.network.play.server.SPacketEntity.S17PacketEntityLookMove
import net.minecraft.network.play.server.SPacketEntityAttach
import net.minecraft.network.play.server.SPacketEntityTeleport
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketPlayerPosLook
import java.util.*

@ModuleInfo(name = "GrimVelocity", description = "GrimFull", category = ModuleCategory.COMBAT)
class GrimVelocity : Module() {
    private val modeValue = ListValue("mode", arrayOf("SpoofS32","2","putPacket"),"1")
    private val cancelPacketValue = IntegerValue("GroundTicks",6,0,100)
    private val AirCancelPacketValue = IntegerValue("AirTicks",6,0,100)
    private val OnlyGround = BoolValue("OnlyGround",false)
    private val OnlyMove = BoolValue("OnlyMove",false)
    private val noMove = BoolValue("noMove",false)
    private val bestnomove = BoolValue("bestnomove",false)

    private val TestNoMove = BoolValue("TestNoMove",true)
    private val CancelS12 = BoolValue("CancelS12",true)
    private val TestValue = BoolValue("Test",false)
    private val TestValue1 = IntegerValue("TestValue",4,0,100)
    private val Safe = BoolValue("SafeMode",false)
    private val AutoDisable = ListValue("AutoDisable", arrayOf("Normal","Silent"), "Silent")
    private val SilentTicks = IntegerValue("AutoDisableSilentTicks",4,0,100)
    private val DeBug = BoolValue("Debug",false)
    private var resetPersec = 8
    private var grimTCancel = 0
    private var updates = 0
    private var S08 = 0
    private val inBus = LinkedList<Packet<INetHandlerPlayClient>>()
    private val outBus = LinkedList<Packet<INetHandlerPlayServer>>()
    override fun onEnable() {
        inBus.clear()
        outBus.clear()
        grimTCancel = 0
        S08 = 0
    }
    override fun onDisable(){
        while (inBus.size > 0) {
            inBus.poll()?.processPacket(mc2.connection)
        }
        while (outBus.size > 0) {
            val upPacket = outBus.poll() ?: continue
            PacketUtils.sendPacketNoEvent(upPacket)
            if (DeBug.get()) {
                ClientUtils.displayChatMessage("S12 Cancelled")
            }
        }
        S08 = 0
        outBus.clear()
        inBus.clear()
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return
        val packet = event.packet
        val packet1 = event.packet.unwrap()
        if(packet1 is SPacketPlayerPosLook){
            if(AutoDisable.get().equals("Normal",true)){
                state = false
            }
            if(AutoDisable.get().equals("Silent",true)){
                S08 = SilentTicks.get()
            }
        }
        if ((OnlyGround.get() && !thePlayer.onGround) || (OnlyMove.get() && !MovementUtils.isMoving) || S08 != 0) {
            return
        }
        if(bestnomove.get()){
            if(mc.thePlayer!!.hurtTime != 0 && packet is CPacketPlayer){
                event.cancelEvent()
                outBus.add(packet)
            }
        }
        if (classProvider.isSPacketEntityVelocity(packet)) {
            val packetEntityVelocity = packet.asSPacketEntityVelocity()
            if (noMove.get() && MovementUtils.isMoving)
                event.cancelEvent()

            if (((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer ) || (Safe.get() && grimTCancel != 0))
                return
            if(TestNoMove.get()){
                if (CancelS12.get()) {
                    if(MovementUtils.isMoving) {
                        if (DeBug.get()) {
                            ClientUtils.displayChatMessage("S12 Cancelled")
                        }
                        event.cancelEvent()
                    }else {
                        if (thePlayer.onGround) {
                            if (DeBug.get()) {
                                ClientUtils.displayChatMessage("S12 Changed")
                            }
                            packetEntityVelocity.motionX = 0
                            packetEntityVelocity.motionY = 0
                            packetEntityVelocity.motionZ = 0
                        }else{
                            if (DeBug.get()) {
                                ClientUtils.displayChatMessage("S12 Cancelled")
                            }
                            event.cancelEvent()
                        }
                    }
                }
            }else {
                if (CancelS12.get()) {
                    if (DeBug.get()) {
                        ClientUtils.displayChatMessage("S12 Cancelled")
                    }
                    event.cancelEvent()
                }
            }
            if (thePlayer.onGround) {
                grimTCancel =  cancelPacketValue.get()
            } else {
                grimTCancel = AirCancelPacketValue.get()
            }
        }
        when(modeValue.get()){
            "SpoofS32"-> {
                if (packet1 !is SPacketConfirmTransaction && (packet1::class.java!!.simpleName
                        .startsWith("S", true)) && (grimTCancel > 0)
                ) {
                    if ((mc.theWorld?.getEntityByID(packet.asSPacketEntityVelocity().entityID)
                            ?: return) == thePlayer
                    ) {
                        return
                    }
                    event.cancelEvent()
                    inBus.add(packet1 as Packet<INetHandlerPlayClient>)
                    grimTCancel--
                }
                if (packet1 is SPacketConfirmTransaction && (grimTCancel > 0)) {
                    event.cancelEvent()
                    if (DeBug.get()) {
                        ClientUtils.displayChatMessage("S32 Cancelled $grimTCancel")
                    }
                }
            }
               "2" -> {
                if (((grimTCancel > 0))&& ((packet1 is S17PacketEntityLookMove)||(packet1 is S16PacketEntityLook)||(packet1 is S15PacketEntityRelMove)||(packet1 is SPacketEntityAttach)||(packet1 is SPacketEntityTeleport)||(packet1 is SPacketEntity)|| (packet1 is SPacketEntityVelocity&&(mc.theWorld?.getEntityByID(SPacketEntityVelocity().entityID) ?: return) != thePlayer))) {
                    event.cancelEvent()
                    inBus.add(packet1 as Packet<INetHandlerPlayClient>)
                }
                if (packet1 is SPacketConfirmTransaction&& ((grimTCancel > 0))) {
                    event.cancelEvent()
                    if(TestValue.get()){
                        if(grimTCancel <= TestValue1.get()){
                            inBus.add(packet1 as Packet<INetHandlerPlayClient>)
                            if (DeBug.get()) {
                                ClientUtils.displayChatMessage("S32 Test")
                            }
                        }
                    }
                    grimTCancel--
                    if (DeBug.get()) {
                        ClientUtils.displayChatMessage("S32 Cancelled $grimTCancel")
                    }
                }
            }
            "putPacket"-> {
                if (packet1 !is CPacketConfirmTransaction && packet1::class.java!!.simpleName
                        .startsWith("C", true) && (grimTCancel > 0)
                ) {
                    event.cancelEvent()
                    grimTCancel--
                    outBus.add(packet1 as Packet<INetHandlerPlayServer>)
                }
                if (packet1 is SPacketConfirmTransaction && (grimTCancel > 0)) {
                    event.cancelEvent()
                    if (DeBug.get()) {
                        ClientUtils.displayChatMessage("S32 Cancelled $grimTCancel")
                    }
                }
            }
        }

    }
    @EventTarget
    fun onWorld(event: WorldEvent) {
        outBus.clear()
        inBus.clear()
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(S08 > 0){
            if (DeBug.get()) {
                ClientUtils.displayChatMessage("Off $S08")
            }
            S08--
        }
        mc.netHandler ?: return
        if ((!inBus.isEmpty()&& grimTCancel == 0)||S08>0) {
            while (inBus.size > 0) {
                inBus.poll()?.processPacket(mc2.connection)
                if (DeBug.get()) {
                    ClientUtils.displayChatMessage("SPacket")
                }
            }
        }

        if (!outBus.isEmpty() && grimTCancel == 0 ) {
            while (outBus.size > 0) {
                val upPacket = outBus.poll() ?: continue
                PacketUtils.sendPacketNoEvent(upPacket)
                if (DeBug.get()) {
                    ClientUtils.displayChatMessage("CPacket")
                }
            }
        }
        updates++
        if (resetPersec > 0) {
            if (updates >= 0 || updates >= resetPersec) {
                updates = 0
                if (grimTCancel > 0){
                    grimTCancel--
                }
            }
        }
    }
}