package net.ragnaroknetwork.rewardsgui.config.serializer;

import net.ragnaroknetwork.rewardsgui.ChatMessage;
import org.bukkit.ChatColor;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class ChatMessageSerializer implements ValueSerialiser<ChatMessage> {

    @Override
    public Class<ChatMessage> getTargetClass() {
        return ChatMessage.class;
    }

    @Override
    public ChatMessage deserialise(FlexibleType flexibleType) throws BadValueException {
        return new ChatMessage(ChatColor.translateAlternateColorCodes('&', flexibleType.getString()));
    }

    @Override
    public Object serialise(ChatMessage value, Decomposer decomposer) {
        return value.toString().replace(ChatColor.COLOR_CHAR, '&');
    }
}
