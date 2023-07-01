package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.SPacketChat

@ModuleInfo(name = "AutoGG", category = ModuleCategory.PLAYER, description = "idk")
class AutoGG : Module() {
    private val textValue = TextValue("ClientName", "LiquidBounce")
    var totalPlayed = 0
    var win = 0

    val Scaffold = LiquidBounce.moduleManager[Scaffold::class.java] as Scaffold

    val ChestStealer = LiquidBounce.moduleManager[ChestStealer::class.java] as ChestStealer
    val ChestAura = LiquidBounce.moduleManager[ChestAura::class.java] as ChestAura

    val KillAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
    val InventoryCleaner = LiquidBounce.moduleManager[InventoryCleaner::class.java] as InventoryCleaner
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()

        if (packet is SPacketChat) {
            val text = packet.chatComponent.unformattedText

            if (text.contains("恭喜", true) && !text.contains(":", true)) {
                mc.thePlayer!!.sendChatMessage("@["+textValue.get()+"]GG")
                win++
                InventoryCleaner.state = false
                Scaffold.state = false

                KillAura.state = false
                ChestAura.state = false
                ChestStealer.state = false
            }
            if (text.contains("游戏开始", true) && !text.contains(":", true)) {
                totalPlayed++
                mc.thePlayer!!.sendChatMessage("@我正在使用"+textValue.get())
            }
        }
    }
    override val tag: String
        get() = "HuaYuTing"
}