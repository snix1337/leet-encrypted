package funny.leet.implement.screens.menu.components.implement.other;

import funny.leet.api.system.animation.Animation;
import funny.leet.api.system.animation.implement.DecelerateAnimation;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.implement.screens.menu.components.AbstractComponent;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import static funny.leet.api.system.animation.Direction.BACKWARDS;
import static funny.leet.api.system.animation.Direction.FORWARDS;

@Setter
@Accessors(chain = true)
public class CheckComponent extends AbstractComponent {
    private boolean state;
    private Runnable runnable;

    // --- Новые размеры для переключателя ---
    private final double componentWidth = 16;
    private final double componentHeight = 10;
    private final double thumbSize = 8; // Диаметр кружка

    // --- Новые анимации ---

    // Анимация для горизонтального смещения кружка
    private final Animation xOffsetAnimation = new DecelerateAnimation()
            .setMs(200)
            // Устанавливаем максимальное расстояние, на которое сместится кружок
            .setValue(componentWidth - thumbSize - 2); // 2 - это отступы по 1px с каждой стороны

    // Анимация для плавного изменения цвета фона (от 0.0f до 1.0f)
    private final Animation colorAnimation = new DecelerateAnimation()
            .setMs(200)
            .setValue(1);


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();

        // Устанавливаем направление анимации в зависимости от состояния (включено/выключено)
        xOffsetAnimation.setDirection(state ? FORWARDS : BACKWARDS);
        colorAnimation.setDirection(state ? FORWARDS : BACKWARDS);

        // Определяем цвета для состояний
        int offBackgroundColor = ColorUtil.getGuiRectColor(0.7F); // Серый цвет для выключенного состояния, темнее
        int onBackgroundColor = ColorUtil.getClientColor();     // Цвет клиента для включенного состояния
        int thumbColor = 0xFFFFFFFF;                          // Белый цвет для кружка

        // Плавно изменяем цвет фона с помощью интерполяции
        int animatedBackgroundColor = ColorUtil.interpolate(offBackgroundColor, onBackgroundColor, colorAnimation.getOutput().floatValue());

        // Отрисовываем фон (дорожку) переключателя
        rectangle.render(ShapeProperties.create(matrix, x - 8, y, componentWidth, componentHeight)
                .round((float) componentHeight / 2) // Полностью скругляем края
                .color(animatedBackgroundColor)
                .build());

        // Рассчитываем позицию кружка с учётом анимации и отступов
        double padding = (componentHeight - thumbSize) / 2; // Вертикальный отступ для центрирования
        double thumbX = x + padding + xOffsetAnimation.getOutput().doubleValue() - 8;
        double thumbY = y + padding;

        // Отрисовываем сам кружок
        rectangle.render(ShapeProperties.create(matrix, thumbX, thumbY, thumbSize, thumbSize)
                .round((float) thumbSize / 2) // Делаем из квадрата круг
                .color(thumbColor)
                .build());
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Проверяем, был ли клик в пределах новых размеров компонента
        if (MathUtil.isHovered(mouseX, mouseY, x - 10, y, componentWidth, componentHeight) && button == 0) {
            runnable.run(); // Выполняем действие, которое должно изменить состояние
            return true;    // Сообщаем, что клик был обработан
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
