package team.lodestar.lodestone.systems.blockentity;

import net.minecraft.core.*;
import net.neoforged.neoforge.items.*;

public interface IItemHandlerSupplier {

    public IItemHandler getInventory(Direction direction);
}
