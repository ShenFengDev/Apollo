package net.ccbluex.liquidbounce.features.module.modules.movement

//Thx LiquidBounce
//只在花雨庭空岛战争工作

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlockIntersects
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import kotlin.math.cos
import kotlin.math.sin


@ModuleInfo(name = "AutoSkyWars", category = ModuleCategory.HYT, description = "space.bilibili.com/500398541")
class AutoSkyWars : Module() {

    private val modeValue = ListValue("mode", arrayOf(
        "firework",
        "fly"

    ), "firework")
    private val X = IntegerValue("posX", 0, -100, 100)
    private val Y = IntegerValue("posY", 10, 0, 50)
    private val Z = IntegerValue("posZ", 0, -100, 100)
    private val centerValue = BoolValue("AimCenter", false)
    //private val TeleportX = IntegerValue("Teleport posX", 0, -100, 100)
    //private val TeleportY = IntegerValue("Teleport posY", 150, 0, 256)
    //private val TeleportZ = IntegerValue("Teleport posZ", 0, -100, 100)
    private val vanillaSpeedValue = FloatValue("FlySpeed", 4f, 0f, 10f)
    private val Times = IntegerValue("TpTicks", 10, 0, 100)
    private var TPtimes = Times.get()


    //@EventTarget
    //fun onPacket(event: PacketEvent) {
    //    val packet = event.packet
    //    val thePlayer = mc.thePlayer!!
    //   val TpX =  TeleportX.get().toDouble()
    //   val TpY =  TeleportY.get().toDouble()
    //   val TpZ =  TeleportZ.get().toDouble()
    //   val packetPlayer = packet.asCPacketPlayer()
    //   if (thePlayer.capabilities.isFlying) {
    //     when (modeValue.get().toLowerCase()) {Caiji
    //     "Teleport" -> {                                                                     /////////
    //           when(TPtimes !== 0){                                                       //                                  //          //      //
    //              true -> {                                                              //                                    //          //      //
    //                        ClientUtils.displayChatMessage("[修改空岛出生点]芜湖!起飞！！")//                      //////
    //                        thePlayer.setPositionAndRotation(                         //                     //     ////      ///         ///     ///
    //                            thePlayer.posX + X.get(),                               //                 //         ///     ///         ///     ///
    //                            thePlayer.posY + Y.get(),                               //                 //          //     ///         ///     ///
    //                            thePlayer.posZ + Z.get(),                                 //          ///   //       ////     ///         ///     ///
    //                            thePlayer.rotationYaw,                                      /////////         ////////  //    ///         ///     ///
    ////////////                           thePlayer.rotationPitch                                                                          ///
    //      )                                                                                                                         //    ///
    //      TPtimes -= 1                                                                                                                 ///
    //   }
    // else -> {
    //   ClientUtils.displayChatMessage("[修改空岛出生点]TP......")
    // packetPlayer.x = TpX
    //   packetPlayer.y = TpY
    //                 packetPlayer.z = TpZ
    //                }
    //          }
    //    }
    //}
    // }
    //}

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val AutoSkyWars = LiquidBounce.moduleManager.getModule(AutoSkyWars::class.java) as AutoSkyWars
        if (AutoSkyWars.state){
            val vanillaSpeed = vanillaSpeedValue.get()
            val thePlayer = mc.thePlayer!!
            if (thePlayer.capabilities.isFlying) {
                when (modeValue.get().toLowerCase()) {
                    "firework" -> {//像烟花一样起飞
                        ClientUtils.displayChatMessage("[修改空岛出生点]火箭发射器ヾ(✿ﾟ▽ﾟ)ノ")
                        thePlayer.setPositionAndRotation(
                            thePlayer.posX + X.get(),
                            thePlayer.posY + Y.get(),
                            thePlayer.posZ + Z.get(),
                            thePlayer.rotationYaw,
                            thePlayer.rotationPitch
                        )
                    }
                    "fly" -> {//飞行
                        thePlayer.motionY = 0.0
                        thePlayer.motionX = 0.0
                        thePlayer.motionZ = 0.0
                        if (mc.gameSettings.keyBindJump.isKeyDown) thePlayer.motionY += vanillaSpeed
                        if (mc.gameSettings.keyBindSneak.isKeyDown) thePlayer.motionY -= vanillaSpeed
                        MovementUtils.strafe(vanillaSpeed)
                        if(TPtimes !== 0){
                            ClientUtils.displayChatMessage("[修改空岛出生点]芜湖!起飞！！")
                            thePlayer.setPositionAndRotation(
                                thePlayer.posX + X.get(),
                                thePlayer.posY + Y.get(),
                                thePlayer.posZ + Z.get(),
                                thePlayer.rotationYaw,
                                thePlayer.rotationPitch
                            )
                            TPtimes -= 1
                        }
                    }

                }
            } else {
                val thePlayer = mc.thePlayer ?: return
                thePlayer.capabilities.isFlying = false
                mc.timer.timerSpeed = 1f
                thePlayer.speedInAir = 0.02f
                TPtimes = Times.get()
            }
        }else{
            return
        }
    }
    override fun handleEvents() = true
    override val tag: String
        get() = modeValue.get()
    override fun onDisable() {
        val thePlayer = mc.thePlayer!!
        thePlayer.capabilities.isFlying = false
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindForward.pressed = false

    }
}


//注释

//注释

//注释 作者：菜级玩家 https://www.bilibili.com/read/cv25755876?spm_id_from=333.999.list.card_opus.click 出处：bilibili