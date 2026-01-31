package funny.leet.implement.features.commands.defaults;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.command.Command;
import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.datatypes.FriendDataType;
import funny.leet.api.feature.command.datatypes.TabPlayerDataType;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.api.feature.command.helpers.Paginator;
import funny.leet.api.feature.command.helpers.TabCompleteHelper;
import funny.leet.api.repository.friend.FriendUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static funny.leet.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

public class FriendCommand extends Command {
    protected FriendCommand() {
        super("friend");
    }

    @Compile
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        args.requireMax(1);
        if (arg.contains("add")) {
            String name = args.getString();
            if (!FriendUtils.isFriend(name)) {
                FriendUtils.addFriend(name);
                logDirect("You successfully added " + name + " to the friend list!");
            } else {
                logDirect(name + " is already in the friend list!", Formatting.RED);
            }
        }
        if (arg.contains("remove")) {
            String name = args.getString();
            if (FriendUtils.isFriend(name)) {
                FriendUtils.removeFriend(name);
                logDirect("You successfully removed " + name + " from the friend list!");
                return;
            }
            logDirect(name + " not found in the friend list", Formatting.RED);
        }
        if (arg.contains("list")) {
            Paginator.paginate(
                    args, new Paginator<>(FriendUtils.getFriends()),
                    () -> logDirect("List of friends:"),
                    friend -> {
                        String names = friend.getName();
                        MutableText namesComponent = Text.literal(names);
                        namesComponent.setStyle(namesComponent.getStyle().withColor(Formatting.WHITE));
                        return namesComponent;
                    },
                    FORCE_COMMAND_PREFIX + label
            );
        }
        if (arg.contains("clear")) {
            FriendUtils.clear();
            logDirect("Friend list cleared.");
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (args.hasExactlyOne()) {
                if (arg.equalsIgnoreCase("add")) {
                    return args.tabCompleteDatatype(TabPlayerDataType.INSTANCE);
                } else if (arg.equalsIgnoreCase("remove")) {
                    return args.tabCompleteDatatype(FriendDataType.INSTANCE);
                }
            } else {
                return new TabCompleteHelper()
                        .sortAlphabetically()
                        .prepend("add", "remove", "list", "clear")
                        .filterPrefix(arg)
                        .stream();
            }
        }
        return Stream.empty();
    }
    @Compile
    @Override
    public String getShortDesc() {
        return "Allows managing the friend list";
    }

    @Compile
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "With this command you can add/remove friends in the cheat",
                "",
                "Usage:",
                "> friend add <name> - Adds a name to the friend list.",
                "> friend remove <name> - Removes a name from the friend list.",
                "> friend list - Returns a list of friends",
                "> friend clear - Clears the friend list."
        );
    }
}
