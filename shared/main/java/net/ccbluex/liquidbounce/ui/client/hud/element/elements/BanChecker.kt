package net.ccbluex.liquidbounce.ui.client.hud.element.elements


import me.utils.CustomUI
import me.utils.render.ShadowUtils
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders

import net.ccbluex.liquidbounce.utils.render.RenderUtils

import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "BanChecker")
class BanChecker () : Element() {


    override fun drawElement(): Border {
        var width = 105f
        var height = 72f

            RenderUtils.drawRoundedRect(
                0f,
                0f,
                width,
                height,
                CustomUI.radius.get().toInt(),
                Color(CustomUI.r.get(), CustomUI.g.get(), CustomUI.b.get(), CustomUI.a.get()).rgb
            )
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glScalef( 1F,  1F,  1F)
            GL11.glPushMatrix()
            ShadowUtils.shadow(CustomUI.shadowValue.get(),{
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                GL11.glScalef(scale, scale, scale)
                RenderUtils.drawRoundedRect(0f,0f,width,height , CustomUI.radius.get().toInt(), Color(0,0,0).rgb)
                GL11.glPopMatrix()

            },{
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                GL11.glScalef(scale, scale, scale)
                GlStateManager.enableBlend()
                GlStateManager.disableTexture2D()
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                RenderUtils.drawRoundedRect(0f,0f,width,height , CustomUI.radius.get().toInt(), Color(0,0,0).rgb)
                GlStateManager.enableTexture2D()
                GlStateManager.disableBlend()
                GL11.glPopMatrix()
            })
            GL11.glPopMatrix()
            GL11.glScalef(scale, scale, scale)
            GL11.glTranslated(renderX, renderY, 0.0)



            RenderUtils.drawRoundedRect(
                0f,
                0f,
                width,
                height,
                CustomUI.radius.get().toInt(),
                Color(CustomUI.r.get(), CustomUI.g.get(), CustomUI.b.get(), CustomUI.a.get()).rgb
            )


        FontLoaders.F16.drawStringWithShadow("封禁检测",6f, 5f,Color.WHITE.rgb)
        FontLoaders.F16.drawStringWithShadow("封禁次数: " + Recorder.ban,6f, 7f + FontLoaders.F16.FONT_HEIGHT,Color.WHITE.rgb)
        FontLoaders.F16.drawStringWithShadow("获胜次数: " + Recorder.win,6f, 7f + FontLoaders.F16.FONT_HEIGHT * 2,Color.WHITE.rgb)
        FontLoaders.F16.drawStringWithShadow("击杀次数: " + Recorder.killCounts,6f, 7f + FontLoaders.F16.FONT_HEIGHT *3,Color.WHITE.rgb)

        return Border(0f,0f,width,height)
    }
}