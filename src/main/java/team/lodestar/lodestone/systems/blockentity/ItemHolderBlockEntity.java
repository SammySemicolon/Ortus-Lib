package team.lodestar.lodestone.systems.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.*;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * A simple block entity which holds a single ItemStack
 */
public abstract class ItemHolderBlockEntity extends LodestoneBlockEntity implements IItemHandlerSupplier {
    public LodestoneBlockEntityInventory inventory;

    public ItemHolderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    @Override
    public IItemHandler getInventory(Direction direction) {
        return inventory;
    }

    @Override
    public ItemInteractionResult onUseWithItem(Player pPlayer, ItemStack pStack, InteractionHand pHand) {
        if (level instanceof ServerLevel serverLevel) {
            inventory.interact(serverLevel, pPlayer, pHand);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void onBreak(@Nullable Player player) {
        inventory.dumpItems(level, worldPosition);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        inventory.save(pRegistries, pTag);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        inventory.load(pRegistries, pTag);
        super.loadAdditional(pTag, pRegistries);
    }
}