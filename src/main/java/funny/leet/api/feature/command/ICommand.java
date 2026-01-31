package funny.leet.api.feature.command;

import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.common.QuickLogger;

import java.util.List;
import java.util.stream.Stream;

public interface ICommand extends QuickLogger {
    void execute(String label, IArgConsumer args) throws CommandException;

    Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException;

    String getShortDesc();

    List<String> getLongDesc();

    List<String> getNames();

    default boolean hiddenFromHelp() {
        return false;
    }
}
