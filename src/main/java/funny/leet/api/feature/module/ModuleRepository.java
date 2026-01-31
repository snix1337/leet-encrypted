package funny.leet.api.feature.module;

import funny.leet.implement.features.modules.combat.*;
import funny.leet.implement.features.modules.misc.*;
import funny.leet.implement.features.modules.movement.*;
import funny.leet.implement.features.modules.player.*;
import funny.leet.implement.features.modules.render.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.particle.Particle;
import obf.uscate.annotations.Compile;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleRepository {
    List<Module> modules = new ArrayList<>();

    public void setup() {
        register(
                new ServerHelper(),
                new WaterSpeed(),
                new ClickAction(),
                new ItemTweaks(),
                new Hud(),
                new AuctionHelper(),
                new ProjectilePrediction(),
                new BlockTags(),
                new Aura(),
                new AutoSwap(),
                new NoFriendDamage(),
                new HitBoxModule(),
                new AntiBot(),
                new AutoCrystal(),
                new AutoSprint(),
                new NoPush(),
                new ElytraHelper(),
                new ClanUpgrade(),
                new NoDelay(),
                new AutoRespawn(),
                new NoSlow(),
                new GuiMove(),
                new ElytraFly(),
                new Blink(),
                new ElytraRecast(),
                new AutoTool(),
                new Nuker(),
                new FastBreak(),
                new CameraTweaks(),
                new HandTweaks(),
                new BlockHighLight(),
                new Tags(),
                new AutoTotem(),
                new ChestSaver(),
                new FreeCam(),
                new TriggerBot(),
                new ContainerStealer(),
                new AutoTpAccept(),
                new Arrows(),
                new AutoLeave(),
                new WorldRenderer(),
                new NoRender(),
                new Criticals(),
                new TargetPearl(),
                new NameProtect(),
                new Flight(),
                new Invisibles(),
                new AutoArmor(),
                new AutoUse(),
                new NoInteract(),
                new CrossHair(),
                new Spider(),
                new ServerRPSpoofer(),

                //--ADDED ON UPDATE #1.0.0--//
                new DragonFly(),
                new ElytraTarget(),

                //--ADDED ON UPDATE #1.3.0--//
                new AirStuck(),
                //new Cape()

                //--ADDED ON UPDATE #1.8.0--//
                new HightJump(),

                //--ADDED ON UPATE #1.8.8--//
                new ElytraMotion(),

                //--RECODED ON UPDATE #1.8.9--//
                // WCH feature added in ElytraHelper

                //--ADDED ON UPDATE #1.9.0--//
                new AntiBan(),

                //--FIXES ON UPDATE 1.9.4--//
                // Elytra Recast

                //--FIXES ON UPDATE 1.9.5--//
                // Fabric Loader 0.17.0 downgraded to 0.16.14

                //--ADDED ON UPDATE #1.9.6--//
                new Particles(),
                new JumpCircle()

                //--FIXES ON UPDATE #1.9.8--//
                // Hud blur fixes

                //--FIXES ON UPDATE #1.9.8--//
                // Added ElytraFall parameter in AutoTotem
        );
    }

    @Compile
    public void register(Module... module) {
        modules.addAll(List.of(module));
    }

    public List<Module> modules() {
        return modules;
    }
}
