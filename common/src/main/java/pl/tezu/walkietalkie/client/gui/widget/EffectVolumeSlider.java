package pl.tezu.walkietalkie.client.gui.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class EffectVolumeSlider extends AbstractSliderButton {

    public EffectVolumeSlider(int x, int y, int width, int height, float initialVolume) {
        super(x, y, width, height, buildMessage(initialVolume), Mth.clamp(initialVolume, 0.0f, 1.0f));
    }

    private static Component buildMessage(double value) {
        int percent = (int) Math.round(value * 100.0);
        return Component.translatable("gui.walkietalkie.volume", percent);
    }

    public float getVolume() {
        return (float) value;
    }

    public void setVolume(float volume) {
        this.value = Mth.clamp(volume, 0.0f, 1.0f);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(buildMessage(value));
    }

    @Override
    protected void applyValue() {
        onVolumeChanged(getVolume());
    }

    protected abstract void onVolumeChanged(float volume);
}

