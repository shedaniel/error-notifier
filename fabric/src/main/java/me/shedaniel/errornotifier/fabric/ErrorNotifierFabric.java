package me.shedaniel.errornotifier.fabric;

import me.shedaniel.errornotifier.ErrorNotifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class ErrorNotifierFabric implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        ErrorNotifier.init(FabricLoader.getInstance().getLaunchArguments(true),
                FabricLoader.getInstance().getGameDir(),
                FabricLoader.getInstance().getModContainer("minecraft").map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse(null),
                FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
    }
}
