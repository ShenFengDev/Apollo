package net.ccbluex.liquidbounce.features.module.modules.movement

//Thx LiquidBounce
//只在花雨庭空岛战争工作

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue


@ModuleInfo(name = "AutoSkyWars", category = ModuleCategory.HYT, description = "space.bilibili.com/500398541")
class AutoSkyWars : Module() {

    private val modeValue = ListValue("mode", arrayOf(
        "firework",
        "fly",
        "flyaim",
        "teleport"//用不了你可以不用
    ), "flyaim")
    private val X = IntegerValue("posX", 0, -100, 100)
    private val Y = IntegerValue("posY", 10, 0, 50)
    private val Z = IntegerValue("posZ", 0, -100, 100)
    private var SilentNoChat = BoolValue("SilentNoChat",false)
    //private val TeleportX = IntegerValue("Teleport posX", 0, -100, 100)
    //private val TeleportY = IntegerValue("Teleport posY", 4, 0, 5)
    //private val TeleportZ = IntegerValue("Teleport posZ", 0, -100, 100)
    private val vanillaSpeedValue = FloatValue("FlySpeed", 4f, 0f, 10f)
    private val Times = IntegerValue("TpTicks", 10, 0, 100)
    private var TPtimes = Times.get()
    private val rangeValue = FloatValue("AimRange", 114514F, 1F, 114514F)
    private val centerValue = BoolValue("Center", false)
    private val lockValue = BoolValue("Lock", true)
    private val turnSpeedValue = FloatValue("TurnSpeed", 360F, 360F, 114514F)
    private val height = IntegerValue("Teleportheight", 190, 0, 256)



