package lumen.terminate_protocol.util.weapon;

import com.google.common.collect.Maps;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import net.minecraft.item.Item;

import java.util.Map;

public class WeaponCooldownManager {
    private final Map<WeaponItem, Integer> cooldowns = Maps.newHashMap();

    public boolean isCoolingDown(WeaponItem item) {
        return this.cooldowns.containsKey(item);
    }

    public boolean isCoolingDown(Item item) {
        if (item instanceof WeaponItem) return this.cooldowns.containsKey(item);
        return false;
    }

    public int getCooldownTicks(WeaponItem item) {
        return this.cooldowns.getOrDefault(item, 0);
    }

    public void update() {
        if (this.cooldowns.isEmpty()) return;

        this.cooldowns.entrySet().removeIf(entry -> {
            int newValue = entry.getValue() > 0 ? entry.getValue() - 1 : 0;
            if (newValue <= 0) {
                this.onCooldownUpdate(entry.getKey());
                return true;
            }
            entry.setValue(newValue);
            return false;
        });
    }

    public void set(WeaponItem item, int ticks) {
        this.cooldowns.put(item, ticks);
        this.onCooldownUpdate(item, ticks);
    }

    public void remove(WeaponItem item) {
        this.cooldowns.remove(item);
        this.onCooldownUpdate(item);
    }

    protected void onCooldownUpdate(WeaponItem item, int duration) {
    }

    protected void onCooldownUpdate(WeaponItem item) {
    }
}
