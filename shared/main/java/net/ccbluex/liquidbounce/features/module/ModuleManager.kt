/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.color.Gident
import net.ccbluex.liquidbounce.features.module.modules.color.Rainbow
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.combat.HytGapple
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.HytDisabler
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.liquidbounce.*
import net.ccbluex.liquidbounce.features.module.modules.hyt.BanChecker
import net.ccbluex.liquidbounce.features.module.modules.hyt.HytAntiBot
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.util.*


class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()
    var shouldNotify : Boolean = false
    var toggleSoundMode = 0

    var toggleVolume = 0F

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
            AutoSkyWars::class.java,
            LegitAura::class.java,
            AutoGG::class.java,
            NoC03::class.java,
                SpeedMine::class.java,
                AutoArmor::class.java,
            DMGParticle::class.java,
            Rainbow::class.java,
                AutoBow::class.java,

            EnchantEffect::class.java,
            BetterFPS::class.java,
            Gident::class.java,
                AutoPlay::class.java,
            PacketFixer::class.java,
                AutoPot::class.java,
            PlayerEdit::class.java,
                Trail::class.java,
               MemoryFix::class.java,
            AsianHat::class.java,


            Ambience::class.java,
               MotionBlur::class.java,
                AutoSoup::class.java,

            WolrdAnim::class.java,
                AutoWeapon::class.java,
                AutoLFix::class.java,
                BowAimbot::class.java,

            Pendant::class.java,
                Criticals::class.java,
                KillAura::class.java,

                Trigger::class.java,
                Velocity::class.java,

                ClickGUI::class.java,
            KeepSprint::class.java,
                HighJump::class.java,
            Spider::class.java,
                InventoryMove::class.java,
            HytDisabler::class.java,

                SafeWalk::class.java,
                WallClimb::class.java,
            PlayerHealthSend::class.java,
                Strafe::class.java,
                Sprint::class.java,
            BowJump::class.java,
                Teams::class.java,
                NoRotateSet::class.java,
                ChestStealer::class.java,
                Scaffold::class.java,
//                PlayerHealthSend::class.java,
                CivBreak::class.java,
                Tower::class.java,
                FastBreak::class.java,
                FastPlace::class.java,
                ESP::class.java,
                Speed::class.java,
                Tracers::class.java,

                NameTags::class.java,
                FastUse::class.java,
                //Teleport::class.java,
                Fullbright::class.java,
                ItemESP::class.java,
                StorageESP::class.java,
                Projectiles::class.java,
                HytGapple::class.java,
                NoClip::class.java,
                Nuker::class.java,
                PingSpoof::class.java,
                FastClimb::class.java,
                Step::class.java,
                AutoRespawn::class.java,
                AutoTool::class.java,

                NoWeb::class.java,
                Spammer::class.java,

                Zoot::class.java,
                AutoLobby::class.java,
                Regen::class.java,
                NoFall::class.java,
                Blink::class.java,
                NameProtect::class.java,
                NoHurtCam::class.java,
                Ghost::class.java,
                MidClick::class.java,
                XRay::class.java,
                Timer::class.java,
                Sneak::class.java,
                GhostHand::class.java,
                AutoWalk::class.java,
                AutoBreak::class.java,
                FreeCam::class.java,
                Aimbot::class.java,
                Eagle::class.java,
                HitBox::class.java,
                AntiCactus::class.java,
                Plugins::class.java,
                AntiHunger::class.java,
                ConsoleSpammer::class.java,
                LongJump::class.java,
                Parkour::class.java,
                //LadderJump::class.java,
                FastBow::class.java,
                MultiActions::class.java,

                AutoClicker::class.java,
                NoBob::class.java,
                BlockOverlay::class.java,
                NoFriends::class.java,
                BlockESP::class.java,
                Chams::class.java,
                Clip::class.java,
                Phase::class.java,
                ServerCrasher::class.java,
                NoFOV::class.java,
                //FastStairs::class.java,
                SwingAnimation::class.java,
                ReverseStep::class.java,
                TNTBlock::class.java,
                InventoryCleaner::class.java,
                TrueSight::class.java,
                LiquidChat::class.java,
                AntiBlind::class.java,
                NoSwing::class.java,
                BedGodMode::class.java,

                Breadcrumbs::class.java,
                AbortBreaking::class.java,
                PotionSaver::class.java,
                CameraClip::class.java,
                WaterSpeed::class.java,
                Ignite::class.java,

                MoreCarry::class.java,
                NoPitchLimit::class.java,
                Kick::class.java,
                Liquids::class.java,
                AtAllProvider::class.java,
                AttackEffects::class.java,
                AirLadder::class.java,
                GodMode::class.java,
                //TeleportHit::class.java,
                ForceUnicodeChat::class.java,
                ItemTeleport::class.java,

                SuperKnockback::class.java,
                ProphuntESP::class.java,
                AutoFish::class.java,
                Damage::class.java,

                KeepContainer::class.java,
                VehicleOneHit::class.java,
                Reach::class.java,
                Rotations::class.java,
                NoJumpDelay::class.java,
                BlockWalk::class.java,
                AntiAFK::class.java,
                PerfectHorseJump::class.java,
                HUD::class.java,
                TNTESP::class.java,
                ComponentOnHover::class.java,
                KeepAlive::class.java,
                ResourcePackSpoof::class.java,
                OldHitting::class.java,
                Cape::class.java,
                Title::class.java,
                NoSlowBreak::class.java,
            JumpCircle::class.java,
            ScaffoldHelper::class.java,
            HytPingSpoof::class.java,
            //HytDisabler::class.java,
            GrimVelocity::class.java,
            StrafeFix::class.java,
            //FollowTargetHud::class.java,
            AntiFakePlayer::class.java,
            //AutoGG::class.java,

            HytAntiBot::class.java,
            BanChecker::class.java,
            //EndDisabler::class.java,
            NoLagHYT::class.java,
            //Velocity2::class.java,

            NoSlow::class.java,

                PortalMenu::class.java
        )

        registerModule(NoScoreboard)
        registerModule(Fucker)
        registerModule(ChestAura)
        registerModule(AntiBot)

        ClientUtils.getLogger().info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        if (!module.isSupported)
            return

        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule(moduleClass: Class<*>) = moduleClassMap[moduleClass]!!

    operator fun get(clazz: Class<*>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
