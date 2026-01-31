package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.animation.Animation;
import funny.leet.api.system.animation.Direction;
import funny.leet.api.system.animation.implement.DecelerateAnimation;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.item.ItemUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.math.StopWatch;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.render.Render2DUtil;
import funny.leet.common.util.render.ScissorManager;
import funny.leet.core.Main;
import funny.leet.implement.features.modules.combat.Aura;
import funny.leet.implement.features.modules.combat.TriggerBot;

import java.util.List;
import java.util.stream.StreamSupport;

public class TargetHud extends AbstractDraggable {

    private final Animation animation = new DecelerateAnimation().setMs(200).setValue(1);
    private final StopWatch stopWatch = new StopWatch();
    private LivingEntity lastTarget;
    private Item lastItem = Items.AIR;
    private float health;
    private float absorptionHealth;

    public TargetHud() {
        super("Target Hud", 10, 40, 100, 36, true);
    }

    @Override
    public boolean visible() {
        return scaleAnimation.isDirection(Direction.FORWARDS);
    }

    @Override
    public void tick() {
        LivingEntity auraTarget = Aura.getInstance().getTarget();
        LivingEntity triggerBotTarget = TriggerBot.getInstance().getTarget();

        if (auraTarget != null) {
            lastTarget = auraTarget;
            updateAbsorptionHealth();
            startAnimation();
        } else if (triggerBotTarget != null) {
            lastTarget = triggerBotTarget;
            updateAbsorptionHealth();
            startAnimation();
            stopWatch.reset();
        } else if (PlayerIntersectionUtil.isChat(mc.currentScreen)) {
            lastTarget = mc.player;
            updateAbsorptionHealth();
            startAnimation();
        } else if (stopWatch.finished(500)) {
            stopAnimation();
        }
    }

    private void updateAbsorptionHealth() {
        if (lastTarget != null) {
            absorptionHealth = lastTarget.getAbsorptionAmount();
        }
    }

    @Override
    public void drawDraggable(DrawContext context) {
        if (lastTarget != null) {
            MatrixStack matrix = context.getMatrices();
            drawUsingItem(context, matrix);
            drawArmor(context, matrix);
            drawMain(context, matrix);
            drawFace(context);
        }
    }

