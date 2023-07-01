/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import java.util.*

@ModuleInfo(name = "HytAntiBot", description = "Prevents KillAura from attacking AntiCheat bots.", category = ModuleCategory.HYT)
class HytAntiBot : Module() {
    private val ground: MutableList<Int> = ArrayList()
    private val air: MutableList<Int> = ArrayList()
    private val invalidGround: MutableMap<Int, Int> = HashMap()
    private val swing: MutableList<Int> = ArrayList()
    private val invisible: MutableList<Int> = ArrayList()
    private val hitted: MutableList<Int> = ArrayList()

    override fun onDisable() {
        clearAll()
        super.onDisable()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return

        val packet = event.packet
        if (classProvider.isSPacketEntity(packet)) {
            val packetEntity = packet.asSPacketEntity()
            val entity = packetEntity.getEntity(mc.theWorld!!)

            if (classProvider.isEntityPlayer(entity) && entity != null) {
                if (packetEntity.onGround && !ground.contains(entity.entityId))
                    ground.add(entity.entityId)
                if (!packetEntity.onGround && !air.contains(entity.entityId))
                    air.add(entity.entityId)
                if (packetEntity.onGround) {
                    if (entity.prevPosY != entity.posY)
                        invalidGround[entity.entityId] = invalidGround.getOrDefault(entity.entityId, 0) + 1
                } else {
                    val currentVL = invalidGround.getOrDefault(entity.entityId, 0) / 2
                    if (currentVL <= 0)
                        invalidGround.remove(entity.entityId)
                    else
                        invalidGround[entity.entityId] = currentVL
                }
                if (entity.invisible && !invisible.contains(entity.entityId)) invisible.add(entity.entityId)
            }
        }
        if (classProvider.isSPacketAnimation(packet)) {
            val packetAnimation = packet.asSPacketAnimation()
            val entity = mc.theWorld!!.getEntityByID(packetAnimation.entityID)
            if (entity != null && classProvider.isEntityLivingBase(entity) && packetAnimation.animationType == 0 && !swing.contains(entity.entityId)) swing.add(entity.entityId)
        }
    }

    @EventTarget
    fun onAttack(e: AttackEvent) {
        val entity = e.targetEntity
        if (entity != null && classProvider.isEntityLivingBase(entity) && !hitted.contains(entity.entityId)) hitted.add(entity.entityId)
    }

    @EventTarget
    fun onWorld(event: WorldEvent?) {
        clearAll()
    }

    private fun clearAll() {
        hitted.clear()
        swing.clear()
        ground.clear()
        invalidGround.clear()
        invisible.clear()
    }
}