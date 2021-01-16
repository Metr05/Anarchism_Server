package io.github.metr05.economicsbase;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class AdminMessageChannel implements MessageChannel {

    @Override
    public Optional<Text> transformMessage(Object sender, MessageReceiver recipient,
                                           Text original, ChatType type) {
        Text text = original;
        text = Text.of(TextColors.RED, "[Admin]", TextColors.RESET, text);
        return Optional.of(text);
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return Collections.emptyList();
    }
}
