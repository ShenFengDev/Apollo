package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.utils.BlurBuffer
import me.utils.CustomUI
import me.utils.render.RoundedUtil
import me.utils.render.ShadowUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts

import net.ccbluex.liquidbounce.utils.render.RenderUtils

import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color


@ElementInfo(name = "KeyBinds-CN")
class KeyBinds : Element() {
    val onlyState = BoolValue("OnlyModuleState", true)
    private var anmitY = 0F
    override fun drawElement(): Border? {
        var y2 = 0
        anmitY = RenderUtils.getAnimationState2(anmitY.toDouble(),(15 + getmoduley()).toFloat().toDouble(), 250.0).toFloat()
        if (true) {
            //draw Background
            RenderUtils.drawRoundedRect(
                0f,
                0f,
                114f,
                anmitY,
                CustomUI.radius.get().toInt(),
                Color(CustomUI.r.get(), CustomUI.g.get(), CustomUI.b.get(), CustomUI.a.get()).rgb
            )
        }



        GlStateManager.resetColor()
        if (true) {
            //draw Title
            val fwidth = 10F
            FontLoaders.F16.drawString("按键显示", fwidth, 4.5f, -1, true)

            //draw Module Bind
            for (module in LiquidBounce.moduleManager.modules) {
                if (module.keyBind == 0) continue
                if (onlyState.get()) {
                    if (!module.state) continue
                }
                FontLoaders.F16.drawString(module.name, fwidth, y2 + 19f, -1, true)
                FontLoaders.F16.drawString(
                    if (module.state) "[开启]" else "[关闭]",
                    (108 - FontLoaders.F16.getStringWidth(if (module.state) "[开启]" else "[关闭]")).toFloat(),
                    y2 + 21f,
                    if (module.state) Color(255, 255, 255).rgb else Color(255, 255, 255).rgb,
                    true
                )
                y2 += 10
            }
        }
        return Border(0f, 0f, 114f, (17 + getmoduley()).toFloat())
    }

    fun getmoduley(): Int {
        var y = 0
        for (module in LiquidBounce.moduleManager.modules) {
            if (module.keyBind == 0) continue
            if (onlyState.get()) {
                if (!module.state) continue
            }
            y += 12
        }
        return y
    }
}