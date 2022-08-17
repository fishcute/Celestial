package fishcute.celestial.mixin;

import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.ClientTick;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ProfiledReloadInstance;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
@Mixin(ReloadableResourceManager.class)
public class ReloadableResourceManagerMixin {

    @Inject(method = "createReload", at = @At("RETURN"))
    private void reload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<PackResources> list, CallbackInfoReturnable<ReloadInstance> cir) {
        ClientTick.reload();
    }
}
