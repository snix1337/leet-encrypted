package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreBoard extends AbstractDraggable {

    private List<ScoreboardEntry> scoreboardEntries = new ArrayList<>();
    private ScoreboardObjective objective;

    public ScoreBoard() {
        super("Score Board", 10, 100, 100, 120, true);
    }

    @Override
    public boolean visible() {
        return objective != null && !scoreboardEntries.isEmpty();
    }

    @Override
    public void tick() {
        if (mc.world == null) return;
        objective = mc.world.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (objective == null) {
            scoreboardEntries = new ArrayList<>();
            return;
        }
        scoreboardEntries = mc.world.getScoreboard()
                .getScoreboardEntries(objective)
                .stream()
                .sorted(Comparator.comparing(ScoreboardEntry::value).reversed()
                        .thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public void drawDraggable(DrawContext context) {
        if (objective == null || scoreboardEntries.isEmpty()) return;
        MatrixStack matrix = context.getMatrices();
        FontRenderer fontTitle = Fonts.getSize(19, Fonts.Type.BOLD);
        FontRenderer fontEntry = Fonts.getSize(17, Fonts.Type.DEFAULT);
        Text titleText = objective.getDisplayName();

        List<Text> lines = new ArrayList<>();
        for (ScoreboardEntry entry : scoreboardEntries) {
            Team team = mc.world.getScoreboard().getScoreHolderTeam(entry.owner());
            Text decorated = Team.decorateName(team, entry.name());
            lines.add(decorated);
        }

        float lineHeight = 10.0F;
        float contentHeight = lines.size() * lineHeight;
        float totalHeight = 17.5F + contentHeight + 6;
        int padding = 8;
        int titleWidth = (int) fontTitle.getStringWidth(titleText.getString());
        int maxLineWidth = 0;
        for (Text line : lines) {
            int width = (int) fontEntry.getStringWidth(line.getString());
            maxLineWidth = Math.max(maxLineWidth, width);
        }
        int contentWidth = Math.max(titleWidth, maxLineWidth) + padding * 2 + 12;
        int width = Math.max(contentWidth, 100);
        float blurSoftness = 1.2F;

        blur.render(ShapeProperties.create(matrix, getX(), getY(), width, 17.5F)
                .round(6).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f))
                .build());

        blur.render(ShapeProperties.create(matrix, getX(), getY(), width, 17.5F)
                .round(6).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.07f))
                .build());

        blur.render(ShapeProperties.create(matrix, getX(), getY() + 17.5F, width, contentHeight + 6)
                .round(6).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.getRect(0.7F))
                .build());

        float centerX = getX() + width / 2.0F;
        fontTitle.drawString(matrix, titleText.getString(),
                (int) (centerX - fontTitle.getStringWidth(titleText.getString()) / 2.0F),
                getY() + 5.7F, ColorUtil.getText());

        float y = getY() + 17.5F + 3;
        float leftX = getX() + padding;
        for (Text line : lines) {
            fontEntry.drawText(matrix, line, (int) leftX, (int) y);
            y += lineHeight;
        }

        if (getX() + width > mc.getWindow().getScaledWidth()) {
            setX(mc.getWindow().getScaledWidth() - width - 5);
        }

        setWidth(width);
        setHeight((int) totalHeight);
    }
}
