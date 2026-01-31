package funny.leet.api.feature.command.datatypes;

import funny.leet.api.feature.command.exception.CommandException;

public interface IDatatypeFor<T> extends IDatatype  {
    T get(IDatatypeContext datatypeContext) throws CommandException;
}
