package funny.leet.implement.features.modules.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.implement.events.packet.PacketEvent;

import java.util.Random;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AntiBan extends Module {

    String[] bannedWords = {
            "Нурсултан", "Nursultan", "Нурик",
            "Целестиал", "Celestial",
            "Экспенсив", "Expensive",
            "Софт", "Soft",
            "Чит", "Cheat",
            "nursultan", "celestial", "expensive", "soft", "cheat"
    };

    SelectSetting mode = new SelectSetting("Mode", "Selects the anti-ban method")
            .value("Add Text", "Encrypt Words").selected("Add Text");

    Random random = new Random();

    public AntiBan() {
        super("AntiBan", ModuleCategory.PLAYER);
        setup(mode);
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof ChatMessageC2SPacket chatPacket) {
            String message = chatPacket.chatMessage();

            boolean hasBannedWord = false;
            for (String bannedWord : bannedWords) {
                if (containsWord(message, bannedWord)) {
                    hasBannedWord = true;
                    break;
                }
            }

            if (hasBannedWord) {
                if (mode.isSelected("Add Text") && !containsNoAdvertising(message)) {
                    String modifiedMessage = message + " (Не реклама)";
                    e.setCancelled(true);
                    mc.getNetworkHandler().sendChatMessage(modifiedMessage);
                } else if (mode.isSelected("Encrypt Words")) {
                    String encryptedMessage = encryptBannedWords(message);
                    if (!encryptedMessage.equals(message)) {
                        e.setCancelled(true);
                        mc.getNetworkHandler().sendChatMessage(encryptedMessage);
                    }
                }
            }
        }
    }

    private String encryptBannedWords(String message) {
        String result = message;

        for (String bannedWord : bannedWords) {
            String pattern = "(?i)" + java.util.regex.Pattern.quote(bannedWord);
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = regex.matcher(result);

            while (matcher.find()) {
                String foundWord = matcher.group();
                String encrypted = encryptWord(foundWord);
                result = result.substring(0, matcher.start()) + encrypted + result.substring(matcher.end());
                matcher = regex.matcher(result);
            }
        }

        return result;
    }

    private String encryptWord(String word) {
        StringBuilder encrypted = new StringBuilder();
        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            boolean shouldEncrypt = (i == 0) || (random.nextInt(3) == 0);

            if (shouldEncrypt && Character.isLetter(c)) {
                int method = random.nextInt(4);
                switch (method) {
                    case 0 -> encrypted.append('#');
                    case 1 -> encrypted.append('$');
                    case 2 -> {
                        char replaced = switch (Character.toLowerCase(c)) {
                            case 'a', 'а' -> '4';
                            case 'e', 'е' -> '3';
                            case 'i', 'и' -> '1';
                            case 'o', 'о' -> '0';
                            case 's', 'с' -> '5';
                            case 't', 'т' -> '7';
                            case 'l' -> '1';
                            case 'g' -> '9';
                            default -> c;
                        };
                        encrypted.append(replaced);
                    }
                    case 3 -> {
                        if (random.nextBoolean()) {
                            encrypted.append(Character.toUpperCase(c));
                        } else {
                            encrypted.append(Character.toLowerCase(c));
                        }
                        if (i < chars.length - 1 && random.nextBoolean()) {
                            encrypted.append(getRandomSymbol());
                        }
                    }
                }
            } else {
                encrypted.append(c);
            }
        }

        return encrypted.toString();
    }

    private char getRandomSymbol() {
        char[] symbols = {'_', '-', '.', '~'};
        return symbols[random.nextInt(symbols.length)];
    }

    private boolean containsWord(String text, String word) {
        return text.toLowerCase().contains(word.toLowerCase());
    }

    private boolean containsNoAdvertising(String text) {
        return text.contains("(Не реклама)") ||
                text.contains("(не реклама)") ||
                text.contains("(Не реклама )") ||
                text.contains("( не реклама)");
    }
}
