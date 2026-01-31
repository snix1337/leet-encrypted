package funny.leet.api.feature.command.datatypes;

import funny.leet.api.feature.command.exception.CommandException;

public interface IDatatypePost<T, O> extends IDatatype {
    T apply(IDatatypeContext datatypeContext, O original) throws CommandException;
}
