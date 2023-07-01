/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import com.mojang.realmsclient.gui.ChatFormatting
import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.Colors
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
public class HUD : Module() {

    private val toggleMessageValue = BoolValue("DisplayToggleMessage", true)
    private val toggleSoundValue = ListValue("ToggleSound", arrayOf("None", "Default", "Custom"), "Custom")
    companion object {
        @JvmField
        val Hotbarblur = BoolValue("BlurGuiButton", false)
     val shadowValue = ListValue(
        "ShadowMode", arrayOf(
             "Test",
            "LiquidBounce",
            "Outline",
            "Default",
            "Autumn"
        ), "LiquidBounce"
    )}
    val hueInterpolation = BoolValue("Hue Interpolate", false)

    val containerBackground = BoolValue("Container-Background", false)
    val animHotbarValue = BoolValue("AnimatedHotbar", true)
    val blackHotbarValue = BoolValue("BlackHotbar", true)


    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val Radius = IntegerValue("BlurRadius", 10 , 1 , 50 )

    val fontChatValue = BoolValue("FontChat", false)
    val logValue = ListValue("LogMode", arrayOf("None", "Liquid","Apollo"), "None")





    private var hotBarX = 0F
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        val sr = ScaledResolution(mc2)
        LiquidBounce.hud.render(false,0,0)


        when (logValue.get().toLowerCase()) {

            "apollo"->{
                val time = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
                RenderUtils.drawOutlinedRect(2f,
                    5.5f,FontLoaders.NL24.getStringWidth("Liquidbounce | $time ")+7.5f,FontLoaders.NL24.FONT_HEIGHT.toFloat(),1,ColorUtils.fade(Color(255,255,255,255),999,5),Color.black)
                //RenderUtils.drawRect(2f,5.5f,FontLoaders.NL24.getStringWidth("LiquidBounce | $time")+4f,FontLoaders.NL24.FONT_HEIGHT-0.8f,Color.black.rgb)
                FontLoaders.NL24.drawString(
                    "LiquidBounce",
                    6.5f,
                    7.5f,
                    ColorUtils.fade(Color(204,50,25,255),999,1).rgb,
                    true
                )
                FontLoaders.NL24.drawString(
                    "LiquidBounce | $time",
                    6f,
                    7f,
                    Color(255,255,255,255).rgb,
                    false
                )


            }
            "liquid" -> {
                val time = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)

                RenderUtils.quickDrawBorderedRect(2f,3.5f,FontLoaders.NL35.height+1f,FontLoaders.NL35.FONT_HEIGHT+0f,10f,Color.black.rgb,Color.black.rgb)
                FontLoaders.NL35.drawString(
                        "LiquidBounce | $time",
                        3.5f,
                        4f,
                        Color(255,255,255,255).rgb,
                        true
                )

                FontLoaders.NL35.drawString(
                    "LiquidBounce",
                    4f,
                    4.5f,
                    Color(0,50,255,255).rgb,
                    true
                )
            }


        }
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return

//        LiquidBounce.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }
    @EventTarget(ignoreCondition = true)
    fun onTick(event: TickEvent) {

        LiquidBounce.moduleManager.shouldNotify = toggleMessageValue.get()
        LiquidBounce.moduleManager.toggleSoundMode = toggleSoundValue.values.indexOf(toggleSoundValue.get())
    }
    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }
    fun getAnimPos(pos: Float): Float {
        if (state && animHotbarValue.get()) hotBarX = net.ccbluex.liquidbounce.utils.AnimationUtils.animate(pos, hotBarX, 0.02F * RenderUtils.deltaTime.toFloat())
        else hotBarX = pos

        return hotBarX
    }

    init {
        state = true
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
                !(classProvider.isGuiChat(event.guiScreen) || classProvider.isGuiHudDesigner(event.guiScreen))) mc.entityRenderer.loadShader(classProvider.createResourceLocation("liquidbounce/blur.json")) else if (mc.entityRenderer.shaderGroup != null &&
                mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("liquidbounce/blur.json")) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }


}