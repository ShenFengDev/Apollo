package net.ccbluex.liquidbounce.features.module.modules.liquidbounce

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.value.ListValue


@ModuleInfo(name = "NoLagHYT", description = "Recall", category = ModuleCategory.HYT)
class NoLagHYT : Module() {

    private val modeValue = ListValue("Mode", arrayOf("HYT","AAC5","Test"), "AntiCheat")
    private var ticks = 0
    private var a = 0
    private var b=0
    override fun onEnable() {
        ticks = 0
    }
    @EventTarget
    fun onUpdate(){
        when(modeValue.get().toLowerCase()){
            "aac5"->{

                val Killaura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
                if(mc.thePlayer!!.onGround){
                    if(b == 0){
                        Killaura.keepSprintValue.set(true)
                        b++
                    }
                }else{
                    b = 0
                    if(a == 0){
                        Killaura.keepSprintValue.set(false)
                        a++
                    }
                }
                if(ticks > 250){
                    if(mc.thePlayer!!.isOnLadder&&mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer!!.motionY=0.11
                    }
                }
                if(ticks > 500){
                    ticks = 0
                }else{
                    ticks++
                }
            }
            "hyt"->{
                if(ticks > 1000){
                    if(mc.thePlayer!!.isOnLadder&&mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer!!.motionY=0.11
                    }
                }
                if(ticks > 2000){
                    ticks = 0
                }else{
                    ticks++
                }
            }
            "test"->{
                if(ticks > 1000){
                    if(mc.thePlayer!!.isOnLadder&&mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer!!.motionY = 0.09
                        mc.thePlayer!!.motionY = 0.10
                        mc.thePlayer!!.motionY = 0.11
                    }
                }
                if(ticks > 2000){
                    ticks = 0
                }else{
                    ticks++
                }
            }
        }
    }
    override val tag: String
        get() = this.modeValue.get()
}
