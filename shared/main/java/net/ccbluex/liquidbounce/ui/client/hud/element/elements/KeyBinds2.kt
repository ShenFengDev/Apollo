package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.utils.BlurBuffer
import me.utils.CustomUI
import me.utils.render.RoundedUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.renderer.GlStateManager
import me.utils.render.ShadowUtils
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders


import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font


@ElementInfo(name = "KeyBinds-New")
class KeyBinds2 : Element() {
    private var anmitY = 0F
    override fun drawElement(): Border? {
        var y2 = 0
        anmitY = RenderUtils.getAnimationState2(anmitY.toDouble(),(getmoduley()).toFloat().toDouble(), 350.0).toFloat()
        GL11.glTranslated(-renderX, -renderY, 0.0)
        GL11.glScalef(1F, 1F, 1F)
        GL11.glPushMatrix()
        ShadowUtils.shadow(10F,{
            GL11.glPushMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
            GL11.glScalef(scale, scale, scale)
            RenderUtils.drawRoundedRect(0F,0F,144F,anmitY,5,Color(0 ,0 ,0,255).rgb)
            GL11.glPopMatrix()
        }, {
            GL11.glPushMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
            GL11.glScalef(scale, scale, scale)
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            RenderUtils.fastRoundedRect(0F,0F,144F,anmitY,5F)
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GL11.glPopMatrix()
        })
        GL11.glPopMatrix()
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslated(renderX, renderY, 0.0)


        //draw Background
        RenderUtils.drawRoundedRect(0F,0F,144F,anmitY,5,Color(255 ,247 ,230,170).rgb)

        GL11.glTranslated(-renderX, -renderY, 0.0)
        GL11.glPushMatrix()
        BlurBuffer.CustomBlurRoundArea(renderX.toFloat(), renderY.toFloat()  ,144F, anmitY ,
            5F, CustomUI.blurValue.get()
        )
        GL11.glPopMatrix()
        GL11.glTranslated(renderX, renderY, 0.0)

        GL11.glTranslated(-renderX, -renderY, 0.0)
        GL11.glPushMatrix()
        RoundedUtil.drawRoundOutline(
            renderX.toFloat(),
            renderY.toFloat(),
            144f,
            anmitY,
            5F,
            0.1F,
            Color(0,0,0,0),
            Color(255,255,255,110)
        )
        GL11.glPopMatrix()
        GL11.glTranslated(renderX, renderY, 0.0)





        //draw Title
        val fwidth = 10F

        //draw Module Bind
        for (module in LiquidBounce.moduleManager.modules) {
            if (module.keyBind == 0) continue

            FontLoaders.F16.drawString(module.name, fwidth, y2+5F, if (module.state) Color(0, 0, 0).rgb else Color(90,90,90).rgb, false)
            FontLoaders.F16.drawString(
                if (module.state) "[开启]" else "[关闭]",
                (144 - FontLoaders.F16.getStringWidth(if (module.state) "[Toggle]" else "[Toggle]") -10).toFloat(),
                y2+4.5F,
                if (module.state) Color(0, 0, 0).rgb else Color(90,90,90).rgb,
                false
            )
            y2 += 12
        }

        return Border(0f, 0f, 114f, (17 + getmoduley()).toFloat())
    }

    fun getmoduley(): Int {
        var y = 0
        for (module in LiquidBounce.moduleManager.modules) {
            if (module.keyBind == 0) continue

            y += 13
        }
        return y
    }
}