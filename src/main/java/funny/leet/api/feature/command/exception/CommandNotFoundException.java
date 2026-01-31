package funny.leet.api.feature.command.exception;

import funny.leet.api.feature.command.ICommand;
import funny.leet.api.feature.command.argument.ICommandArgument;
import funny.leet.common.QuickLogger;

import java.util.List;

public class CommandNotFoundException extends CommandException implements QuickLogger {

    public final String command;

    public CommandNotFoundException(String command) {
        super(String.format("Команда не найдена: %s", command));
        this.command = command;
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
       logDirect(getMessage());
    }
}