    private void drawMain(DrawContext context, MatrixStack matrix) {
        FontRenderer font = Fonts.getSize(18);
        float hp = PlayerIntersectionUtil.getHealth(lastTarget);
        float maxHealth = lastTarget.getMaxHealth();
        float widthHp = 61;
        String stringHp = PlayerIntersectionUtil.getHealthString(hp);
        health = MathHelper.clamp(MathUtil.interpolateSmooth(1, health, Math.round(hp / maxHealth * widthHp)), 2, widthHp);
        float absorption = lastTarget.getAbsorptionAmount();
        float totalHealthWidth = health + (absorption / maxHealth * widthHp);
        totalHealthWidth = MathHelper.clamp(totalHealthWidth, 0, widthHp);

        float blurSoftness = 1.2F;

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), getHeight())
                .round(6.5F).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.6f))
                .build());

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), getHeight())
                .round(6.5F).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.06f))
                .build());

        if (font.getStringWidth(lastTarget.getName().getString()) > 60) {
            ScissorManager scissorManager = Main.getInstance().getScissorManager();
            scissorManager.push(matrix.peek().getPositionMatrix(), getX(), getY(), getWidth() - 1, getHeight());
            font.drawGradientString(matrix, lastTarget.getName().getString(), getX() + 34, getY() + 8, ColorUtil.getText(), ColorUtil.getText(0.65F));
            scissorManager.pop();
        } else {
            font.drawString(matrix, lastTarget.getName().getString(), getX() + 34, getY() + 8, ColorUtil.getText());
        }

        if (absorption > 0) {
            float absorptionWidth = absorption / maxHealth * widthHp;
            rectangle.render(ShapeProperties.create(matrix, getX() + 34, getY() + 25F, absorptionWidth, 1.5F)
                    .round(0.75F).color(0xFFFFD700).build());
        }

        rectangle.render(ShapeProperties.create(matrix, getX() + 34, getY() + 27F, widthHp, 2F)
                .round(0.75F).color(0xFF060712).build());

        rectangle.render(ShapeProperties.create(matrix, getX() + 34, getY() + 27F, health, 2F)
                .softness(5).round(1).color(ColorUtil.roundClientColor(0.2F)).build());

        rectangle.render(ShapeProperties.create(matrix, getX() + 34, getY() + 27F, health, 2F)
                .round(0.75F).color(ColorUtil.roundClientColor(1)).build());

        float width = Fonts.getSize(14).getStringWidth(stringHp);
        Fonts.getSize(14).drawString(matrix, stringHp,
                getX() + MathHelper.clamp(34 + health - width / 2, 34, 95 - width),
                getY() + 21, ColorUtil.getText());
    }

    private void drawArmor(DrawContext context, MatrixStack matrix) {
        List<ItemStack> items = StreamSupport.stream(lastTarget.getEquippedItems().spliterator(), false)
                .filter(s -> !s.isEmpty()).toList();

        if (items.isEmpty()) return;

        float itemSize = 11;
        float totalWidth = items.size() * itemSize;
        float x = getX() + getWidth() / 2F - totalWidth / 2F;
        float y = getY() - 13;

        matrix.push();
        matrix.translate(x, y, -200);

        float blurSoftness = 1.2F;

        blur.render(ShapeProperties.create(matrix, 0, 0, totalWidth, itemSize)
                .round(3.5F).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.6f))
                .build());

        blur.render(ShapeProperties.create(matrix, 0, 0, totalWidth, itemSize)
                .round(3.5F).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.06f))
                .build());

        float itemX = 0;
        for (ItemStack stack : items) {
            Render2DUtil.defaultDrawStack(context, stack, itemX, 0.5F, false, true, 0.5F);
            itemX += itemSize;
        }

        matrix.pop();
    }

    private void drawUsingItem(DrawContext context, MatrixStack matrix) {
        animation.setDirection(lastTarget.isUsingItem() ? Direction.FORWARDS : Direction.BACKWARDS);

        if (!lastTarget.getActiveItem().isEmpty() && lastTarget.getActiveItem().getCount() != 0) {
            lastItem = lastTarget.getActiveItem().getItem();
        }

        if (!animation.isFinished(Direction.BACKWARDS) && !lastItem.equals(Items.AIR)) {
            int size = 24;
            float anim = animation.getOutput().floatValue();
            float progress = (lastTarget.getItemUseTime() + tickCounter.getTickDelta(false)) / ItemUtil.maxUseTick(lastItem) * 360;
            float x = getX() - (size + 5) * anim;
            float y = getY() + 6;

            ScissorManager scissorManager = Main.getInstance().getScissorManager();
            scissorManager.push(matrix.peek().getPositionMatrix(), getX() - 50, getY(), 50, getHeight());

            MathUtil.setAlpha(anim, () -> {
                blur.render(ShapeProperties.create(matrix, x, y, size, size)
                        .round(6.5F).softness(1).thickness(2)
                        .outlineColor(ColorUtil.getOutline(0.5F))
                        .color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.5f))
                        .build());

                arc.render(ShapeProperties.create(matrix, x, y, size, size)
                        .round(0.4F).thickness(0.2f).end(progress)
                        .color(ColorUtil.fade(0), ColorUtil.fade(200), ColorUtil.fade(0), ColorUtil.fade(200))
                        .build());

                Render2DUtil.defaultDrawStack(context, lastItem.getDefaultStack(), x + 3, y + 3, false, false, 1);
            });

            scissorManager.pop();
        }
    }

    private void drawFace(DrawContext context) {
        EntityRenderer<? super LivingEntity, ?> baseRenderer = mc.getEntityRenderDispatcher().getRenderer(lastTarget);
        if (!(baseRenderer instanceof LivingEntityRenderer<?, ?, ?>)) return;

        @SuppressWarnings("unchecked")
        LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?> renderer =
                (LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>) baseRenderer;

        LivingEntityRenderState state = renderer.getAndUpdateRenderState(lastTarget, tickCounter.getTickDelta(false));
        Identifier textureLocation = renderer.getTexture(state);

        Render2DUtil.drawTexture(context, textureLocation,
                getX() + 5, getY() + 5.5F, 25, 3, 8, 8, 64,
                ColorUtil.getRect(1), ColorUtil.multRed(-1, 1 + lastTarget.hurtTime / 4F));
    }
}
