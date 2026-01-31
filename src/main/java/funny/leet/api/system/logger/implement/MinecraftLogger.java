package funny.leet.api.system.logger.implement;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import funny.leet.api.system.logger.Logger;
import funny.leet.common.QuickImports;

import java.util.Arrays;

public class MinecraftLogger implements Logger, QuickImports {
    @Override
    public void log(Object message) {

    }

    @Override
    public void minecraftLog(Text... components) {
        if (mc.player != null) {
            MutableText component = Text.literal("");
            Arrays.asList(components).forEach(component::append);
            mc.inGameHud.getChatHud().addMessage(component);
        }
    }
}
