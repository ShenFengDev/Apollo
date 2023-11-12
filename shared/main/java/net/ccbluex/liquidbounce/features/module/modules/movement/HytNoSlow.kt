/*

    ——————————————————————————————————————————————————————————————————————————————
    孩子不懂事弄着玩的 孩子不懂事弄着玩的 孩子不懂事弄着玩的 孩子不懂事弄着玩的 孩子不懂事弄着玩的
    ——————————————————————————————————————————————————————————————————————————————
    大佬不要骂我我有玉玉症 大佬不要骂我我有玉玉症 大佬不要骂我我有玉玉症 大佬不要骂我我有玉玉症
    ——————————————————————————————————————————————————————————————————————————————
    好多东西没删干净  缓速吃优化 By Lynn   跑吃 By Newno
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.*
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.util.EnumFacing
import me.utils.PacketUtils
import java.util.*


@ModuleInfo(
    name = "HYTNoSlow", description = "Bypass Hyt",
    category = ModuleCategory.MOVEMENT

)
class HYTNoSlow : Module() {

    private val modeValue = ListValue(
        "PacketMode",
        arrayOf("Hyt-Custom","Hyt-Normal","Hyt-Fast","Hyt-Legit","Hyt-NoMove"),
        "HytBw"
    )
    private var customslow = FloatValue("Custom-Slow", 0.6f, 0f, 1f)
    private var customtimer = FloatValue("Custom-Timer", 1f, 0f, 1f)
    //从这里开始不会删的不要删
    private val Timer = MSTimer()
    private var pendingFlagApplyPacket = false
    private val msTimer = MSTimer()
    private var sendBuf = false
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var nextTemp = false
    private var waitC03 = false
    private var packet = 0

    private fun OnPre(event: MotionEvent): Boolean {
        return event.eventState == EventState.PRE
    }
    private fun OnPost(event: MotionEvent): Boolean {
        return event.eventState == EventState.POST
    }
    override fun onDisable() {
        Timer.reset()
        msTimer.reset()
        pendingFlagApplyPacket = false
        sendBuf = false
        packetBuf.clear()
        nextTemp = false
        waitC03 = false
    }

    private fun sendPacket(
        Event: MotionEvent,
        SendC07: Boolean,
        SendC08: Boolean,
        Delay: Boolean,
        DelayValue: Long,
        onGround: Boolean,
        Hypixel: Boolean = false
    ) {
        val aura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        val digging = classProvider.createCPacketPlayerDigging(
            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
            WBlockPos(-1, -1, -1),
            EnumFacing.DOWN as IEnumFacing
        )
        val blockPlace =
            classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.currentItem as IItemStack)
        val blockMent = classProvider.createCPacketPlayerBlockPlacement(
            WBlockPos(-1, -1, -1),
            255,
            mc.thePlayer!!.inventory.currentItem as IItemStack,
            0f,
            0f,
            0f
        )
        if (onGround && !mc.thePlayer!!.onGround) {
            return
        }
        if (SendC07 && OnPre(Event)) {
            if (Delay && Timer.hasTimePassed(DelayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!Delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (SendC08 && OnPost(Event)) {
            if (Delay && Timer.hasTimePassed(DelayValue) && !Hypixel) {
                mc.netHandler.addToSendQueue(blockPlace)
                Timer.reset()
            } else if (!Delay && !Hypixel) {
                mc.netHandler.addToSendQueue(blockPlace)
            } else if (Hypixel) {
                mc.netHandler.addToSendQueue(blockMent)
            }
        }
    }
    //到这里结束
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!MovementUtils.isMoving) {
            return
        }
        when (modeValue.get().toLowerCase()) {
            "hyt-custom" -> {
                if ((event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) && !mc.thePlayer!!.isBlocking && classProvider.isItemFood(
                        mc.thePlayer!!.heldItem!!.item
                    ) || classProvider.isItemPotion(mc.thePlayer!!.heldItem!!.item)
                ) {
                    if (mc.thePlayer!!.isUsingItem && mc.thePlayer!!.itemInUseCount >= 1) {
                        mc.timer.timerSpeed = customtimer.get()
                        return
                    }
                    return
                }
                if (event.eventState == EventState.PRE && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                    mc.timer.timerSpeed = 1.0F
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand() as IItemStack))
                }
            }

            "hyt-normal" -> {
                if ((event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) && !mc.thePlayer!!.isBlocking && classProvider.isItemFood(
                        mc.thePlayer!!.heldItem!!.item
                    ) || classProvider.isItemPotion(mc.thePlayer!!.heldItem!!.item)
                ) {
                    if (mc.thePlayer!!.isUsingItem && mc.thePlayer!!.itemInUseCount >= 1) {
                        if(mc.thePlayer!!.sprinting){
                            if(mc.thePlayer!!.onGround != true){
                                mc.thePlayer!!.sprinting = false
                            }
                        }
                        if (mc.thePlayer!!.ticksExisted % 2 === 0) {
                            mc.thePlayer!!.sprinting = false  //开始进食取消疾跑状态
                        } else {
                            mc.thePlayer!!.sprinting = true   //开始疾跑状态
                        }
                        return
                    }
                }
                if (event.eventState == EventState.PRE && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand() as IItemStack))
                }
            }
            "hyt-fast" -> {
                if((event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) && !mc.thePlayer!!.isBlocking && classProvider.isItemFood(mc.thePlayer!!.heldItem!!.item) || classProvider.isItemPotion(mc.thePlayer!!.heldItem!!.item)){
                    if(mc.thePlayer!!.isUsingItem && mc.thePlayer!!.itemInUseCount >= 1){
                        if(mc.thePlayer!!.sprinting){
                            if(mc.thePlayer!!.onGround != true){
                                mc.thePlayer!!.sprinting = false
                            }
                        }
                        if (packet != 16) {
                            if (mc.thePlayer!!.ticksExisted % 2 === 0) {
                                mc.thePlayer!!.sprinting = false  //开始进食取消疾跑状态
                                mc.timer.timerSpeed = 0.33f   //开始进食进入缓速timer
                            }
                            else {
                                mc.thePlayer!!.sprinting = true   //每一阶段进食后开始疾跑状态
                                mc.timer.timerSpeed = 0.9F   //每一阶段进食后加速骗过grim
                                //此处应该有一个神笔spoof
                            }
                            PacketUtils.sendPacketNoEvent(CPacketPlayer(true))
                            return
                        }
                    }
                }
                if (event.eventState == EventState.PRE && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                    mc.timer.timerSpeed = 1.0F
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand() as IItemStack))
                }
            }
            "hyt-nomove" -> {
                if (event.eventState == EventState.PRE && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand() as IItemStack))
                }
            }
            "hyt-legit" -> {
                if (event.eventState == EventState.PRE && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand() as IItemStack))
                }
            }
            /*
            燃油费我给你跪下了你不要rename香草了
            "Grim" -> {
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionX
                mc.thePlayer!!.motionY = mc.thePlayer!!.motionY
                mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ
            }
             */

        }

    }
    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }
    //这里就是绕过的关键了
    private fun getMultiplier(item: IItem?, isForward: Boolean): Float {
        if (modeValue.get() == "Hyt-Custom" ) {
            return when {
                classProvider.isItemSword(item) -> {
                    if (isForward) 1F else 1F
                }

                else -> customslow.get()
            }
        }
        if (modeValue.get() == "Hyt-NoMove" ) {
            return when {
                classProvider.isItemSword(item) -> {
                    if (isForward) 1F else 1F
                }

                else -> 0F
            }
        }
        if (modeValue.get() == "Hyt-Legit" ) {
            return when {
                classProvider.isItemSword(item) -> {
                    if (isForward) 1F else 1F
                }

                else -> 0.8F
            }
        }
        if (modeValue.get() == "Hyt-Normal" ) {
            return when {
                classProvider.isItemSword(item) -> {
                    if (isForward) 1F else 1F
                }
                else -> 0.59999999999999999999999999999999114514F
            }
        }
        if (modeValue.get() == "Hyt-Fast" ) {
            return when {
                classProvider.isItemSword(item) -> {
                    if (isForward) 1F else 1F
                }
                else -> 0.79F
            }
        }else {

            //打滑其他模式吃东西无法移动的改这里
            //0F改0.2F就是原版减速 1F就是无减速 0F就是停止移动
            return when {
                classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item) -> {
                    if (isForward) 0F else 0F
                }

                classProvider.isItemBow(item) -> {
                    if (isForward) 0F else 0F
                }
                classProvider.isItemSword(item) -> {
                    if (isForward) 1F else 1F
                }

                else -> 0F
            }
        }
    }
    override val tag: String
        get() = modeValue.get()

}


