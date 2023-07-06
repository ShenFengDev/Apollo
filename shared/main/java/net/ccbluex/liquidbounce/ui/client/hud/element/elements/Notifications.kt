package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.math.BigDecimal

/**cal
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", single = true)
class Notifications(
    x: Double = 143.0, y: Double = 30.0, scale: Float = 1.05F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {
    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Notification", "", NotifyType.INFO)


    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val notifications = mutableListOf<Notification>()
        //FUCK YOU java.util.ConcurrentModificationException
        for ((index, notify) in LiquidBounce.hud.notifications.withIndex()) {
            GL11.glPushMatrix()

            if (notify.drawNotification(index, this)) {
                notifications.add(notify)
            }
            GL11.glPopMatrix()
        }

            if (!LiquidBounce.hud.notifications.contains(exampleNotification)) {
                LiquidBounce.hud.addNotification(exampleNotification)
            }
        for (notify in notifications) {
            LiquidBounce.hud.notifications.remove(notify)
        }
        //val Notification = LiquidBounce.hud.notifications


        if (classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                LiquidBounce.hud.addNotification(exampleNotification)

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()
            //            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(
                -exampleNotification.width.toFloat() + 80,
                -exampleNotification.height.toFloat() - 24.5f,
                80F,
                -24.5F
            )
        }

        return null
    }

}

class Notification(
    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 2000,
    val animeTime: Int = 500
) {
    val height = 30
    var fadeState = FadeState.IN
    var nowY = -height
    var string = ""
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var animeYTime = System.currentTimeMillis()
    val width = Fonts.font30.getStringWidth(content) + 53



    /**
     * Draw notification
     */
    fun drawNotification(index: Int, noti: Notifications): Boolean {
        val realY = -(index + 1) * (height + 10)
        val nowTime = System.currentTimeMillis()
        //Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutBack(pct)
            }
            GL11.glTranslated(0.0, (realY - nowY) * pct, 0.0)
        } else {
            animeYTime = nowTime
        }
        GL11.glTranslated(0.0, nowY.toDouble(), 0.0)

        //X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutBack(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInBack(pct)
            }

            FadeState.END -> {
                return true
            }
        }

        var string1 = ""

        if (type.toString() == "SUCCESS") {
            string = "a"
            string1 = "o"
        }
        if (type.toString() == "ERROR") {
            string = "B"
            string1 = "p"
        }
        if (type.toString() == "WARNING") {
            string = "D"
            string1 = "r"
        }
        if (type.toString() == "INFO") {
            string = "C"
            string1 = "m"
        }

        val renderX = noti.renderX
        val renderY = noti.renderY



                GL11.glScaled(pct, pct, pct)
                GL11.glTranslatef(-width.toFloat() / 2, -height.toFloat() / 2, 0F)
                RenderUtils.drawRect(0F, 0F, width.toFloat(), height.toFloat(), Color(255, 255, 255, 240))


                Fonts.font25.drawString(title, 32F.toInt(), 5, Color.BLACK.rgb)
                Fonts.font40.drawString(content, 24.5F.toInt(), 5+Fonts.font40.fontHeight, Color.BLACK.rgb)
                if (type.toString() == "SUCCESS") {
                Fonts.Noti80.drawString(string, 4, 8, Color(0,255,40,225).rgb)
                }
                if (type.toString() == "ERROR") {
                Fonts.Noti80.drawString(string, 4, 8, Color(255,0,40,225).rgb)
                }
                if (type.toString() == "WARNING") {
                Fonts.Noti80.drawString(string, 4, 8, Color(255,255,40,225).rgb)
                }
                if (type.toString() == "INFO") {
                Fonts.Noti80.drawString(string, 4, 8, Color(255,255,255,95).rgb)
                }


                GlStateManager.resetColor()




        return false
    }
}

enum class NotifyType(var icon: String) {
    SUCCESS("check-circle"),
    ERROR("close-circle"),
    WARNING("warning"),
    INFO("information");
}


enum class FadeState { IN, STAY, OUT, END }


