/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import co.uk.hexeption.utils.C08PacketPlayerBlockPlacement
import me.utils.PacketUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.*
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WMathHelper
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.createUseItemPacket
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.ItemSword
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketEntity
import net.minecraft.network.play.server.SPacketEntityAttach
import net.minecraft.network.play.server.SPacketWindowItems
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "NoSlow", description = "Cancels slowness effects caused by soulsand and using items.",
    category = ModuleCategory.MOVEMENT)
class NoSlow : Module() {
    private val inBus = LinkedList<Packet<INetHandlerPlayClient>>()
    private val outBus = LinkedList<Packet<INetHandlerPlayServer>>()
    private val packets = LinkedBlockingQueue<IPacket>()
    private val modeValue = ListValue("Mode", arrayOf("BedWars","TianKeng"),"BedWars")
      private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)




    val timer = MSTimer()
    private val Timer = MSTimer()
    private var pendingFlagApplyPacket = false
    private val msTimer = MSTimer()
    private var sendBuf = false
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var nextTemp = false
    private var waitC03 = false
    private var lastBlockingStat = false

    private val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura





    private val isBlocking: Boolean
        get() = (mc.thePlayer!!.isUsingItem || (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).blockingStatus) && mc.thePlayer!!.heldItem != null

    override fun onDisable() {
        Timer.reset()
        msTimer.reset()
        pendingFlagApplyPacket = false
        sendBuf = false
        packetBuf.clear()
        nextTemp = false
        waitC03 = false
    }

    private fun blink() {

        try {


            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }


        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    @EventTarget
    fun onMotion(event: MotionEvent) {



        if (!MovementUtils.isMoving) {
            return
        }


        when(modeValue.get().toLowerCase()){
            "bedwars"->{
                val item = mc.thePlayer!!.heldItem?.item

                if (classProvider.isItemBlock(item)) return

                if (event.eventState == EventState.PRE && classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item)) {
                    val curSlot = mc.thePlayer!!.inventory.currentItem
                    val spoof = if (curSlot == 0) 1 else -1
                    PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(curSlot + spoof))
                    PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(curSlot))
                }
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand()))
                mc2.connection!!.sendPacket(
                    C08PacketPlayerBlockPlacement(
                        getHytBlockpos(), 255,
                        EnumHand.MAIN_HAND, 0f, 0f, 0f
                    )
                )
            }
            "tiankeng"->{
                mc.thePlayer!!.motionX=mc.thePlayer!!.motionX
                mc.thePlayer!!.motionY=mc.thePlayer!!.motionY
                mc.thePlayer!!.motionZ= mc.thePlayer!!.motionZ

            }

        }







    }




    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        when(modeValue.get().toLowerCase()){

            "tiankeng"->{
                val packet = event.packet

                    if(mc.thePlayer!!.itemInUseCount != 0){
                     if(packet is CPacketPlayerTryUseItem || packet is CPacketPlayerTryUseItemOnBlock&& !classProvider.isItemBow(mc.thePlayer!!.heldItem)){
                        event.cancelEvent()
                        packets.add(packet)
                    }
                }

                if(mc.thePlayer!!.isUsingItem&&mc.thePlayer!!.itemInUseCount == 0){
                    blink()
                }
            }

        }
    }



    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: IItem?, isForward: Boolean): Float {
        return when {
            classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item) -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }
            classProvider.isItemSword(item) -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }
            classProvider.isItemBow(item) -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }
            else -> 0.2F
        }
    }

    fun getHytBlockpos(): BlockPos {
        val random = Random()
        val dx = WMathHelper.floor_double(random.nextDouble() / 1000 + 2820)
        val jy = WMathHelper.floor_double(random.nextDouble() / 100 * 0.20000000298023224)
        val kz = WMathHelper.floor_double(random.nextDouble() / 1000 + 2820)
        return BlockPos(dx, -jy % 255, kz)
    }
    override val tag: String?
        get() = modeValue.get()

}
