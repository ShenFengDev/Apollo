/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import kotlin.random.Random

@ModuleInfo(name = "LegitAura", description = "wawa", category = ModuleCategory.COMBAT)
class LegitAura : Module() {

    private val maxCPSValue: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {

        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minCPSValue.get()
            if (minCPS > newValue)
                set(minCPS)
        }

    }

    private val minCPSValue: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {

        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxCPS = maxCPSValue.get()
            if (maxCPS < newValue)
                set(maxCPS)
        }

    }

    private val rangeValue = FloatValue("Range", 4.4F, 1F, 8F)
    private val turnSpeedValue = FloatValue("TurnSpeed", 2F, 1F, 180F)
    private val fovValue = FloatValue("FOV", 180F, 1F, 180F)
    private val centerValue = BoolValue("Center", false)
    private val lockValue = BoolValue("Lock", true)
    private val jitterValue = BoolValue("Jitter", false)

    private val clickTimer = MSTimer()
    private var leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var leftLastSwing = 0L

    fun runAttack1() {
        // Left click
        if (System.currentTimeMillis() - leftLastSwing >= leftDelay && mc.playerController.curBlockDamageMP == 0F) {
            mc.gameSettings.keyBindAttack.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click Handling

            leftLastSwing = System.currentTimeMillis()
            leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
        }
    }


    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (mc.gameSettings.keyBindAttack.isKeyDown)
            clickTimer.reset()


        val thePlayer = mc.thePlayer ?: return

        val range = rangeValue.get()
        val entity = mc.theWorld!!.loadedEntityList
                .filter {
                    EntityUtils.isSelected(it, true) && thePlayer.canEntityBeSeen(it) &&
                            thePlayer.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(it) <= fovValue.get()
                }
                .minBy { RotationUtils.getRotationDifference(it) } ?: return

        if(thePlayer.getDistanceToEntityBox(entity) <= range)runAttack1()

        if (!lockValue.get() && RotationUtils.isFaced(entity, range.toDouble()))
            return




        val (_, rotation1) = RotationUtils.lockView(
            entity.entityBoundingBox,
            true && !clickTimer.hasTimePassed(leftDelay / 2),
            true,
            false,
            false,
            range
        ) ?: return

        val rotation = RotationUtils.limitAngleChange(
                Rotation(thePlayer.rotationYaw, thePlayer.rotationPitch),
                if (centerValue.get())
                    RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox),false)
                else
                    rotation1,
                (turnSpeedValue.get() + Math.random()).toFloat()
        )

        rotation.toPlayer(thePlayer)

        if (jitterValue.get()) {
            val yaw = Random.nextBoolean()
            val pitch = Random.nextBoolean()
            val yawNegative = Random.nextBoolean()
            val pitchNegative = Random.nextBoolean()

            if (yaw)
                thePlayer.rotationYaw += if (yawNegative) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)

            if (pitch) {
                thePlayer.rotationPitch += if (pitchNegative) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)
                if (thePlayer.rotationPitch > 90)
                    thePlayer.rotationPitch = 90F
                else if (thePlayer.rotationPitch < -90)
                    thePlayer.rotationPitch = -90F
            }
        }
    }
}