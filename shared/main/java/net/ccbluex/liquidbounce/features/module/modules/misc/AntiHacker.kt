/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.TextValue

@ModuleInfo(name = "AntiHacker", description = "可以让开了HytGetName的人不打你", category = ModuleCategory.MISC)
object AntiHacker : Module() {

    private val Name = TextValue("YourName","Me")
    var delay = TimeUtils.randomDelay(500, 5000)
    val msTimer = MSTimer()

    fun onUpdate(event: UpdateEvent) {
        if (msTimer.hasTimePassed(delay)) {
            mc.thePlayer!!.sendChatMessage("杀死了" + Name)
            delay = TimeUtils.randomDelay(500, 5000)
        }
    }
}