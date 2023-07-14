/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FontValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import org.lwjgl.opengl.GL11

/**
 * CustomHUD effects element
 *
 * Shows a list of active potion effects
 */
@ElementInfo(name = "Effects")
class Effects(x: Double = 2.0, y: Double = 10.0, scale: Float = 1F,
              side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    private val fontValue = Fonts.minecraftFont
    private val shadow = BoolValue("Shadow", true)

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        var y = 0F
        var width = 0F

        val fontRenderer = fontValue

        assumeNonVolatile = true

        for (effect in mc.thePlayer!!.activePotionEffects) {
            val potion = functions.getPotionById(effect.potionID)

            val number = when {
                effect.amplifier == 1 -> "二"
                effect.amplifier == 2 -> "三"
                effect.amplifier == 3 -> "四"
                effect.amplifier == 4 -> "五"
                effect.amplifier == 5 -> "六"
                effect.amplifier == 6 -> "七"
                effect.amplifier == 7 -> "八"
                effect.amplifier == 8 -> "九"
                effect.amplifier == 9 -> "十"
                effect.amplifier > 10 -> "十一"
                else -> "一"
            }

            val name = "${functions.formatI18n(potion.name)} $number§f: §7${effect.getDurationString()}"
            val stringWidth = fontRenderer.getStringWidth(name).toFloat()

            if (width < stringWidth)
                width = stringWidth
            if (potion.hasStatusIcon) {
                GlStateManager.pushMatrix()
                GL11.glDisable(2929)
                GL11.glEnable(3042)
                GL11.glDepthMask(false)
                OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                val statusIconIndex = potion.statusIconIndex
                mc.textureManager.bindTexture(classProvider.createResourceLocation("textures/gui/container/inventory.png"))
                mc2.ingameGUI.drawTexturedModalRect(
                    -stringWidth-20f, //X-pos
                    (y-5f ).toFloat(),                                 //Y-pos
                    statusIconIndex % 8 * 18,
                    198 + statusIconIndex / 8 * 18,
                    18,
                    18
                )
                GL11.glDepthMask(true)
                GL11.glDisable(3042)
                GL11.glEnable(2929)
                GlStateManager.popMatrix()
            }
            fontRenderer.drawString(name, -stringWidth, y, potion.liquidColor, shadow.get())
            y -= fontRenderer.fontHeight + 6f
        }

        assumeNonVolatile = false

        if (width == 0F)
            width = 40F

        if (y == 0F)
            y = -10F

        return Border(2F, fontRenderer.fontHeight.toFloat(), -width - 2F, y + fontRenderer.fontHeight - 2F)
    }
}