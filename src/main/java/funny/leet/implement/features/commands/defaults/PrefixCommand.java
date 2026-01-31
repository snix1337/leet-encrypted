package funny.leet.implement.features.commands.defaults;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Formatting;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.command.Command;
import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.api.feature.command.helpers.TabCompleteHelper;
import funny.leet.common.QuickImports;
import funny.leet.implement.features.commands.CommandDispatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefixCommand extends Command implements QuickImports {
    protected PrefixCommand() {
        super("prefix");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        if (arg.equals("set")) {
            args.requireMin(1);
            logDirect("Set prefix '" + Formatting.RED + (CommandDispatcher.prefix = args.getString()) + Formatting.GRAY + "'", Formatting.GRAY);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (arg.equalsIgnoreCase("set")) {
                return new TabCompleteHelper().sortAlphabetically().prepend("name").stream();
            } else {
                return new TabCompleteHelper().sortAlphabetically().prepend("set").filterPrefix(arg).stream();
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Allows changing the command prefix in the mod";
    }

    @Compile
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "With this command you can change the command prefix in the mod",
                "",
                "Usage:",
                "> prefix set <name> - sets the command prefix"
        );
    }
}
