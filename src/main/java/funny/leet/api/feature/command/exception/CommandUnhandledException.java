package funny.leet.api.feature.command.exception;

import funny.leet.api.feature.command.ICommand;
import funny.leet.api.feature.command.argument.ICommandArgument;
import funny.leet.common.QuickLogger;

import java.util.List;

public class CommandUnhandledException extends RuntimeException implements ICommandException, QuickLogger {

    public CommandUnhandledException(String message) {
        super(message);
    }

    public CommandUnhandledException(Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
    }
}
