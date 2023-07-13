
package net.ccbluex.liquidbounce.features.module.modules.combat


import me.utils.player.PlayerUtil.isMoving
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketEntityAction


@ModuleInfo(name = "SuperKnockback", description = "A module of control player attack knockback", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("Old","Smart","wtap" ), "Smart")
    private val onlyMoveValue = BoolValue("OnlyMove", false)
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val delay = IntegerValue("Delay", 0, 0, 500)

    val timer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            if (event.targetEntity!!.asEntityLivingBase().hurtTime > hurtTimeValue.get() || !timer.hasTimePassed(delay.get().toLong()) ||
                (!isMoving() && onlyMoveValue.get()) || (!mc.thePlayer!!.onGround && onlyGroundValue.get())) {
                return
            }
            when (modeValue.get().toLowerCase()) {
                "old"->{

                    if (mc.thePlayer!!.sprinting) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))

                    mc.thePlayer!!.sprinting = true
                    mc.thePlayer!!.serverSprintState = true
                }
                "smart" -> {
                    if(mc.thePlayer!!.sprinting){
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                        mc.thePlayer!!.sprinting = true
                        mc.thePlayer!!.serverSprintState = true
                    }else{
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    }


                }

                "wtap" -> {
                    if (mc2.player.isSprinting) {
                        mc2.player.isSprinting = false
                    }

                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc2.player.serverSprintState = true
                }

            }
            timer.reset()
        }
    }
    override val tag: String
        get() = modeValue.get()
}
