package funny.leet.api.system.logger.implement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import funny.leet.api.system.logger.Logger;

public class ConsoleLogger implements Logger {
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger("leet");

    @Override
    public void log(Object message) {
        logger.info("[leet]", message);
    }

    @Override
    public void minecraftLog(Text... components) {
        MutableText component = Text.literal("");
        component.append(getPrefix());
        component.append(Text.literal(" "));
        for (Text text : components) {
            component.append(text);
        }
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(component);
        }
    }

    private Text getPrefix() {
        MutableText text = Text.literal("");

        int startR = 128, startG = 128, startB = 160;
        int endR = 30, endG = 30, endB = 60;
        String prefix = "leet → ";
        int steps = prefix.length();

        for (int i = 0; i < steps; i++) {
            float t = (float) i / (steps - 1);
            int r = (int) MathHelper.lerp(t, startR, endR);
            int g = (int) MathHelper.lerp(t, startG, endG);
            int b = (int) MathHelper.lerp(t, startB, endB);

            int rgb = (r << 16) | (g << 8) | b;

            text.append(Text.literal(String.valueOf(prefix.charAt(i)))
                    .setStyle(Text.literal("").getStyle().withColor(rgb)));
        }

        return text;
    }
}
