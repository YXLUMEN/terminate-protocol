package lumen.terminate_protocol.util;

import net.minecraft.sound.SoundEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SoundHelper {
    public static SingleSound of(SoundEvent soundEvent) {
        return of(soundEvent, 1.0f, 1.0f);
    }

    public static SingleSound of(SoundEvent soundEvent, float volume) {
        return of(soundEvent, volume, 1.0f);
    }

    public static SingleSound of(SoundEvent soundEvent, float volume, float pitch) {
        return new SingleSound(soundEvent, volume, pitch);
    }

    public static MultiSound of(SoundEvent... soundEvent) {
        return new MultiSound(Arrays.stream(soundEvent).map(SoundHelper::of).toList());
    }

    public static MultiSound of(SingleSound... sounds) {
        return new MultiSound(List.of(sounds));
    }

    public static MultiSound of(Collection<SingleSound> sounds) {
        return new MultiSound(List.copyOf(sounds));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<SingleSound> sounds = new ArrayList<>();

        public Builder add(SoundEvent sound, float volume) {
            return add(sound, volume, 1.0f);
        }

        public Builder add(SoundEvent sound, float volume, float pitch) {
            sounds.add(SoundHelper.of(sound, volume, pitch));
            return this;
        }

        public MultiSound build() {
            return SoundHelper.of(sounds);
        }
    }

    public record SingleSound(SoundEvent sound, float volume, float pitch) implements ISoundRecord {
    }

    public record MultiSound(List<SingleSound> sounds) implements ISoundRecord {
    }
}
