package fishcute.celestial.mixin;

import fishcute.celestial.util.ClientTick;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Mixin(SimpleReloadableResourceManager.class)
public class ReloadableResourceManagerMixin {

    @Inject(method = "createFullReload", at = @At("RETURN"))
    private void reload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<PackResources> list, CallbackInfoReturnable<ReloadInstance> cir) {
        System.out.println("Sky RP Reload Called -------------------------------------------------------");
        ClientTick.reload();
    }
}
