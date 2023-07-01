package net.ccbluex.liquidbounce.features.module.modules.liquidbounce


import me.utils.PacketUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketKeepAlive


@ModuleInfo(name = "HytDisabler", description = "修复版", category = ModuleCategory.HYT)
class HytDisabler : Module() {

    val modeValue = ListValue(
        "Mode",
        arrayOf(
            "HytSpartan"
        ), "HytSpartan"
    )

    // debug
    private val debugValue = BoolValue("Debug", true)


    // variables
    private val keepAlives = arrayListOf<CPacketKeepAlive>()
    private val transactions = arrayListOf<CPacketConfirmTransaction>()
    private val msTimer = MSTimer()

    fun debug(s: String) {
        if (debugValue.get())
            ClientUtils.displayChatMessage("§7[§3§lDisabler§7]§f $s")

    }
    @EventTarget
    fun onPacket(event: PacketEvent) {

        val packet = event.packet
        when (modeValue.get().toLowerCase()) {
            "hytspartan" -> {
                if (packet is CPacketKeepAlive && (keepAlives.size <= 0 || packet != keepAlives[keepAlives.size - 1])) {
                    debug(LiquidBounce.CLIENT_NAME + "c00 added")
                    keepAlives.add(packet)
                    event.cancelEvent()
                }
                if (packet is CPacketConfirmTransaction && (transactions.size <= 0 || packet != transactions[transactions.size - 1])) {
                    debug(LiquidBounce.CLIENT_NAME + "c0f added")
                    transactions.add(packet)
                    event.cancelEvent()
                }
            }
        }
        @EventTarget
        fun onUpdate() {
            when (modeValue.get().toLowerCase()) {
                "hytspartan" -> {
                    if (msTimer.hasTimePassed(3000L) && keepAlives.size > 0 && transactions.size > 0) {
                        PacketUtils.sendPacketNoEvent(keepAlives[keepAlives.size - 1])
                        PacketUtils.sendPacketNoEvent(transactions[transactions.size - 1])

                        debug(LiquidBounce.CLIENT_NAME + "c00 no.${keepAlives.size - 1} sent.")
                        debug(LiquidBounce.CLIENT_NAME + "c0f no.${transactions.size - 1} sent.")
                        keepAlives.clear()
                        transactions.clear()
                        msTimer.reset()
                    }
                }
            }
        }
    }
}

