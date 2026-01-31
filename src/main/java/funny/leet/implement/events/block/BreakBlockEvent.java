package funny.leet.implement.events.block;

import net.minecraft.util.math.BlockPos;
import funny.leet.api.event.events.Event;

public record BreakBlockEvent(BlockPos blockPos) implements Event {}
