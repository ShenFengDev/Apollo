package net.ccbluex.liquidbounce.ui.cnfont;


import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class FontLoaders {

    public static FontDrawer F40;
    public static FontDrawer xyz20;
    public static FontDrawer jelloFontMedium16;
    public static FontDrawer jellolightBig2;
    public static FontDrawer xyz18;
    public static FontDrawer Check24;
    public static FontDrawer NL16;
    public static FontDrawer NL35;
    public static FontDrawer NL20;

    public static FontDrawer NL18;
    public static FontDrawer NL24;
    public static FontDrawer NIcon24;
    public static FontDrawer NL14;
    public static FontDrawer N2Icon24;

    public static FontDrawer F14;
    public static FontDrawer F35;
    public static FontDrawer F22;

    public static FontDrawer F18;

    public static FontDrawer F20;
    public static FontDrawer F10;
    public static FontDrawer F24;
    public static FontDrawer S22;
    public static FontDrawer JB;
    public static FontDrawer SB30;
    public static FontDrawer SB20;
    public static FontDrawer SB15;
    public static FontDrawer SB35;
    public static FontDrawer SB18;
    public static FontDrawer F26;
    public static FontDrawer jellolightBig;
    public static FontDrawer jellolight18;
    public static FontDrawer jellom18;
    public static FontDrawer jellor16;
    public static FontDrawer jellor18;

    public static FontDrawer R20;
    public static FontDrawer R22;
    public static FontDrawer F28;
    public static FontDrawer F16;
    public static FontDrawer j30;
    public static FontDrawer S16;
    public static FontDrawer j20;
    public static FontDrawer T24;
    public static FontDrawer T40;
    public static FontDrawer T22;
    public static FontDrawer T20;
    public static FontDrawer T16;
    public static FontDrawer T18;
    public static FontDrawer Tcheck;
    public static void initFonts() {
        F10 = getFont("misans.ttf", 10, true);
        F16 = getFont("misans.ttf", 16, true);
        F14 = getFont("misans.ttf", 14, true);
        F18= getFont("misans.ttf", 18, true);
        F35= getFont("misans.ttf", 35, true);
        F40= getFont("misans.ttf", 40, true);
        F22= getFont("misans.ttf", 22, true);
        F24= getFont("misans.ttf", 24, true);
        F22= getFont("misans.ttf", 22, true);
        F20= getFont("misans.ttf", 20, true);
        SB20 = getFont("sfbold.ttf", 20, true);
        SB30= getFont("sfbold.ttf", 30, true);
        SB35= getFont("sfbold.ttf", 35, true);

        SB15= getFont("sfbold.ttf", 15, true);
        SB18= getFont("sfbold.ttf", 18, true);
        jellor16 = getFont("jelloregular.ttf",16,true);
        jellor18 = getFont("jelloregular.ttf",18,true);
        jellom18 = getFont("jellomedium.ttf",18,true);
        jellolight18 = getFont("jellolight.ttf", 18, true);
        jellolightBig = getFont("jellomedium.ttf",45,true);

        S16 = getFont("sfui.ttf",16,true);
        S22 = getFont("sfui.ttf",22,true);
        JB = getFont("misans.ttf",45,true);
        R20 = getFont("regular.ttf", 20, true);
        R22 = getFont("regular.ttf", 22, true);
        F26= getFont("misans.ttf", 22, true);
        F28 = getFont("misans.ttf", 20, true);
        j30 = getFont("jellolight.ttf", 30, true);
        j20 = getFont("jellolight.ttf", 20, true);
        T24 = getFont("bold.ttf", 24, true);
        T20 = getFont("bold.ttf", 20, true);
        T16 = getFont("bold.ttf", 16, true);
        T18 = getFont("bold.ttf", 18, true);
        T22 = getFont("bold.ttf", 22, true);
        T40 = getFont("bold.ttf", 40, true);
        Tcheck = getFont("check.ttf", 36, true);

        N2Icon24 = getFont("icon2.ttf", 24, true);
        NL14 = getFont("sfui.ttf", 14, true);
        NL18= getFont("sfui.ttf", 18, true);
        NL16 = getFont("sfui.ttf", 16, true);
        NL20 = getFont("sfui.ttf", 20, true);
        NL24 = getFont("sfui.ttf", 24, true);
        NL35 = getFont("sfui.ttf", 35, true);
        NIcon24 = getFont("icon.ttf", 24, true);
        Check24 = getFont("check.ttf", 24, true);
        xyz18 = getFont("misans.ttf", 18, true);
        xyz20 = getFont("misans.ttf", 20, true);
        jelloFontMedium16 = getFont("jellomedium.ttf", 16, true);
        jellolightBig2 = getFont("jellolight.ttf", 45, true);

    }

    public static FontDrawer getFont(String name, int size, boolean antiAliasing) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/" + name)).getInputStream()).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return new FontDrawer(font, antiAliasing);
    }
}
