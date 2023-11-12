/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import me.utils.PacketUtils
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.Block
import net.minecraft.inventory.ClickType
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.SPacketWindowItems
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
/*
已提交 https://github.com/GrimAnticheat/Grim/pull/995

泛滥狗：
    Ideal Q1779667227

prideplusnextgen这种就把mc2换成mc

改description死妈了 改description死妈了 改description死妈了
改description死妈了 改description死妈了 改description死妈了
改description死妈了 改description死妈了 改description死妈了
改description死妈了 改description死妈了 改description死妈了
改description死妈了 改description死妈了 改description死妈了
 */


@ModuleInfo(name = "NoSlow", description = "artday moyusense",
    category = ModuleCategory.MOVEMENT)
class NoSlow : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Basic", "NoPacket", "GrimAC", "HuaYuTing", "Spoof", "Watchdog", "NewNCP", "HYTPit"), "Basic")

    // Highly customizable values

    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val better = BoolValue("Better-Check", false)
    private val debug = BoolValue("Debug", false)

    private val msTimer = MSTimer()
    private val badPacket = LinkedBlockingDeque<SPacketWindowItems>()
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var c08: CPacketPlayerTryUseItem? = null
    private var send = false
    private var noRev = false
    private var lastBlockingStat = false
    private var blinking = false

    // Blocks
    val soulsandValue = BoolValue("Soulsand", true)

    override fun onEnable() {
        msTimer.reset()
        c08 = null
        send = false
        blinking = false
        noRev = false
        badPacket.clear()
        packetBuf.clear()
        lastBlockingStat = false
    }
    private fun onPre(event : MotionEvent): Boolean {
        return event.eventState == EventState.PRE
    }
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if ((!mc2.player.isActiveItemStackBlocking && !mc2.player.isHandActive || !MovementUtils.isMoving) && better.get())
            return

        if (debug.get() && mc2.player.ticksExisted % 10 == 0) ClientUtils.displayChatMessage("NoSlow -> Slowdown:" + mc2.player.ticksExisted)

        when (modeValue.get().toLowerCase()) {
            "basic" -> {
                if (event.eventState == EventState.PRE) mc2.connection!!.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
            }
            "huayuting" -> {
                val item = mc.thePlayer!!.heldItem!!.item != null && mc.thePlayer!!.isUsingItem
                        && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)
                val blockPlace1 =
                    net.ccbluex.liquidbounce.utils.createUseItemPacket(
                        mc.thePlayer!!.inventory.getCurrentItemInHand(),
                        WEnumHand.MAIN_HAND
                    )
                val blockPlace2 =
                    net.ccbluex.liquidbounce.utils.createUseItemPacket(
                        mc.thePlayer!!.inventory.getCurrentItemInHand(),
                        WEnumHand.OFF_HAND
                    )

                if (event.eventState == EventState.PRE && item) {
                    if (onPre(event)){
                        mc2.connection!!.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                    } else {
                        mc2.connection!!.sendPacket(CPacketConfirmTransaction())
                        PacketUtils.sendTryUseItem()
                    }
                }
                if (event.eventState == EventState.POST && item) {
                    mc2.connection!!.sendPacket(CPacketConfirmTransaction())
                    mc.netHandler.addToSendQueue(blockPlace1)
                    mc.netHandler.addToSendQueue(blockPlace2)
                }
            }
            "watchdog" -> {
                if (!mc2.player.isActiveItemStackBlocking) return
                if (event.eventState == EventState.PRE) {
                    mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem % 8 + 1))
                    mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                } else {
                    mc2.connection!!.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                }
            }
            "newncp" -> {
                if (event.eventState == EventState.PRE) {
                    mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem % 8 + 1))
                    mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get().toLowerCase()) {
            "grimac" -> {
                if (msTimer.hasTimePassed(230) && blinking) {
                    blinking = false
                    mc2.connection!!.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1, -1, -1), EnumFacing.DOWN))
                    if (packetBuf.isNotEmpty()) {
                        var canAttack = false
                        for (packet in packetBuf) {
                            if (packet is CPacketPlayer) {
                                canAttack = true
                            }
                            if(!((packet is CPacketUseEntity || packet is CPacketAnimation) && !canAttack)) {
                                mc2.connection!!.sendPacket(packet)
                            }
                        }
                        packetBuf.clear()
                    }
                }

                if(!blinking) {
                    lastBlockingStat = mc2.player.isActiveItemStackBlocking
                    if (!mc2.player.isActiveItemStackBlocking) {
                        return
                    }
                    mc2.connection!!.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                    blinking = true
                    msTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItemMainhand = mc2.player!!.heldItemMainhand.item

        event.forward = getMultiplier(heldItemMainhand, true)
        event.strafe = getMultiplier(heldItemMainhand, false)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val packet = event.packet.unwrap()

        when (modeValue.get().toLowerCase()) {
            "spoof" -> {
                if (packet is CPacketPlayerTryUseItem) {
                    if (packet.hand == EnumHand.MAIN_HAND && mc2.player.heldItemMainhand.item is ItemSword) {
                        event.cancelEvent()
                    }
                }
            }
            "huayuting" -> {
                if (mc2.player.heldItemMainhand.item is ItemFood || mc2.player.heldItemMainhand.item is ItemBow || mc2.player.heldItemMainhand.item is ItemPotion) {
                    if (packet is CPacketPlayerTryUseItem && !noRev) {
                        event.cancelEvent()
                        mc2.connection!!.sendPacket(CPacketClickWindow(0, 36, 0, ClickType.SWAP, ItemStack(Block.getBlockById(166)), 0))
                        c08 = packet
                    }

                    if (packet is CPacketPlayerDigging) {
                        if (packet.action == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                            send = true
                        }
                    }

                    if (packet is SPacketWindowItems) {
                        event.cancelEvent()
                        badPacket.add(packet)
                        if (c08 != null) {
                            noRev = true
                            mc2.connection!!.sendPacket(CPacketConfirmTransaction())
                            mc2.connection!!.sendPacket(c08!!)
                            noRev = false
                            c08 = null
                        }
                    }
                }
            }
            "grimac" -> {
                if (blinking) {
                    if ((packet is CPacketPlayerDigging || packet is CPacketPlayerTryUseItem) && mc2.player.isActiveItemStackBlocking) {
                        event.cancelEvent()
                    } else if (packet is CPacketPlayer || packet is CPacketAnimation || packet is CPacketEntityAction || packet is CPacketUseEntity || packet is CPacketPlayerDigging || packet is CPacketPlayerTryUseItem) {
                        packetBuf.add(packet as Packet<INetHandlerPlayServer>)
                        event.cancelEvent()
                    }
                }
            }
        }
    }

    private fun getMultiplier(item: Item?, isForward: Boolean): Float {
        return when (item) {
            is ItemFood, is ItemPotion, is ItemBucketMilk -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }

            is ItemSword -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }

            is ItemBow -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }

            else -> 0.2F
        }
    }

    override val tag: String
        get() = modeValue.get()
}
