/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Horizontal
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Vertical
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 *
 * by 吸尘器
 *
 * Skid By WaWa
 */
@ElementInfo(name = "VAPEArraylist", single = true)
class VAPEArraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                    side: Side = Side(Horizontal.RIGHT, Vertical.UP)) : Element(x, y, scale, side) {
    private val colorRedValue = IntegerValue("R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("G", 111, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val imageValue = BoolValue("Logo",true)
    private val backgroundValue = BoolValue("Background",true)
    private val shadow = BoolValue("ShadowText", true)
    private val spaceValue = FloatValue("Space", 0F, 0F, 5F)
    private val textHeightValue = FloatValue("TextHeight", 11F, 1F, 20F)
    private val textYValue = FloatValue("TextY", 1F, 0F, 20F)

    private var x2 = 0
    private var y2 = 0F

    private var modules = emptyList<Module>()


    override fun drawElement(): Border? {
        val fontRenderer = FontLoaders.SB18

        AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        for (module in LiquidBounce.moduleManager.modules) {
            if (!module.array || (!module.state && module.slide == 0F)) continue

            val displayString = module.name

            val width = fontRenderer.getStringWidth(displayString)

            if (module.state) {
                if (module.slide < width) {
                    module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                    module.slideStep += delta / 4F
                }
            } else if (module.slide > 0) {
                module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                module.slideStep -= delta / 4F
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        // Draw arraylist
        val space = spaceValue.get()
        val textHeight = textHeightValue.get()
        val textY = textYValue.get()
        val backgroundCustomColor = Color(0, 0, 0, 100).rgb
        val textShadow = shadow.get()
        val textSpacer = textHeight + space
        val customColor = Color(colorRedValue.get(),colorGreenValue.get(),colorBlueValue.get(),255)

                modules.forEachIndexed { index, module ->
                    val displayString = module.name

                    val xPos = -module.slide - 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index

                    val size = modules.size * 2.0E-2f
                    if (module.state) {
                        if (module.higt < yPos) {
                            module.higt += (size -
                                    (module.higt * 0.002f).coerceAtMost(size - (module.higt * 0.0001f))) * delta
                            module.higt = yPos.coerceAtMost(module.higt)
                        } else {
                            module.higt -= (size -
                                    (module.higt * 0.002f).coerceAtMost(size - (module.higt * 0.0001f))) * delta
                            module.higt = module.higt.coerceAtLeast(yPos)
                        }
                    }
                    if (backgroundValue.get()) {
                        RenderUtils.drawRect(
                                xPos - if (backgroundValue.get()) 5 else 2,
                                module.higt,
                                if (backgroundValue.get()) -3F else 0F,
                                module.higt + textHeight,
                                backgroundCustomColor
                        )
                        RenderUtils.drawRect(-3F, module.higt, 1F,module.higt + textHeight, customColor)
                    }
                    fontRenderer.drawString(displayString, xPos, module.higt + textY,customColor.rgb, textShadow)
                }
        if (imageValue.get()){
            FontLoaders.F22.drawString(
                "LiquidBounce"
                ,-57,-11,
                Color(colorRedValue.get(),colorGreenValue.get(),colorBlueValue.get()).rgb,
                true)
        }


        // Draw border
        if (classProvider.isGuiHudDesigner(mc.currentScreen)) {
            x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return Border(0F, -1F, -20F, 20F)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Horizontal.RIGHT, Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 2
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                }
            }
            y2 = textSpacer * modules.size

            return Border(0F, 0F, x2 - 7F, y2 - if (side.vertical == Vertical.DOWN) 1F else 0F)
        }

        AWTFontRenderer.assumeNonVolatile = false
        GlStateManager.resetColor()
        return null
    }

    override fun updateElement() {
        modules = LiquidBounce.moduleManager.modules
                .filter { it.array && it.slide > 0 }
                .sortedBy { -FontLoaders.SB18.getStringWidth("VAPE") }
    }
}