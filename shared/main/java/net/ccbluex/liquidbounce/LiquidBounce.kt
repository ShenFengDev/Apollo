/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce

import me.manager.CombatManager
import me.sound.Sound
import me.utils.AnimationHandler
import me.utils.WebUtil
import net.ccbluex.liquidbounce.api.Wrapper
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.cape.CapeAPI.registerCapeService
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.ModuleManager

import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.features.special.ClientRichPresence
import net.ccbluex.liquidbounce.features.special.DonatorCape
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.tabs.BlocksTab
import net.ccbluex.liquidbounce.tabs.ExploitsTab
import net.ccbluex.liquidbounce.tabs.HeadsTab
import net.ccbluex.liquidbounce.ui.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClassUtils.hasForge
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.misc.sound.TipSoundManager
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.opengl.Display
import java.awt.AWTException
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.util.*
import javax.imageio.ImageIO

object LiquidBounce {

    // Client information
     var CLIENT_NAME = "Apollo"
    const val CLIENT_VERSION = 1.6
    const val CLIENT_CREATOR = "LiquidBounce-Team"
    const val MINECRAFT_VERSION = Backend.MINECRAFT_VERSION
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false
    var mainMenuPrep = false
    var darkMode = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var combatManager: CombatManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var fontLoaders: FontLoaders
    public var toggleVolume = 0F
    val windows = System.getProperties().getProperty("os.name").toLowerCase().contains("windows")

    var trayIcon: TrayIcon? = null
    // HUD & ClickGUI
    lateinit var hud: HUD
    lateinit var animationHandler: AnimationHandler
    lateinit var clickGui: ClickGui

    // Update information
    var latestVersion = 0

    // Menu Background
    var background: IResourceLocation? = null



    // Discord RPC
    lateinit var clientRichPresence: ClientRichPresence

    lateinit var wrapper: Wrapper


    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        //ClientUtils.getLogger().info("Starting $CLIENT_NAME b$CLIENT_VERSION, by $CLIENT_CREATOR")

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(DonatorCape())
        eventManager.registerListener(InventoryUtils())


        // Init SoundManager
        tipSoundManager = TipSoundManager()

        // Init Discord RPC
        clientRichPresence = ClientRichPresence()

        combatManager = CombatManager()
        eventManager.registerListener(combatManager)


        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()
        FontLoaders.initFonts()


        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()
        animationHandler = AnimationHandler()
        try {
            // Remapper
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        fontLoaders = FontLoaders()

        // Load configs
        fileManager.loadConfigs(fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig)

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)



        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
            HeadsTab()
        }

        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to register cape service", throwable)
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()

        //val a = WebUtil.get("https://gitcode.net/Darren_kool/Pr11Praa/-/raw/master/1.txt")
        //if (!a.contains(CLIENT_NAME)) {
        //    MinecraftInstance.mc.shutdown()
        //}

        //System notification

        Display.setTitle(CLIENT_NAME + " B" + CLIENT_VERSION)

      Sound()
//        displayTray("Apollo", "Thanks Use", TrayIcon.MessageType.NONE)
//      Sound.INSTANCE.Spec()

        // Set is starting status
        isStarting = false
    }

//     * Execute if client will be stopped
//     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

        // Shutdown discord rpc
        clientRichPresence.shutdown()
    }
}