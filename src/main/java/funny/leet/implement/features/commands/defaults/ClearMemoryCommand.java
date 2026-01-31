package funny.leet.implement.features.commands.defaults;

import funny.leet.api.feature.command.exception.CommandNotEnoughArgumentsException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Formatting;
import obf.uscate.annotations.Compile;
import funny.leet.core.Main;
import funny.leet.api.feature.command.Command;
import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.exception.CommandException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static funny.leet.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClearMemoryCommand extends Command {

    public ClearMemoryCommand(Main main) {
        super("clearmemory", "cm");
    }

    @Compile
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            logDirect("Using: " + Formatting.GRAY + FORCE_COMMAND_PREFIX + label + " <amount>");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args.getString());
        } catch (NumberFormatException e) {
            logDirect("Amount must be a number.");
            return;
        }

        if (amount <= 0) {
            logDirect("Error: The amount of memory " + Formatting.RED + "must be greater than 0" + Formatting.RESET + "!");
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);

        if (amount > maxMemory) {
            logDirect("Error: Requested quantity (" + amount + " MB) " + Formatting.RED + "exceeds the maximum JVM memory" + Formatting.RESET + " (" + maxMemory + " MB)!");
            return;
        }

        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        System.gc();
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long freedMemory = (beforeMemory - afterMemory) / (1024 * 1024);

        if (freedMemory > 0) {
            logDirect("The allowed amount of memory " + Formatting.GREEN + "has been cleared" + Formatting.RESET + ": " + freedMemory + " MB.");
        } else {
            logDirect("Failed to free memory. Try later.", Formatting.YELLOW);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return Stream.of("50", "100", "200", "500")
                    .filter(s -> {
                        try {
                            return s.startsWith(args.getString());
                        } catch (CommandNotEnoughArgumentsException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Cleans the RAM by calling garbage collection.";
    }

    @Compile
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "This command causes garbage collection to free up RAM.",
                "",
                "Usage:",
                "> clearmemory <amount> - Attempts to free up the specified amount of memory in megabytes.",
                "",
                "Note: The actual amount of memory freed depends on the JVM and may differ from the requested amount."
        );
    }
}
