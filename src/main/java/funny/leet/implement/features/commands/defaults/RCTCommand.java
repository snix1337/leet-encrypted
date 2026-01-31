package funny.leet.implement.features.commands.defaults;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Formatting;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.command.Command;
import funny.leet.api.feature.command.argument.IArgConsumer;
import funny.leet.api.feature.command.exception.CommandException;
import funny.leet.api.repository.rct.RCTRepository;
import funny.leet.common.QuickImports;
import funny.leet.common.util.world.ServerUtil;
import funny.leet.core.Main;
import funny.leet.implement.features.draggables.Notifications;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RCTCommand extends Command implements QuickImports {
    private final RCTRepository repository;

    protected RCTCommand(Main main) {
        super("rct");
        repository = main.getRCTRepository();
    }

    @Compile
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (!ServerUtil.isHolyWorld()) {
            Notifications.getInstance().addList("[RCT] Не работает на этом " + Formatting.RED + "сервере", 3000);
            return;
        }

        if (ServerUtil.isPvp()) {
            Notifications.getInstance().addList("[RCT] Вы находитесь в режиме " + Formatting.RED + "пвп", 3000);
            return;
        }

        if (args.hasAny()) {
            args.requireMin(1);
            int anarchy = args.getArgs().getFirst().getAs(Integer.class);
            repository.reconnect(anarchy);
        } else repository.reconnect(ServerUtil.getAnarchy());
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        return Stream.empty();
    }


    @Override
    public String getShortDesc() {
        return "Перезаходит на анархию";
    }


    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Перезаходит на анархию",
                "",
                "Использование:",
                "> rct <anarchy> - Заходит на <anarchy>",
                "> rct - Перезаходит на анархию где вы только что были"
        );
    }
}