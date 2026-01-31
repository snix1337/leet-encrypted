package funny.leet.implement.screens.menu.components.implement.other;

import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.math.MathUtil;
import funny.leet.implement.screens.menu.components.AbstractComponent;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;

@Setter
@Accessors(chain = true)
public class SettingComponent extends AbstractComponent {
    private Runnable runnable;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        image.setTexture("textures/settings.png").render(ShapeProperties.create(context.getMatrices(), x - 4.8F, y + 1.245F, 7, 7).color(0xFFafb0bc).build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x - 4.8F, y + 1.245F, 7, 7) && button == 0) {
            runnable.run();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
