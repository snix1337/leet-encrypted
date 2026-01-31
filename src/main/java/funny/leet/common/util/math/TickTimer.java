package funny.leet.common.util.math;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TickTimer {
    public int ticks = 0;

    public void reset() {
        ticks = 0;
    }
}