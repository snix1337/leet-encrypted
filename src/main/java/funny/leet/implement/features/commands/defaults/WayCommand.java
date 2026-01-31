package funny.leet.implement.features.commands.defaults;

import funny.leet.api.feature.command.datatypes.WayDataType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.command.Command;
import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.api.feature.command.helpers.Paginator;
import funny.leet.api.feature.command.helpers.TabCompleteHelper;
import funny.leet.api.repository.way.WayRepository;
import funny.leet.common.QuickImports;
import funny.leet.core.Main;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static funny.leet.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class WayCommand extends Command implements QuickImports {
    final WayRepository wayRepository;

    protected WayCommand(Main main) {
        super("way");
        wayRepository = main.getWayRepository();
    }

    @Compile
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        switch (arg) {
            case "add" -> handleAddWay(args);
            case "remove" -> handleRemoveWay(args);
            case "clear" -> handleClearWays(args);
            case "list" -> handleListWays(label, args);
        }
    }

    @Compile
    private void handleAddWay(IArgConsumer args) throws CommandException {
        args.requireMin(4);
        String name = args.getString();
        int x = args.getArgs().get(0).getAs(Integer.class);
        int y = args.getArgs().get(1).getAs(Integer.class);
        int z = args.getArgs().get(2).getAs(Integer.class);

        if (wayRepository.hasWay(name)) {
            logDirect("Marker with this name already exists in the list!", Formatting.RED);
            return;
        }

        String address = mc.getNetworkHandler() == null ? "vanilla" : mc.getNetworkHandler().getServerInfo() == null ? "vanilla" : mc.getNetworkHandler().getServerInfo().address;
        logDirect("Added marker " + name + ", Coordinates:" + " (" + x + ", " + y + ", " + z + "), Server: " + address, Formatting.GRAY);
        wayRepository.addWay(name, new BlockPos(x, y, z), address);
    }

    @Compile
    private void handleRemoveWay(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        String name = args.getString();
        if (wayRepository.hasWay(name)) {
            wayRepository.deleteWay(name);
            logDirect(Formatting.GREEN + "Marker " + Formatting.RED + name + Formatting.GREEN + " was successfully removed!");
        } else logDirect("Marker with name '" + name + "' not found!");
    }

    @Compile
    private void handleListWays(String label, IArgConsumer args) throws CommandException {
        args.requireMax(1);
        Paginator.paginate(args, new Paginator<>(wayRepository.wayList),
                () -> logDirect("List of markers:"),
                way -> Text.literal(Formatting.GRAY + "Name: " + Formatting.RED + way.name())
                        .append(Text.literal(Formatting.GRAY + " Coordinates: " + Formatting.WHITE + " (" + way.pos().getX() + ", " + way.pos().getY() + ", " + way.pos().getZ() + ")")
                                .append(Text.literal(Formatting.GRAY + " Server: " + Formatting.WHITE + way.server()))), FORCE_COMMAND_PREFIX + label);
    }

    @Compile
    private void handleClearWays(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        wayRepository.clearList();
        logDirect(Formatting.GREEN + "All markers were removed.");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (arg.equalsIgnoreCase("remove")) {
                if (args.hasExactlyOne()) return args.tabCompleteDatatype(WayDataType.INSTANCE);
            } else if (arg.equalsIgnoreCase("add")) {
                String string = args.has(5) ? "" : args.has(4) ? "z" : args.has(3) ? "y" : args.has(2) ? "x" : "Name";
                return new TabCompleteHelper().sortAlphabetically().prepend(string).stream();
            } else {
                return new TabCompleteHelper().sortAlphabetically().prepend("add", "remove", "list", "clear").filterPrefix(arg).stream();
            }
        }
        return Stream.empty();
    }


    @Override
    public String getShortDesc() {
        return "Allows placing markers in the world";
    }

    @Compile
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "With this command you can add/remove markers in the world",
                "",
                "Usage:",
                "> way add <name> <x> <y> <z> - Adds a marker",
                "> way remove <name> - Removes a marker",
                "> way list - Returns a list of markers",
                "> way clear - Clears the list of markers."
        );
    }
}
