package funny.leet.api.feature.command.exception;

import net.minecraft.util.Formatting;
import funny.leet.api.feature.command.ICommand;
import funny.leet.api.feature.command.argument.ICommandArgument;
import funny.leet.common.QuickLogger;

import java.util.List;

public interface ICommandException extends QuickLogger {

    String getMessage();

    default void handle(ICommand command, List<ICommandArgument> args) {
        logDirect(
                this.getMessage(),
                Formatting.RED
        );
    }
}
