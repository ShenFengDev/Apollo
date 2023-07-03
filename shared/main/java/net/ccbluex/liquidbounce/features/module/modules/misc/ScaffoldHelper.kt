package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue


@ModuleInfo("ScaffoldHelper","ScaffoldHelper",category = ModuleCategory.MISC)
class ScaffoldHelper : Module() {
        //private val delayValue = IntegerValue("Delay",0,0,1)



    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val scaffold = LiquidBounce.moduleManager.getModule(Scaffold::class.java)
        //val long = delayValue.get().toLong()
        if(!MovementUtils.isMoving) return


                scaffold.state = !mc.thePlayer!!.onGround




        if(!scaffold.state&&mc.thePlayer!!.onGround){
            RotationUtils.setTargetRotation(Rotation(mc.thePlayer!!.rotationYaw,18f))
        }



    }

    override fun onDisable() {
        val scaffold = LiquidBounce.moduleManager.getModule(Scaffold::class.java)as Scaffold
        scaffold.state = false

    }
    override fun onEnable() {


    }

}