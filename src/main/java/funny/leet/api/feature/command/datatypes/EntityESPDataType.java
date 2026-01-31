package funny.leet.api.feature.command.datatypes;

import net.minecraft.entity.EntityType;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.api.feature.command.helpers.TabCompleteHelper;
import funny.leet.core.Main;

import java.util.Optional;
import java.util.stream.Stream;

public enum EntityESPDataType implements IDatatypeFor<EntityType<?>> {
    INSTANCE;

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        Stream<String> ways = getEntities().map(s -> s.getName().getString().replace(" ", "_"));
        String context = ctx.getConsumer().getString();
        return new TabCompleteHelper().append(ways).filterPrefix(context).sortAlphabetically().stream();
    }

    @Override
    public EntityType<?> get(IDatatypeContext datatypeContext) throws CommandException {
        return findEntity(datatypeContext.getConsumer().getString()).orElse(null);
    }

    public Optional<EntityType<?>> findEntity(String text) {
        return getEntities().filter(s -> s.getName().getString().replace(" ", "_").equalsIgnoreCase(text)).findFirst();
    }

    private Stream<EntityType<?>> getEntities() {
        return Main.getInstance().getBoxESPRepository().entities.keySet().stream();
    }
}