/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.inventory.IGuiChest
import net.ccbluex.liquidbounce.api.minecraft.inventory.ISlot
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.player.InventoryCleaner
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.item.ItemUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.inventory.ClickType
import net.minecraft.network.play.client.CPacketClickWindow
import java.awt.Color
import kotlin.random.Random

@ModuleInfo(name = "ChestStealer", description = "Automatically steals all items from a chest.", category = ModuleCategory.WORLD)
class ChestStealer : Module() {

    /**
     * OPTIONS
     */

    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 200, 0, 400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelayValue.get()
            if (i > newValue)
                set(i)

            nextDelay = TimeUtils.randomDelay(minDelayValue.get(), get())
        }
    }

    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 150, 0, 400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelayValue.get()

            if (i < newValue)
                set(i)

            nextDelay = TimeUtils.randomDelay(get(), maxDelayValue.get())
        }
    }
    private val delayOnFirstValue = BoolValue("DelayOnFirst", false)
    private val instantexploit = BoolValue("tickstealer",false)
    private val takeRandomizedValue = BoolValue("TakeRandomized", false)
    private val onlyItemsValue = BoolValue("OnlyItems", false)
    private val noCompassValue = BoolValue("NoCompass", false)

    public var stealing = false

    private val autoCloseMaxDelayValue: IntegerValue = object : IntegerValue("AutoCloseMaxDelay", 0, 0, 400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = autoCloseMinDelayValue.get()
            if (i > newValue) set(i)
            nextCloseDelay = TimeUtils.randomDelay(autoCloseMinDelayValue.get(), this.get())
        }
    }

    private val autoCloseMinDelayValue: IntegerValue = object : IntegerValue("AutoCloseMinDelay", 0, 0, 400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = autoCloseMaxDelayValue.get()
            if (i < newValue) set(i)
            nextCloseDelay = TimeUtils.randomDelay(this.get(), autoCloseMaxDelayValue.get())
        }
    }

    private val closeOnFullValue = BoolValue("CloseOnFull", true)
    private val chestTitleValue = BoolValue("ChestTitle", false)

    public val slientValue = BoolValue("Silent", false)

    /**
     * VALUES
     */

    private val delayTimer = MSTimer()
    private var nextDelay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

    private val autoCloseTimer = MSTimer()
    private var nextCloseDelay = TimeUtils.randomDelay(autoCloseMinDelayValue.get(), autoCloseMaxDelayValue.get())

    private var contentReceived = 0

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val scaledResolution = classProvider.createScaledResolution(mc)
        if (this.stealing && this.slientValue.get())
            Fonts.font35.drawCenteredString("Stealing", (scaledResolution.scaledWidth / 2).toFloat(), (scaledResolution.scaledHeight / 2 - 20).toFloat(), Color.WHITE.rgb, true)
    }

    @EventTarget
    fun onUpdate(event:UpdateEvent){
        if (instantexploit.get()) {
            if (classProvider.isGuiChest(mc.currentScreen)) {
                val chest = mc.currentScreen?.asGuiChest()
                val rows = chest?.inventoryRows!! * 9
                for (i in 0 until rows) {
                    val slot = chest!!.inventorySlots?.getSlot(i)
                    if (slot!!.hasStack) {
                        mc2.connection?.sendPacket(
                            CPacketClickWindow(
                                chest?.inventorySlots?.windowId!!,
                                i,
                                0,
                                ClickType.QUICK_MOVE,
                                slot?.stack!!.unwrap(),
                                1.toShort()
                            )
                        )
                    }
                }
                mc.thePlayer?.closeScreen()
            }
        }
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val thePlayer = mc.thePlayer!!

        if (!classProvider.isGuiChest(mc.currentScreen) || mc.currentScreen == null) {
            if (delayOnFirstValue.get())
                delayTimer.reset()
            autoCloseTimer.reset()
            stealing = false
            return
        }

        if (!delayTimer.hasTimePassed(nextDelay)) {
            autoCloseTimer.reset()
            return
        }

        val screen = mc.currentScreen!!.asGuiChest()

        // No Compass
        if (noCompassValue.get() && thePlayer.inventory.getCurrentItemInHand()?.item?.unlocalizedName == "item.compass")
            return

        // Chest title
        if (chestTitleValue.get() && (screen.lowerChestInventory == null || !screen.lowerChestInventory!!.name.contains(classProvider.createItemStack(functions.getObjectFromItemRegistry(classProvider.createResourceLocation("minecraft:chest"))!!).displayName)))
            return

        // inventory cleaner
        val inventoryCleaner = LiquidBounce.moduleManager[InventoryCleaner::class.java] as InventoryCleaner

        // Is empty?
        if (!isEmpty(screen) && (!closeOnFullValue.get() || !fullInventory)) {
            this.stealing = true
            autoCloseTimer.reset()

            // Randomized
            if (takeRandomizedValue.get()) {
                do {
                    val items = mutableListOf<ISlot>()

                    for (slotIndex in 0 until screen.inventoryRows * 9) {
                        val slot = screen.inventorySlots!!.getSlot(slotIndex)

                        val stack = slot.stack

                        if (stack != null && (!onlyItemsValue.get() || !classProvider.isItemBlock(stack.item)) && (!inventoryCleaner.state || inventoryCleaner.isUseful(stack, -1)))
                            items.add(slot)
                    }

                    val randomSlot = Random.nextInt(items.size)
                    val slot = items[randomSlot]

                    move(screen, slot)
                } while (delayTimer.hasTimePassed(nextDelay) && items.isNotEmpty())
                return
            }

            // Non randomized
            for (slotIndex in 0 until screen.inventoryRows * 9) {
                val slot = screen.inventorySlots!!.getSlot(slotIndex)

                val stack = slot.stack

                if (delayTimer.hasTimePassed(nextDelay) && shouldTake(stack, inventoryCleaner)) {
                    move(screen, slot)
                }
            }
            //AutoClose
        } else if (screen.inventorySlots!!.windowId == contentReceived && autoCloseTimer.hasTimePassed(nextCloseDelay)) {
            thePlayer.closeScreen()
            stealing = false
            nextCloseDelay = TimeUtils.randomDelay(autoCloseMinDelayValue.get(), autoCloseMaxDelayValue.get())
        }
    }

    override fun onEnable() {
        stealing = false
    }

    override fun onDisable() {
        stealing = false
    }

    @EventTarget
    private fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (classProvider.isSPacketWindowItems(packet))
            contentReceived = packet.asSPacketWindowItems().windowId
    }

    private inline fun shouldTake(stack: IItemStack?, inventoryCleaner: InventoryCleaner): Boolean {
        return stack != null && !ItemUtils.isStackEmpty(stack) && (!onlyItemsValue.get() || !classProvider.isItemBlock(stack.item)) && (!inventoryCleaner.state || inventoryCleaner.isUseful(stack, -1))
    }

    private fun move(screen: IGuiChest, slot: ISlot) {
        screen.handleMouseClick(slot, slot.slotNumber, 0, 1)
        //mc.netHandler.networkManager.sendPacket(CPacketClickWindow(slot.stack!!.stackSize, slot.slotNumber, 1, null,1)as IPacket)
        delayTimer.reset()
        nextDelay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
    }

    private fun isEmpty(chest: IGuiChest): Boolean {
        val inventoryCleaner = LiquidBounce.moduleManager[InventoryCleaner::class.java] as InventoryCleaner

        for (i in 0 until chest.inventoryRows * 9) {
            val slot = chest.inventorySlots!!.getSlot(i)

            val stack = slot.stack

            if (shouldTake(stack, inventoryCleaner))
                return false
        }

        return true
    }

    private val fullInventory: Boolean
        get() = mc.thePlayer?.inventory?.mainInventory?.none(ItemUtils::isStackEmpty) ?: false
}