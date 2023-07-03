/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat


import me.utils.PacketUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketClientStatus
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketPlayerPosLook
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@ModuleInfo(name = "Velocity", description = "Edit your velocity",category = ModuleCategory.COMBAT)
class Velocity : Module() {


    /**
     * OPTIONS
     */

    private val modeValue = ListValue("Mode", arrayOf("GrimReduce","NewGrimAC","Jump","Grim"), "GrimReduce")


    // AAC Push

    public var block: IBlock? = null


    private val noFireValue = BoolValue("noFire", false)


    private val hytGround = BoolValue("HytOnlyGround", true)
    private val hyMove = BoolValue("HytOnlyMove", false)

    private val debugValue = BoolValue("Debug",false)


    /**
     * VALUES
     */



    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }



    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        if ((noFireValue.get() && mc.thePlayer!!.burning)||(hytGround.get()&&!mc.thePlayer!!.onGround)) return

        when (modeValue.get().toLowerCase()) {
            "grim"->{
                if(mc.thePlayer!!.hurtTime > 8){
                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!.asEntityPlayer(),ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!.asEntityPlayer(),ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!.asEntityPlayer(),ICPacketEntityAction.WAction.START_SPRINTING))

                }
                if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                    thePlayer.motionY = 0.42

                    val yaw = thePlayer.rotationYaw * 0.017453292F

                    thePlayer.motionX -= sin(yaw) * 0.1845141919810
                    thePlayer.motionZ += cos(yaw) * 0.1845141919810
                    if(debugValue.get()&&mc.thePlayer!!.hurtTime>=9){
                        ClientUtils.displayChatMessage("reduce"+(-mc.thePlayer!!.motionX))
                    }
                }
            }


            "grimreduce"->{
                if (thePlayer.hurtTime > 0) {
                    thePlayer.motionX += -1.0E-7
                    thePlayer.motionY += -1.0E-7
                    thePlayer.motionZ += -1.0E-7
                    thePlayer.isAirBorne = true
                    if(debugValue.get()&&mc.thePlayer!!.hurtTime>=9){
                        ClientUtils.displayChatMessage("reduce"+(-mc.thePlayer!!.motionX))
                    }
                }
            }

            "newgrimac"->{
                if(debugValue.get()&&mc.thePlayer!!.hurtTime>=9){
                    ClientUtils.displayChatMessage("reduce"+(-mc.thePlayer!!.motionX-mc.thePlayer!!.motionX))
                }
                if (thePlayer.hurtTime == 8){
                    thePlayer.motionX += -1.0E-8
                    thePlayer.motionZ += -1.0E-8
                }
                if (thePlayer.hurtTime == 5){
                    thePlayer.motionX += -1.5E-8
                    thePlayer.motionZ += -1.5E-8

                }
                if (thePlayer.hurtTime == 3){
                    thePlayer.motionX += -1.0E-7
                    thePlayer.motionZ += -1.0E-7

                }
            }


            "jump" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                thePlayer.motionY = 0.42

                val yaw = thePlayer.rotationYaw * 0.017453292F

                thePlayer.motionX -= sin(yaw) * 0.2
                thePlayer.motionZ += cos(yaw) * 0.2
                if(debugValue.get()&&mc.thePlayer!!.hurtTime>=9){
                    ClientUtils.displayChatMessage("reduce"+(-mc.thePlayer!!.motionX))
                }
            }




        }


    }

    }





