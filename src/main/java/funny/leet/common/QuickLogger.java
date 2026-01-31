package funny.leet.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.stream.Stream;

public interface QuickLogger {
    static Text getPrefix() {
        MutableText text = Text.literal("");

        int startR = 128, startG = 128, startB = 160; // Начальный серый с лёгким синим оттенком
        int endR = 30, endG = 30, endB = 60; // Конечный тёмно-серый с лёгким синим оттенком
        String prefix = "leet → "; // Включаем стрелку в градиент
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

    default void logDirect(Text... components) {
        MutableText component = Text.literal("");
        component.append(getPrefix());
        component.append(Text.literal(" "));
        Arrays.asList(components).forEach(component::append);
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(component);
        }
    }

    default void logDirect(String message, Formatting color) {
        Stream.of(message.split("\n")).forEach(line -> {
            MutableText component = Text.literal(line.replace("\t", "    "));
            component.setStyle(component.getStyle().withColor(color));
            logDirect(component);
        });
    }

    default void logDirect(String message) {
        logDirect(message, Formatting.GRAY);
    }
}
