package funny.leet.common.util.math;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;

 /**
  * Утилита для управления временными интервалами, оптимизированная для использования в Minecraft.
  * Позволяет отслеживать прошедшее время, проверять достижение заданного интервала и учитывать TPS сервера.
  */
public class StopWatch {
    @Getter
    private long startTime;
    private long lastMS = System.currentTimeMillis();
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Сбрасывает таймер на текущее время.
     */

    // added
    public StopWatch() {
        reset();
    }

     /**
     * Проверяет, прошло ли указанное количество времени с последнего сброса.
     *
     * @param time Время в миллисекундах.
     * @return true, если прошло указанное время.
     */
    public boolean hasReached(long time) {
         return System.currentTimeMillis() - lastMS >= time;
     }

    /**
     * Проверяет, прошло ли указанное количество времени с учетом TPS сервера.
     *
     * @param time Время в миллисекундах.
     * @param tps  Текущий TPS сервера (тиков в секунду).
     * @return true, если прошло указанное время с учетом TPS.
     */
    public boolean hasReachedWithTPS(long time, float tps) {
        float tpsFactor = 20.0f / Math.max(tps, 1.0f); // Учитываем TPS, избегаем деления на 0
        return System.currentTimeMillis() - lastMS >= time * tpsFactor;
    }

    /**
     * Устанавливает последнее время с дополнительным смещением.
     *
     * @param offset Смещение в миллисекундах (может быть отрицательным).
     */
    public void setLastMS(long offset) {
         lastMS = System.currentTimeMillis() + offset;
     }

    /**
     * Устанавливает абсолютное значение времени.
     *
     * @param time Время в миллисекундах.
     */
    public void setTime(long time) {
         lastMS = time;
     }

    /**
     * Возвращает прошедшее время с момента последнего сброса.
     *
     * @return Прошедшее время в миллисекундах.
     */
    public long getElapsedTime() {
         return System.currentTimeMillis() - lastMS;
     }

    /**
     * Проверяет, работает ли таймер (время еще не истекло).
     *
     * @return true, если таймер работает.
     */
    public boolean isRunning() {
         return System.currentTimeMillis() - lastMS <= 0;
     }

    public boolean isReached(long time) {
         return System.currentTimeMillis() - lastMS > time;
     }

    /**
     * Проверяет, истекло ли время с момента последнего сброса.
     *
     * @return true, если время истекло.
     */
    public boolean hasTimeElapsed() {
         return lastMS < System.currentTimeMillis();
     }

    // added
    public StopWatch setMs(long ms) {
        this.startTime = System.currentTimeMillis() - ms;
        return this;
    }

    // added
    public boolean finished(final double delay) {
        return System.currentTimeMillis() - delay >= startTime;
    }

    // added
     public boolean every(final double delay) {
         boolean finished = this.finished(delay);
         if (finished) reset();
         return finished;
     }

     // added
     public void reset() {
         this.startTime = System.currentTimeMillis();
     }

     // added
     public int elapsedTime() {
         return Math.toIntExact(System.currentTimeMillis() - this.startTime);
     }
 }
