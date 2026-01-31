package funny.leet.implement.features.commands.defaults;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import obf.uscate.annotations.Compile;
import funny.leet.core.Main;
import funny.leet.api.feature.command.Command;
import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.datatypes.MacroDataType;
import funny.leet.api.feature.command.datatypes.KeyDataType;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.api.feature.command.helpers.Paginator;
import funny.leet.api.feature.command.helpers.TabCompleteHelper;
import funny.leet.api.repository.macro.MacroRepository;
import funny.leet.common.util.other.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static funny.leet.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MacroCommand extends Command {

    final MacroRepository macroRepository;

    public MacroCommand(Main main) {
        super("macro");
        macroRepository = main.getMacroRepository();
    }

    @Compile
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String action = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        switch (action) {
            case "add" -> handleAddMacro(args);
            case "remove" -> handleRemoveMacro(args);
            case "list" -> handleListMacros(args, label);
            case "clear" -> handleClearMacros(args);
        }
    }

    @Compile
    private void handleAddMacro(IArgConsumer args) throws CommandException {
        args.requireMin(3);
        int key = args.getDatatypeFor(KeyDataType.INSTANCE).getValue();
        String name = args.getString();
        String command = args.rawRest();

        if (macroRepository.hasMacro(name)) {
            logDirect("Macro with this name already exists in the list!", Formatting.RED);
            return;
        }

        macroRepository.addMacro(name, command, key);
        logDirect(Formatting.GREEN +
                "Added macro with name " + Formatting.RED
                + name + Formatting.GREEN
                + " with key " + Formatting.RED
                + StringUtil.getBindName(key).toLowerCase() + Formatting.GREEN
                + " with command " + Formatting.RED
                + command);
    }

    @Compile
    private void handleRemoveMacro(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        String name = args.getString();
        if (macroRepository.hasMacro(name)) {
            macroRepository.deleteMacro(name);
            logDirect(Formatting.GREEN + "Macro " + Formatting.RED + name + Formatting.GREEN + " was successfully removed!");
        } else {
            logDirect("Macro with this name not found!", Formatting.RED);
        }
    }

    @Compile
    private void handleListMacros(IArgConsumer args, String label) throws CommandException {
        args.requireMax(1);
        Paginator.paginate(
                args, new Paginator<>(macroRepository.macroList),
                () -> logDirect("List of macros:"),
                macro -> {
                    String names = macro.name();
                    String keys = StringUtil.getBindName(macro.key()).toLowerCase();
                    String command = macro.message();

                    return Text.literal(Formatting.GRAY + "Name: " + Formatting.WHITE + names)
                            .append(Text.literal(Formatting.GRAY + " Key: " + Formatting.WHITE + keys))
                            .append(Text.literal(Formatting.GRAY + " Command: " + Formatting.WHITE + command));
                },
                FORCE_COMMAND_PREFIX + label);
    }

    @Compile
    private void handleClearMacros(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        macroRepository.clearList();
        logDirect("All macros were removed.", Formatting.GREEN);
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny() && args.hasExactlyOne()) {
            return new TabCompleteHelper()
                    .sortAlphabetically()
                    .prepend("add", "remove", "list", "clear")
                    .filterPrefix(args.getString())
                    .stream();
        } else if (args.hasAny()) {
            String arg = args.getString();
            if (arg.equalsIgnoreCase("add") && args.hasExactlyOne()) {
                return args.tabCompleteDatatype(KeyDataType.INSTANCE);
            } else if (arg.equalsIgnoreCase("remove") && args.hasExactlyOne()) {
                return args.tabCompleteDatatype(MacroDataType.INSTANCE);
            }
        }
        return Stream.empty();
    }


    @Override
    public String getShortDesc() {
        return "Allows managing macros";
    }

    @Compile
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "This command allows managing macros, which automatically input specified commands into the chat.",
                "",
                "Usage:",
                "> macro add <key> <name> <message> - Adds a new macro that will be activated by pressing the specified key and input the specified message.",
                "> macro remove <name> - Removes a macro with the specified name.",
                "> macro list - Displays a list of all current macros.",
                "> macro clear - Removes all macros from the list."
        );
    }

}
