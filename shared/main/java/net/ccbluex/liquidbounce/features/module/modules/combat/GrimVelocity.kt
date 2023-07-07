package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.*
import net.minecraft.network.play.server.SPacketEntity.S15PacketEntityRelMove
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "GrimVelocity", description = "Better", category = ModuleCategory.COMBAT)
class GrimVelocity : Module() {
    private val cancelPacketValue = IntegerValue("GroundTicks",6,0,100)
    private val AirCancelPacketValue = IntegerValue("AirTicks",6,0,100)
    private val cancelS12PacketValue = BoolValue("NoS12",true)
    private val ModeValue = ListValue("CancelPacket", arrayOf("S32", "C0f","none"), "S32")
    private val C0fResend = BoolValue("C0fResend",false)
    private val simple = BoolValue("S27Cancel",false)
    private val S32Test = BoolValue("S32Spoof",false)
    val DelayClientPacket = BoolValue("ClintPacketSpoof",false)
    private val ServerPacketTest = BoolValue("ServerPacketSpoof",false)
    private val NoMoveFix = BoolValue("NoMoveFix",false)
    private val OnlyMove = BoolValue("OnlyMove",false)
    private val OnlyGround = BoolValue("OnlyGround",false)
    private val AutoDisableMode = ListValue("AutoDisableMode",arrayOf("Safe", "Silent"),"slient")
    private val AutoSilent = IntegerValue("AutoSilentTicks",8,0,10)
    var cancelPackets = 0
    private var resetPersec = 4
    private var updates = 0
    private var S08 = 0
    private val C0fPacket = LinkedBlockingQueue<Packet<*>>()
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private val S32Packet = LinkedList<Packet<INetHandlerPlayClient>>()
    private val SPacket = LinkedList<Packet<INetHandlerPlayClient>>()
    private val debugValue = BoolValue("debug",false)
    private var bllnk = false
    fun debug(s: String) {
        if (debugValue.get())
            ClientUtils.displayChatMessage(s)
    }
    override fun onEnable() {
        cancelPackets=0
        packets.clear()
        S32Packet.clear()
        C0fPacket.clear()
    }
    override fun onDisable(){
        cancelPackets=0
        packets.clear()
        S32Packet.clear()
        C0fPacket.clear()
    }
    @EventTarget
    fun onWorld(event:WorldEvent){
        cancelPackets=0
        packets.clear()
        S32Packet.clear()
        C0fPacket.clear()
    }
    @EventTarget
    fun onPacket(event: PacketEvent){
        if((OnlyMove.get()&&!MovementUtils.isMoving)||(OnlyGround.get()&&!mc.thePlayer!!.onGround)){return}
        val packet = event.packet.unwrap()
        if(S08>0){
            S08--
            //debug("Off $S08")
            return
        }
        if(packet is SPacketPlayerPosLook){
            if(AutoDisableMode.get().equals("silent", ignoreCase = true)){
                S08 = AutoSilent.get()
            }
            if(AutoDisableMode.get().equals("safe", ignoreCase = true)){
                LiquidBounce.moduleManager[GrimVelocity::class.java].state = false
            }
        }
        if (packet is SPacketEntityVelocity) {
            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer) {
                return
            }

                if(cancelS12PacketValue.get()) {
                    if(NoMoveFix.get()){
                        packet.motionX*=0
                        packet.motionY*=0
                        packet.motionZ*=0
                    }
                    event.cancelEvent()


                }

            cancelPackets = if(mc.thePlayer!!.onGround) cancelPacketValue.get() else AirCancelPacketValue.get()
            bllnk = true
        }
        if(simple.get()&&bllnk&&packet is SPacketExplosion){
            cancelPackets--
            event.cancelEvent()
        }
        if ((
                    (packet is SPacketConfirmTransaction && ModeValue.get().equals("s32", ignoreCase = true))
                    ||(packet is CPacketConfirmTransaction && ModeValue.get().equals("c0f", ignoreCase = true)) )
            && cancelPackets > 0){
            if(C0fResend.get()&&ModeValue.get().equals("c0f", ignoreCase = true)){
                C0fPacket.add(packet)
            }
            if(S32Test.get() && ModeValue.get().equals("s32", ignoreCase = true)){
                S32Packet.add(packet as Packet<INetHandlerPlayClient>)
            }
            if(ModeValue.get().equals("s32", ignoreCase = true)){
                debug("S32")
            }else{
                debug("C0f")
            }
            event.cancelEvent()
            cancelPackets--
        }
        if(bllnk){
            if(DelayClientPacket.get() && (MovementUtils.isMoving||!OnlyMove.get())){
                if (packet is CPacketPlayer ){ // Cancel all movement stuff
                    event.cancelEvent()
                }
                if (packet is CPacketPlayer.Position || packet is CPacketPlayer.PositionRotation ||
                    packet is CPacketPlayerTryUseItemOnBlock ||
                    packet is CPacketAnimation ||
                    packet is CPacketEntityAction || packet is CPacketUseEntity || (packet::class.java.simpleName.startsWith("C", true) )
                ) {
                    event.cancelEvent()
                    packets.add(packet)
                }
                if(classProvider.isCPacketPlayerPosLook(packet)||classProvider.isCPacketPlayerPosition(packet)){
                    event.cancelEvent()
                    packets.add(packet)
                }
                if(packet is CPacketConfirmTransaction){
                    event.cancelEvent()
                    packets.add(packet)
                }
                bllnk =false

            }
            if(ServerPacketTest.get()){
                if(packet is SPacketEntityVelocity || packet is SPacketEntity || packet is SPacketSpawnPlayer || packet is SPacketEntityTeleport || packet is S15PacketEntityRelMove){
                    if(packet is SPacketEntityVelocity){
                        if ((mc.theWorld?.getEntityByID(packet.entityID) ?: return) == mc.thePlayer) {
                            return
                        }
                    }
                    SPacket.add(packet as Packet<INetHandlerPlayClient>)
                    event.cancelEvent()
                }
            }
            cancelPackets--
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if((OnlyMove.get()&&!MovementUtils.isMoving)||(OnlyGround.get()&&!mc.thePlayer!!.onGround)){return}
        updates++
        if (resetPersec > 0) {
            if (updates >= 0 || updates > resetPersec) {
                updates = 0
                if (cancelPackets > 0){
                    cancelPackets--
                }
            }
        }


        if(cancelPackets == 0 || !bllnk){
            if(DelayClientPacket.get()){
                while (packets.size > 0 &&!packets.isEmpty()) {
                    mc2.connection!!.networkManager.sendPacket(packets.take())
                    packets.clear()
                    debug("blink")
                }
            }
            if(C0fResend.get()){
                while (!C0fPacket.isEmpty()) {
                    mc2.connection!!.networkManager.sendPacket(C0fPacket.take())
                    C0fPacket.clear()
                    debug("C0fResend")
                }
            }
            if(S32Test.get()){
                while (!S32Packet.isEmpty()&&S32Packet.size > 0) {
                    S32Packet.poll()?.processPacket(mc2.connection)
                    S32Packet.clear()
                    debug("S32Test")
                }
            }
            if(ServerPacketTest.get()){
                while (SPacket.size > 0 && !SPacket.isEmpty()) {
                    SPacket.poll()?.processPacket(mc2.connection)
                    SPacket.clear()
                    debug("STest")
                }
            }
        }
    }

}