package fishcute.celestial;

import com.mojang.brigadier.CommandDispatcher;
import fishcute.celestial.sky.CelestialSky;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CelestialClient {
    public static final String MOD_ID = "celestial";

    public static void onInitializeClient() {
    }
    /*
    Failed attempt at making client-side commands:
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("breakblocker").executes(context -> {
                    CelestialSky.loadResources();
                    return 1;
                }));
    }*/
}