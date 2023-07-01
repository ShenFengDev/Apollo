/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.font.Fonts

class SaveConfigCommand : Command("saveconfig", "configsave") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Saving...")
        LiquidBounce.fileManager.saveAllConfigs()
        chat("Saved.")
    }
}
