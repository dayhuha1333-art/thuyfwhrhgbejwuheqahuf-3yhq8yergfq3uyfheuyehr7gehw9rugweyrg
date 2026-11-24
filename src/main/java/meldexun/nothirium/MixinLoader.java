package meldexun.nothirium;

import zone.rong.mixinbooter.IEarlyMixinLoader;
import java.util.Collections;
import java.util.List;

public class MixinLoader implements IEarlyMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.nothirium.json");
    }
}
