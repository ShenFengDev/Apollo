package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.MinecraftInstance.*
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

import net.minecraft.world.World
@ModuleInfo("Noxz","", category = ModuleCategory.COMBAT)
class NoXZ : Module() {
    private val alwaysValue = BoolValue("Always", true)

    private val onlyAirValue = BoolValue("}OnlyBreakAir", true)

    private val worldValue = BoolValue("BreakOnWorld", false)

    private val sendC03Value = BoolValue("SendC03", false)// bypass latest but flag timer

    private val C06Value = BoolValue("Send1.17C06", false) // need via to 1.17+

    private val flagPauseValue = IntegerValue("FlagPause-Time", 50, 0, 5000)

    var gotVelo = false
    var flagTimer = MSTimer()

     override fun onEnable() {
        gotVelo = false
        flagTimer.reset()
    }

     fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet is SPacketPlayerPosLook)
            flagTimer.reset()
        if (!flagTimer.hasTimePassed(flagPauseValue.get().toLong())) {
            gotVelo = false
            return
        }

        if (packet is SPacketEntityVelocity && packet.entityID == mc.thePlayer?.entityId) {
            event.cancelEvent()
            gotVelo = true
        } else if (packet is SPacketExplosion) {
            event.cancelEvent()
            gotVelo = true
        }
    }

    fun onTick(event: TickEvent) {

        if (!flagTimer.hasTimePassed(flagPauseValue.get().toLong())) {
            gotVelo = false
            return
        }

        val thePlayer = mc.thePlayer ?: return
        val theWorld = mc.theWorld ?: return
        if (gotVelo || alwaysValue.get()) { // packet processed event pls
            val pos = BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ)
            if (checkBlock(pos) || checkBlock(pos.up())) {
                gotVelo = false
            }
        }
    }

    fun checkBlock(pos: BlockPos): Boolean {
        if (!onlyAirValue.get() || mc2.world.isAirBlock(pos)) {
            if (sendC03Value.get()) {
                if (C06Value.get())
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ, mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch, mc.thePlayer!!.onGround)
                    )
                else
                    mc.netHandler.addToSendQueue(CPacketPlayer(mc.thePlayer!!.onGround) as IPacket)
            }
            mc.netHandler.addToSendQueue(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN) as IPacket)
            if (worldValue.get())
                mc2.world.setBlockToAir(pos)

            return true
        }
        return false
    }
}