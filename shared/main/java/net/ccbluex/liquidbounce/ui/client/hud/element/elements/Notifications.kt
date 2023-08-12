
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

//import cn.liying.Tfont.Fonts
//import cn.liying.utils.RoundedUtil
import me.utils.render.RoundedUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiScreen
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.EaseUtils
//import net.ccbluex.liquidbounce.utils.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max


/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 2.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {
    /**
     * Example notification for CustomHUD designer
     */

    private val exampleNotification = Notification("Notification", "This is example", NotifyType.INFO)
    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val notifications = mutableListOf<Notification>()
        //FUCK YOU java.util.ConcurrentModificationException
        for((index, notify) in LiquidBounce.hud.notifications.withIndex()){
            GL11.glPushMatrix()

            if(notify.drawNotification(index,this.renderX.toFloat(), this.renderY.toFloat(),scale)){
                notifications.add(notify)
            }

            GL11.glPopMatrix()
        }
        for(notify in notifications){
            LiquidBounce.hud.notifications.remove(notify)
        }
        if(classProvider.isGuiHudDesigner(mc.currentScreen)){
            if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                LiquidBounce.hud.addNotification(exampleNotification)
        }

        if (mc.currentScreen is IGuiScreen) {


            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()
//            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(),0F,0F)
        }

        return null
    }


}


class Notification(val title: String, val content: String, val type: NotifyType, val time: Int=1000, val animeTime: Int=350) {
    private var s: String? = null
    var n2: Int = Fonts.font35.getStringWidth(content)
    var textLength = Math.max(n2 as Int, 0 as Int)
    val width = this.textLength + 12
    val height=30
    var fadeState = FadeState.IN
    var nowY=-height
    var displayTime=System.currentTimeMillis()
    var animeXTime=System.currentTimeMillis()
    var x = 0F
    // RoundedUtil.drawRound(10F,0F,132F,28F,4.5F, Color(41 ,150, 43,150))
    //            Fonts.font40.drawString("o",15F,3F,Color(90 ,239, 87,240).rgb)


    var animeYTime=System.currentTimeMillis()



    /**
     * Draw notification
     */
    fun drawNotification(index: Int, blurRadius: Float, y: Float, scale: Float):Boolean {

        val realY=-(index+1) * (height + 2)


        val nowTime=System.currentTimeMillis()

        var transY=nowY.toDouble()
        //Y-Axis Animation
        if(nowY!=realY){
            var pct=(nowTime-animeYTime)/animeTime.toDouble()
            if(pct>1){
                nowY=realY
                pct=1.0
            }else{
                pct= EaseUtils.easeOutQuart(pct)
            }
            GL11.glTranslated(1.0,(realY-nowY)*pct,0.0)
        }else{
            animeYTime=nowTime

        }
        GL11.glTranslated(1.0,nowY.toDouble(),0.0)

        //X-Axis Animation
        var pct=(nowTime-animeXTime)/animeTime.toDouble()
        when(fadeState){
            FadeState.IN -> {
                if(pct>1){
                    fadeState= FadeState.STAY
                    animeXTime=nowTime
                    pct=3.0
                }
                pct= EaseUtils.easeOutQuart(pct)
                transY+=(realY-nowY)*pct
            }

            FadeState.STAY -> {
                pct=1.0
                if(nowTime-animeXTime >time){
                    fadeState= FadeState.OUT
                    animeXTime=nowTime
                }
            }

            FadeState.OUT -> {
                if(pct>1){
                    fadeState= FadeState.END
                    animeXTime=nowTime
                    pct=1.0
                }
                pct=1- EaseUtils.easeInQuart(pct)
            }

            FadeState.END -> {
                return true
            }
        }

        GL11.glTranslated(width-(width*pct),0.0,0.0)
        GL11.glTranslatef(-width.toFloat(),0F,0F)

        //draw notify
//        GL11.glPushMatrix()
//        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        var string = ""

        if (type.toString() == "SUCCESS") {
            string = "o"

        }
        if (type.toString() == "ERROR") {
            string = "p"

        }
        if (type.toString() == "WARNING") {
            string = "r"

        }
        if (type.toString() == "INFO") {
            string = "m"

        }


        // RenderUtils.drawShadow(-22, 0, width + 22, height)
        //RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), Color(255, 255, 255, 225))
        //RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), Color(0, 0, 0, 150))
        RoundedUtil.drawRound(-22f,0f,width.toFloat()+20f,height.toFloat()-10f,2f,Color(255, 255, 255, 225))

        //Fonts.font35.drawString(title, 7F, 5F, Color(0, 0, 0, 225).rgb)
        Fonts.font30.drawString(content, 7F, 8F, Color(0, 0, 0, 225).rgb)
        //RenderUtils.drawImage(image, -19,  6, 22, 22)
        if (type.toString() == "SUCCESS") {
            Fonts.Noti80.drawString(string, -18, 3, Color(0,255,80,225).rgb)
        }
        if (type.toString() == "ERROR") {
            Fonts.Noti80.drawString(string, -18, 3, Color(255,0,40,225).rgb)
        }
        if (type.toString() == "WARNING") {
            Fonts.Noti80.drawString(string, -18, 3, Color(255,255,40,225).rgb)
        }
        if (type.toString() == "INFO") {
            Fonts.Noti80.drawString(string, -18, 3, Color(205,205,205,180).rgb)
        }
        //RenderUtils.drawShadowWithCustomAlpha(38F,0F,width - 50F,28F,255f)
        //RoundedUtil.drawRound(38F,0F,width - 50F,28F,4.5F, Color(192,192,192,100))
        //Fonts.tenacitycheck60.drawString("m",42F,9F,Color(192,192,192,150).rgb)
        //Fonts.fontSFUI40.drawString(title,60F,3f,Color.white.rgb)
        //Fonts.font30.drawString(content,60f,16f,Color.white.rgb)




        return false
    }
}

enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(90 ,239, 87,180 )),
    ERROR(Color(206,33,33,190)),
    WARNING(Color(0xF5FD00)),
    INFO(Color(107,255,107,90));
}


enum class FadeState { IN, STAY, OUT, END }
