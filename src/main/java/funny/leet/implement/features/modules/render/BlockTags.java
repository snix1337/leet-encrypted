package funny.leet.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.Pair;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.common.util.render.Render3DUtil;
import funny.leet.implement.events.block.BlockUpdateEvent;
import funny.leet.implement.events.render.WorldLoadEvent;
import funny.leet.implement.events.render.WorldRenderEvent;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockTags extends Module {
    Map<BlockPos, Pair<VoxelShape, Integer>> map = new HashMap<>();

    SelectSetting modeSetting = new SelectSetting("Mode", "Operating mode for BlockTags").value("Block Update");
    MultiSelectSetting blockTypeSetting = new MultiSelectSetting("Blocks", "Blocks that will be displayed")
            .value("Ancient Debris", "Diamond", "Emerald", "Iron", "Gold", "Chest", "Ender Chest", "Furnace", "Obsidian");

    public BlockTags() {
        super("Block Tags", ModuleCategory.RENDER);
        setup(modeSetting, blockTypeSetting);
    }

    @Override
    public void deactivate() {
        map.clear();
        super.deactivate();
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent e) {
        map.forEach((pos, pair) -> {
            Render3DUtil.drawShape(pos, pair.getLeft(), pair.getRight(), 1);
        });
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        map.clear();
    }

    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent e) {
        BlockPos pos = e.pos();
        BlockState state = e.state();
        String blockName = getBlockName(state).toLowerCase();
        switch (e.type()) {
            case LOAD -> {
                if (blockTypeSetting.getSelected().stream().anyMatch(type -> getBlockName(state).toLowerCase().contains(type.toLowerCase()))) {
                    putBlock(pos, state);
                }
            }
            case UPDATE -> {
                if (blockTypeSetting.getSelected().stream().anyMatch(type -> getBlockName(state).toLowerCase().contains(type.toLowerCase())) && !map.containsKey(pos)) {
                    putBlock(pos, state);
                }
                if (map.containsKey(pos) && !map.get(pos).getLeft().equals(state.getOutlineShape(mc.world, pos))) {
                    map.remove(pos);
                }
            }
            case UNLOAD -> map.remove(pos);
        }
    }

    private void putBlock(BlockPos pos, BlockState state) {
        VoxelShape shape = state.getOutlineShape(mc.world, pos);
        int color = getColorByBlock(state);
        map.put(pos, new Pair<>(shape, color));
    }

    private int getColorByBlock(BlockState block) {
        return switch (getBlockName(block).toLowerCase()) {
            case "ancient debris" -> 0xFFA67554;
            case "diamond" -> 0xFF197B81;
            case "emerald" -> 0xFF41871B;
            case "iron" -> 0xFF754C1F;
            case "gold" -> 0xFFC5B938;
            case "chest" -> 0xFF8B4513; // золотистый
            case "ender chest" -> 0xFF87CEEB; // светло-сиреневый с синим оттенком и изумнрудными тонами
            case "furnace" -> 0xFFA52A2A; // темно-красный
            case "obsidian" -> 0xFF1C2525; // темно серо-черный
            default -> -1;
        };
    }

    private String getBlockName(BlockState state) {
        String baseName = state.getBlock().asItem().toString().replace("minecraft:", "").replace("_", " ");
        if (baseName.contains("furnace") && !baseName.equals("furnace")) {
            return "";
        }
        return baseName.replace("_ore", "");
    }
}