    //废弃代码
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
    fun onUpdate(event: UpdateEvent) {
        val autoSkyWars = LiquidBounce.moduleManager.getModule(AutoSkyWars::class.java) as AutoSkyWars
        if (autoSkyWars.state){
            val vanillaSpeed = vanillaSpeedValue.get()
            val thePlayer = mc.thePlayer!!
            if (thePlayer.capabilities.isFlying) {
                when (modeValue.get().toLowerCase()) {
                    "fly","flyaim","firework" -> {//飞行
                        //Silent Teleport ↓
                        if(TPtimes != 0){
                            if (!SilentNoChat.get()) {
                                ClientUtils.displayChatMessage("[修改空岛出生点]芜湖!起飞！！")
                            }
                            thePlayer.setPositionAndRotation(
                                thePlayer.posX + X.get(),
                                thePlayer.posY + Y.get(),
                                thePlayer.posZ + Z.get(),
                                thePlayer.rotationYaw,
                                thePlayer.rotationPitch
                            )
                            TPtimes -= 1
                            when (modeValue.get().toLowerCase()) {
                                "firework" -> {
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX + X.get(),
                                        thePlayer.posY + Y.get(),
                                        thePlayer.posZ + Z.get(),
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }
                            }
                        }
                        when (modeValue.get().toLowerCase()) {
                            "fly","flyaim" -> {
                                //Vanilla Fly ↓
                                thePlayer.motionY = 0.0
                                thePlayer.motionX = 0.0
                                thePlayer.motionZ = 0.0
                                if (mc.gameSettings.keyBindJump.isKeyDown) thePlayer.motionY += vanillaSpeed
                                if (mc.gameSettings.keyBindSneak.isKeyDown) thePlayer.motionY -= vanillaSpeed
                                MovementUtils.strafe(vanillaSpeed)
                                when (modeValue.get().toLowerCase()) {
                                    "flyaim" -> {
                                        mc.gameSettings.keyBindForward.pressed = true
                                    }
                                }
                            }
                        }
                    }
                    "teleport" -> {
                        if(TPtimes != 0){
                            thePlayer.setPositionAndRotation(
                                thePlayer.posX + X.get(),
                                thePlayer.posY + Y.get(),
                                thePlayer.posZ + Z.get(),
                                thePlayer.rotationYaw,
                                thePlayer.rotationPitch
                            )
                            TPtimes -= 1
                        }else{//debug ↓
                            val PlayerX = thePlayer.posX
                            val PlayerZ = thePlayer.posZ
                            val tpxtimes = PlayerX / 5//因为下面是每次TP 5格，所以这里就是自己的坐标除以5，比如是：玩家此时的X轴坐标为-100，那tpxtimes就是-20次，然后下面加-20*-5就是-100+100 = 0
                            val tpztimes = PlayerZ / 5
                            val remainZ = tpztimes / 20//计算时间，因为Update是每个ticks都，所以除20得到秒
                            val remainX = tpxtimes / 20
                            val remainY = thePlayer.posY / 20
                            ClientUtils.displayChatMessage(">>>>>>>>>>>>>>>>>>SkyWars Helper<<<<<<<<<<<<<<<<<<")
                            ClientUtils.displayChatMessage("Code by 菜级玩家(bilibili.com)")
                            ClientUtils.displayChatMessage("Only working in HuaYuTing,Thank you for using," + LiquidBounce.CLIENT_NAME)
                            ClientUtils.displayChatMessage(">>>PosX-> $PlayerX")
                            ClientUtils.displayChatMessage(">>>PosZ-> $PlayerZ")
                            if (tpxtimes.toInt() == 0){
                                ClientUtils.displayChatMessage(">>>X teleport Time remaining -> SUCCESSFUL")
                            }else{
                                ClientUtils.displayChatMessage(">>>PX teleport Time remaining -> $remainX seconds")
                            }
                            if (tpztimes.toInt() == 0){
                                ClientUtils.displayChatMessage(">>>Z teleport Time remaining -> SUCCESSFUL")
                            }else{
                                ClientUtils.displayChatMessage(">>>Z teleport Time remaining -> $remainZ seconds")
                            }
                            if (thePlayer.posY.toInt() > height.get()){
                                ClientUtils.displayChatMessage(">>>Y teleport Time remaining -> SUCCESSFUL")
                            }else{
                                ClientUtils.displayChatMessage(">>>Y teleport Time remaining -> $remainY seconds")
                            }
                            if(thePlayer.posY.toInt() < height.get()){//出玻璃用的
                                thePlayer.setPositionAndRotation(
                                    thePlayer.posX,
                                    thePlayer.posY + 4,
                                    thePlayer.posZ,
                                    thePlayer.rotationYaw,
                                    thePlayer.rotationPitch
                                )
                            }
                            if (tpxtimes.toInt() != 0) {
                                if(tpxtimes < 0){//判断tpxtimes是不是负数，是负数就负负得正，不是负数就正正得正，下面Z轴同样写法
                                    val isNegativeX = 5
                                    ClientUtils.displayChatMessage("X-> $isNegativeX")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX + isNegativeX,
                                        thePlayer.posY,
                                        thePlayer.posZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }else{
                                    val isNegativeX = -5
                                    ClientUtils.displayChatMessage("X-> $isNegativeX")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX + isNegativeX,
                                        thePlayer.posY,
                                        thePlayer.posZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }
                            }
                            if (tpztimes.toInt() != 0){
                                if(tpztimes < 0){
                                    val isNegativeZ = 5
                                    ClientUtils.displayChatMessage("Z-> $isNegativeZ")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX,
                                        thePlayer.posY,
                                        thePlayer.posZ + isNegativeZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }else{
                                    val isNegativeZ = -5
                                    ClientUtils.displayChatMessage("Z-> $isNegativeZ")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX,
                                        thePlayer.posY,
                                        thePlayer.posZ + isNegativeZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                val thePlayer = mc.thePlayer ?: return//怎么写都warn，儍閉
                thePlayer.capabilities.isFlying = false
                mc.timer.timerSpeed = 1f
                thePlayer.speedInAir = 0.02f
                TPtimes = Times.get()
            }
        }else{
            return
        }
    }
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val autoSkyWars = LiquidBounce.moduleManager.getModule(AutoSkyWars::class.java) as AutoSkyWars
        if (autoSkyWars.state) {
            val thePlayer = mc.thePlayer!!
            if (thePlayer.capabilities.isFlying) {
                when (modeValue.get().toLowerCase()) {
                    "flyaim" -> {
                        //ClientUtils.displayChatMessage("2")
                        //Aim-bot ↓
                        val thePlayer = mc.thePlayer ?: return//我**你全家都**,**idea,我***

                        val range = rangeValue.get()
                        val entity = mc.theWorld!!.loadedEntityList
                            .filter {
                                EntityUtils.isSelected(it, true) && thePlayer.canEntityBeSeen(it) &&
                                        thePlayer.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(
                                    it) <= 360
                            }
                            .minBy { RotationUtils.getRotationDifference(it) } ?: return

                        if (!lockValue.get() && RotationUtils.isFaced(entity, range.toDouble()))
                            return

                        val rotation = RotationUtils.limitAngleChange(
                            Rotation(thePlayer.rotationYaw, thePlayer.rotationPitch),
                            if (centerValue.get())
                                RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox), true)
                            else
                                RotationUtils.searchCenter(entity.entityBoundingBox, false, false, true,
                                    false, range).rotation,
                            (turnSpeedValue.get() + Math.random()).toFloat()
                        )
                        rotation.toPlayer(thePlayer)

                        //没错这部分是脑残菜级玩家东缝西合的答辩
                    }
                }
            }
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