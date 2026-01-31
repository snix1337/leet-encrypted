package funny.leet.implement.features.commands.defaults;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.command.Command;
import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.datatypes.BlockDataType;
import funny.leet.api.feature.command.datatypes.BlockESPDataType;
import funny.leet.api.feature.command.datatypes.EntityDataType;
import funny.leet.api.feature.command.datatypes.EntityESPDataType;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.api.feature.command.helpers.Paginator;
import funny.leet.api.feature.command.helpers.TabCompleteHelper;
import funny.leet.api.repository.box.BoxESPRepository;
import funny.leet.common.QuickImports;
import funny.leet.core.Main;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static funny.leet.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoxESPCommand extends Command implements QuickImports {
    final BoxESPRepository repository;

    protected BoxESPCommand(Main main) {
        super("box");
        this.repository = main.getBoxESPRepository();
    }

    @Compile
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        switch (arg) {
            case "add" -> handleAdd(args);
            case "remove" -> handleRemove(args);
            case "clear" -> handleClear(args);
            case "list" -> handleList(label, args);
        }
    }

    @Compile
    private void handleAdd(IArgConsumer args) throws CommandException {
        args.requireMin(2);
        String type = args.getString().toLowerCase(), name = args.getString();
        int color = args.has(1) ? args.getArgs().getFirst().getAs(Integer.class) : 0;
        switch (type) {
            case "block" -> BlockDataType.INSTANCE.findBlock(name).ifPresentOrElse(block -> {
                if (!repository.blocks.containsKey(block)) {
                    repository.blocks.put(block, color);
                    StringBuilder builder = new StringBuilder();
                    builder.append("Added block: ").append(Formatting.GREEN).append(block.getName().getString()).append(Formatting.GRAY);
                    if (color != 0) builder.append(", Color: ").append(Formatting.GREEN).append(color);
                    logDirect(builder.toString(), Formatting.GRAY);
                } else
                    logDirect(Formatting.GRAY + "Block \"" + Formatting.RED + block.getName().getString() + Formatting.GRAY + "\" already added");
            }, () -> logDirect(Formatting.RED + "Failed to find block"));
            case "entity" -> EntityDataType.INSTANCE.findEntity(name).ifPresentOrElse(entity -> {
                if (!repository.entities.containsKey(entity)) {
                    repository.entities.put(entity, color);
                    StringBuilder builder = new StringBuilder();
                    builder.append("Added entity: ").append(Formatting.GREEN).append(entity.getName().getString()).append(Formatting.GRAY);
                    if (color != 0) builder.append(", Color: ").append(Formatting.GREEN).append(color);
                    logDirect(builder.toString(), Formatting.GRAY);
                } else
                    logDirect(Formatting.GRAY + "Entity \"" + Formatting.RED + entity.getName().getString() + Formatting.GRAY + "\" already added");
            }, () -> logDirect(Formatting.RED + "Failed to find entity"));
            default -> logDirect(Formatting.RED + "Invalid type");
        }
    }

    @Compile
    private void handleRemove(IArgConsumer args) throws CommandException {
        args.requireMin(2);
        String type = args.getString().toLowerCase(), name = args.getString();
        switch (type) {
            case "block" ->
                    repository.blocks.keySet().stream().filter(block -> block.getName().getString().replace(" ", "_").equalsIgnoreCase(name)).findFirst().ifPresentOrElse(block -> {
                        repository.blocks.remove(block);
                        logDirect("Removed block: " + Formatting.GREEN + block.getName().getString(), Formatting.GRAY);
                    }, () -> logDirect(Formatting.RED + "Failed to find block"));
            case "entity" ->
                    repository.entities.keySet().stream().filter(block -> block.getName().getString().replace(" ", "_").equalsIgnoreCase(name)).findFirst().ifPresentOrElse(block -> {
                        repository.entities.remove(block);
                        logDirect("Removed entity: " + Formatting.GREEN + block.getName().getString(), Formatting.GRAY);
                    }, () -> logDirect(Formatting.RED + "Failed to find entity"));
            default -> logDirect(Formatting.RED + "Invalid type");
        }
    }

    @Compile
    private void handleList(String label, IArgConsumer args) throws CommandException {
        args.requireMin(1);
        String type = args.getString().toLowerCase();
        switch (type) {
            case "block" ->
                    Paginator.paginate(args, new Paginator<>(repository.blocks.entrySet().stream().toList()), () -> logDirect("List of blocks:"),
                            block -> {
                                Text text = Text.literal(Formatting.GRAY + "Name: " + Formatting.RED + block.getKey().getName().getString());
                                if (block.getValue() != 0)
                                    text.copy().append(Formatting.GRAY + "Color: " + Formatting.RED + block.getValue());
                                return text;
                            }, FORCE_COMMAND_PREFIX + label);
            case "entity" ->
                    Paginator.paginate(args, new Paginator<>(repository.entities.entrySet().stream().toList()), () -> logDirect("List of entities:"),
                            entity -> {
                                Text text = Text.literal(Formatting.GRAY + "Name: " + Formatting.RED + entity.getKey().getName().getString());
                                if (entity.getValue() != 0)
                                    text.copy().append(Formatting.GRAY + "Color: " + Formatting.RED + entity.getValue());
                                return text;
                            }, FORCE_COMMAND_PREFIX + label);
            default -> logDirect(Formatting.RED + "Invalid type");
        }
    }

    @Compile
    private void handleClear(IArgConsumer args) throws CommandException {
        args.requireMin(1);
        String type = args.getString().toLowerCase();
        switch (type) {
            case "block" -> {
                repository.blocks.clear();
                logDirect(Formatting.GREEN + "All blocks were removed.");
            }
            case "entity" -> {
                repository.entities.clear();
                logDirect(Formatting.GREEN + "All entities were removed.");
            }
            default -> logDirect(Formatting.RED + "Invalid type");
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        String arg = args.getString();
        if (args.has(4)) {
            return Stream.empty();
        } else if (args.has(3)) {
            return new TabCompleteHelper().append("Color").sortAlphabetically().stream();
        } else if (args.has(2)) {
            String nextArg = args.getString().toLowerCase();
            switch (nextArg) {
                case "block" -> {
                    if (arg.equalsIgnoreCase("remove")) return args.tabCompleteDatatype(BlockESPDataType.INSTANCE);
                    else if (arg.equalsIgnoreCase("add")) return args.tabCompleteDatatype(BlockDataType.INSTANCE);
                }
                case "entity" -> {
                    if (arg.equalsIgnoreCase("remove")) return args.tabCompleteDatatype(EntityESPDataType.INSTANCE);
                    else if (arg.equalsIgnoreCase("add")) return args.tabCompleteDatatype(EntityDataType.INSTANCE);
                }
            }
        } else if (args.hasAny()) {
            return new TabCompleteHelper().append("Block", "Entity").filterPrefix(args.getString()).sortAlphabetically().stream();
        } else {
            return new TabCompleteHelper().append("add", "remove", "list", "clear").filterPrefix(arg).sortAlphabetically().stream();
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Allows displaying boxes in the world";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "With this command you can add/remove render boxes in the world",
                "",
                "Usage:",
                "> box add <Block/Entity> <block/entity> <color> - Adds a block/entity",
                "> box remove <Block/Entity> <block/entity> - Removes a block/entity",
                "> box list <Block/Entity> - Returns a list of blocks/entities",
                "> box clear <Block/Entity> - Clears blocks/entities"
        );
    }
}
