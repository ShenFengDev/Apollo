package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue


@ModuleInfo(name = "OldHitting", description = "faq", category = ModuleCategory.RENDER, array = false)
class OldHitting : Module() {
    private val modeValue = ListValue("Mode", arrayOf("None","RedBone","MineCraft", "Reverse", "Strange","ETB", "Test", "Jello", "SigmaOld","Zoom","Push", "SideDown"), "MineCraft")

    private val onlySword = BoolValue("Only-Sword", true)

    fun getModeValue(): ListValue {
        return modeValue
    }

    fun getOnlySword(): BoolValue {
        return onlySword
    }

    companion object {
        @JvmField
         val SpeedSwing = FloatValue("SpeedSwing", 1.8F, 0F, 20F)
        @JvmField
        var itemPosX = FloatValue("itemPosX", 0f, -1f, 1f)
        @JvmField
        var itemPosY = FloatValue("itemPosY", 0f, -1f, 1f)
        @JvmField
        var itemPosZ = FloatValue("itemPosZ", 0f, -1f, 1f)
        @JvmField
        var Scale = FloatValue("Scale", 1f, 0f, 2f)



    }
    override val tag: String
        get() = modeValue.get()

}
