package funny.leet.implement.features.draggables;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.other.BufferUtil;
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.math.StopWatch;
import funny.leet.common.util.other.StringUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.render.Render2DUtil;
import funny.leet.common.util.render.ScissorManager;
import funny.leet.core.Main;
import funny.leet.implement.features.modules.render.Hud;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaPlayer extends AbstractDraggable {

    public static MediaPlayer getInstance() {
        return Instance.getDraggable(MediaPlayer.class);
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MediaInfo mediaInfo = new MediaInfo("Track Name", "Artist", new byte[0], 43, 150, false);
    private final Identifier artwork = Identifier.of("textures/tunes.png");
    private final StopWatch lastMedia = new StopWatch();
    public IMediaSession session;
    private float widthDuration;

    public MediaPlayer() {
        super("Tunes", 10, 400, 100, 40, true);
    }

    @Override
    public boolean visible() {
        return !lastMedia.finished(2000) || PlayerIntersectionUtil.isChat(mc.currentScreen);
    }

    @Override
    public void tick() {
        if (Hud.getInstance().isState() && Hud.getInstance().interfaceSettings.isSelected("Tunes") && mc.player.age % 5 == 0) {
            executorService.submit(() -> {
                IMediaSession currentSession = session = MediaPlayerInfo.Instance.getMediaSessions().stream()
                        .max(Comparator.comparing(s -> s.getMedia().getPlaying()))
                        .orElse(null);

                if (currentSession != null) {
                    MediaInfo info = currentSession.getMedia();
                    if (!info.getTitle().isEmpty() || !info.getArtist().isEmpty()) {
                        if (mediaInfo.getTitle().equals("Track Name") || !Arrays.toString(mediaInfo.getArtworkPng()).equals(Arrays.toString(info.getArtworkPng()))) {
                            BufferUtil.registerTexture(artwork, info.getArtworkPng());
                        }
                        mediaInfo = info;
                        lastMedia.reset();
                    }
                }
            });
        }
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack matrix = context.getMatrices();
        ScissorManager scissor = Main.getInstance().getScissorManager();
        FontRenderer big = Fonts.getSize(15, Fonts.Type.BOLD);
        FontRenderer mini = Fonts.getSize(12, Fonts.Type.DEFAULT);

        int sizeArtwork = 32;
        int sizePausePlay = 4;
        int maxDurationWidth = getWidth() - (sizeArtwork + 12);
        int duration = (int) mediaInfo.getDuration();
        int position = MathHelper.clamp((int) mediaInfo.getPosition(), 0, duration);
        String timeDuration = StringUtil.getDuration(duration);

        widthDuration = MathHelper.clamp(
                MathUtil.interpolateSmooth(1, widthDuration, Math.round((float) position / duration * maxDurationWidth)),
                1, maxDurationWidth
        );

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), getHeight())
                .round(6.5F).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(1.0F))
                .color(ColorUtil.getRect(0.7F))
                .build());

        scissor.push(matrix.peek().getPositionMatrix(), getX() + sizeArtwork + 8, getY(), getWidth() - sizeArtwork - 10, getHeight());
        big.drawStringWithScroll(matrix, mediaInfo.getTitle(), getX() + sizeArtwork + 8, getY() + 7, 56, ColorUtil.getText());
        mini.drawStringWithScroll(matrix, mediaInfo.getArtist(), getX() + sizeArtwork + 8, getY() + 15.5F, 56, ColorUtil.getText(0.75F));
        scissor.pop();

        Render2DUtil.drawTexture(context, artwork, getX() + 4, getY() + 4, sizeArtwork, 6.0F, sizeArtwork, sizeArtwork, sizeArtwork, ColorUtil.getRect(1));

        mini.drawString(matrix, StringUtil.getDuration(position), getX() + 8 + sizeArtwork, getY() + 27, ColorUtil.getText());
        mini.drawString(matrix, timeDuration, getX() + getWidth() - 4 - mini.getStringWidth(timeDuration), getY() + 27, ColorUtil.getText());

        rectangle.render(ShapeProperties.create(matrix, getX() + 8 + sizeArtwork, getY() + getHeight() - 8, maxDurationWidth, 2)
                .round(0.75F).color(ColorUtil.getRectDarker(0.75F)).build());

        rectangle.render(ShapeProperties.create(matrix, getX() + 8 + sizeArtwork, getY() + getHeight() - 8, widthDuration, 2)
                .softness(4).round(1).color(ColorUtil.roundClientColor(0.2F)).build());

        rectangle.render(ShapeProperties.create(matrix, getX() + 8 + sizeArtwork, getY() + getHeight() - 8, widthDuration, 2)
                .round(0.75F).color(ColorUtil.roundClientColor(1)).build());

        Render2DUtil.drawTexture(context,
                Identifier.of("textures/" + (mediaInfo.getPlaying() ? "pause" : "play") + ".png"),
                (float) (getX() + (double) (getWidth() + sizeArtwork + 4 - sizePausePlay) / 2),
                getY() + 26,
                sizePausePlay
        );
    }
}
