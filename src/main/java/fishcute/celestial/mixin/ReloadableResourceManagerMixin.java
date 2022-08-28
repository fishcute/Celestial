package fishcute.celestial.mixin;

import fishcute.celestial.util.ClientTick;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.AsyncReloader;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(AsyncReloader.class)
public class ReloadableResourceManagerMixin {

    @Inject(method = "create", at = @At("RETURN"))
    private static void reload(IResourceManager resourceManager, List<IFutureReloadListener> listeners, Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> alsoWaitedFor, CallbackInfoReturnable<AsyncReloader<Void>> cir) {

        ClientTick.reload();
    }
}
