package net.ccbluex.liquidbounce.ui.client.hud.element.elements
//Coarse_KK

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder.killCounts
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*


@ElementInfo(name = "GameInfo")
class GameInfo2(x: Double = 5.0, y: Double = 87.0, scale: Float = 1F) : Element(x, y, scale) {

    private val redValue = IntegerValue("BackgroundRed", 0, 0, 255)
    private val greenValue = IntegerValue("BackgroundGreen", 0, 0, 255)
    private val blueValue = IntegerValue("BackgroundBlue", 0, 0, 255)
    private val alpha = IntegerValue("BackgroundAlpha", 0, 0, 255)
    private val rredValue = IntegerValue("RectRed", 0, 0, 255)
    private val rgreenValue = IntegerValue("RectGreen", 0, 0, 255)
    private val rblueValue = IntegerValue("RectBlue", 0, 0, 255)
    private val ralpha = IntegerValue("RectAlpha", 192,0, 255)
    private val textredValue = IntegerValue("TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("TextGreen", 244, 0, 255)
    private val textblueValue = IntegerValue("TextBlue", 255, 0, 255)
    private val shadowValue = BoolValue("Shadow", true)

    private var GameInfoRows = 0
    private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
    var aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura?
    var target = aura!!.target

    /**
     * Draw element
     */


    override fun drawElement(): Border {
        val icon = Fonts.flux
        val color = Color.WHITE.rgb
        val fontHeight = Fonts.font40.fontHeight
        if(shadowValue.get()){
            RenderUtils.drawShadowWithCustomAlpha(0F, 10.5F, 150F, 70F, 200F)
        }
        RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 20F, 150F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb)
        icon.drawString("c", 3F, 2.5F + fontHeight + 6F, color)
        icon.drawString("m", 3F, 15.9F + fontHeight + 6F, color)
        icon.drawString("f", 3F, 28.5F + fontHeight + 6F, color)
        icon.drawString("a", 3F, 39.5F + fontHeight + 6F, color)
        icon.drawString("x", 3F, 52F + fontHeight + 6F, color)
        FontLoaders.F24.drawStringWithShadow("游戏信息", (5F + icon.getStringWidth("u")).toInt().toDouble(),
            (this.GameInfoRows * 18F + 16).toInt().toDouble(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        FontLoaders.F16.drawStringWithShadow("延迟:" + EntityUtils.getPing(mc2.player).toString(),
            (5F + icon.getStringWidth("b")).toInt().toDouble(),
            (this.GameInfoRows * 18F + 30).toInt().toDouble(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        FontLoaders.F16.drawStringWithShadow("帧数: " + Minecraft.getDebugFPS(),
            (5F + icon.getStringWidth("e")).toInt().toDouble(),
            (this.GameInfoRows * 18F + 43).toInt().toDouble(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        FontLoaders.F16.drawStringWithShadow("击杀: " +killCounts, (5F + icon.getStringWidth("G")).toInt().toDouble(),
            (this.GameInfoRows * 18F + 54).toInt().toDouble(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        FontLoaders.F16.drawStringWithShadow("游戏时间: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}" ,
            (5F + icon.getStringWidth("G")).toInt().toDouble(),
            (this.GameInfoRows * 18F + 66).toInt().toDouble(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)

        return Border(0F, this.GameInfoRows * 18F + 12F, 150F, 80F)
    }
    fun calculateBPS(): Double {
        if(mc.thePlayer != null) {
            val bps = Math.hypot(
                mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX,
                mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ
            ) * mc.timer.timerSpeed * 20
            return Math.round(bps * 100.0) / 100.0
        }else{
            return 0.00;
        }

    }




}
