package net.ragnaroknetwork.rewardsgui.config.serializer;

import org.bukkit.Material;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class MaterialSerializer implements ValueSerialiser<Material> {

    @Override
    public Class<Material> getTargetClass() {
        return Material.class;
    }

    @Override
    public Material deserialise(FlexibleType flexibleType) throws BadValueException {
        Material material = Material.matchMaterial(flexibleType.getString());

        if (material == null)
            throw flexibleType.badValueExceptionBuilder()
                    .message("Material is not valid")
                    .build();

        return material;
    }

    @Override
    public Object serialise(Material value, Decomposer decomposer) {
        return value.toString();
    }
}