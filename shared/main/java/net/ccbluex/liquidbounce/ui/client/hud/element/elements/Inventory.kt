
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.utils.render.RoundedUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.color.Gident
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * CustomHUD Armor element
 *
 * Shows a horizontal display of current armor
 */


@ElementInfo(name = "Inventory")
class Inventory(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {

    private val backgroundalphaValue = IntegerValue("BackGroundAlpha",0,0,255)
    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val hud = LiquidBounce.moduleManager.getModule(HUD::class.java) as HUD
        RoundedUtil.drawRound(6.0F, 33.0F, 166.0F, 60.0F, 2.5F,Color(0,0,0,backgroundalphaValue.get() ))
        mc.fontRendererObj.drawString("Inventory",6,32,Color.WHITE.rgb)

        var itemX = 10
        var itemY  = 40
        var airs = 0
        // 遍历消耗品栏，绘制物品栏图标
        for (i in 9..35) {
            val stack = mc2.player.inventory.mainInventory[i]
            if (stack.isEmpty) {
                airs++
            }
            GL11.glPushMatrix()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            if (mc.theWorld != null) RenderHelper.enableGUIStandardItemLighting()
            GlStateManager.pushMatrix()
            GlStateManager.disableAlpha()
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT)
            mc.renderItem.zLevel = -150.0f
            mc2.renderItem.renderItemAndEffectIntoGUI(stack, itemX, itemY)
            mc2.renderItem.renderItemOverlays(mc2.fontRenderer, stack, itemX, itemY)
            mc.renderItem.zLevel = 0.0f
            GlStateManager.disableBlend()
            GlStateManager.scale(0.5, 0.5, 0.5)
            GlStateManager.disableDepth()
            GlStateManager.disableLighting()
            GlStateManager.enableDepth()
            GlStateManager.scale(2.0f, 2.0f, 2.0f)
            GlStateManager.enableAlpha()
            GlStateManager.popMatrix()
            GL11.glPopMatrix()

            if (itemX < 152) {
                itemX += 18
            } else {
                itemX = 10
                itemY += 18
            }
        }

        if (airs == 27) {
            Fonts.minecraftFont.drawString("Empty...", 76, 61, Color.WHITE.rgb)
        }
        return Border(6f, 32f, 6f + 166f, 93f)
    }
}