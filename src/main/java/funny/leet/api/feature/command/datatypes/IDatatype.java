package funny.leet.api.feature.command.datatypes;

import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.common.QuickImports;

import java.util.stream.Stream;

public interface IDatatype extends QuickImports {
    Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException;
}
