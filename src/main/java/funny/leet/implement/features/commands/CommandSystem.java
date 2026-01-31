package funny.leet.implement.features.commands;

import funny.leet.api.feature.command.ICommandSystem;
import funny.leet.api.feature.command.argparser.IArgParserManager;
import funny.leet.implement.features.commands.argparser.ArgParserManager;

public enum CommandSystem implements ICommandSystem {
    INSTANCE;

    @Override
    public IArgParserManager getParserManager() {
        return ArgParserManager.INSTANCE;
    }
}
