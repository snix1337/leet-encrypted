package funny.leet.api.feature.command;

import funny.leet.api.feature.command.argparser.IArgParserManager;

public interface ICommandSystem {
    IArgParserManager getParserManager();
}
