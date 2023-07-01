package me.utils

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue


@ModuleInfo(name = "CustomUI", description = "Custom", category = ModuleCategory.COLOR)
class CustomUI : Module() {


    companion object {
        @JvmField
        val r = IntegerValue("red", 39, 0, 255)
        @JvmField
        val g = IntegerValue("green", 120, 0, 255)
        @JvmField
        val b = IntegerValue("blue", 186, 0, 255)
        @JvmField
        val r2= IntegerValue("red2", 20, 0, 255)
        @JvmField
        val g2= IntegerValue("green2", 50, 0, 255)
        @JvmField
        val b2 = IntegerValue("blue2", 80, 0, 255)
        @JvmField
        val a = IntegerValue("alpha", 180, 0, 255)
        @JvmField
        val radius = FloatValue("2", 3f, 0f, 10f)
        @JvmField
        val outlinet = FloatValue("1", 0.4f, 0f, 5f)
        @JvmField
        val drawMode = ListValue("mode", arrayOf("1", "2","shadow","blurshadower"), "圆角矩形")
        @JvmField
        val shadowValue = FloatValue("shadowstronge", 8f,0f,20f)
        @JvmField
        val blurValue = FloatValue("blurstronge", 15f,0f,30f)

    }




}
