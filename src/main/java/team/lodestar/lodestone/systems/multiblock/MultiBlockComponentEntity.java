package team.lodestar.lodestone.systems.multiblock;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.*;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.helpers.*;
import team.lodestar.lodestone.registry.common.*;
import team.lodestar.lodestone.systems.blockentity.*;

/**
 * A basic Multiblock component block entity. Defers some important actions to the core of the multiblock.
 */
public class MultiBlockComponentEntity extends LodestoneBlockEntity implements IItemHandlerSupplier {

    public BlockPos corePos;

    public MultiBlockComponentEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MultiBlockComponentEntity(BlockPos pos, BlockState state) {
        super(LodestoneBlockEntities.MULTIBLOCK_COMPONENT.get(), pos, state);
    }

    @Override
    public IItemHandler getInventory(Direction direction) {
        if (corePos != null && level.getBlockEntity(corePos) instanceof MultiBlockCoreEntity core && core instanceof IItemHandlerSupplier supplier) {
            return supplier.getInventory(direction);
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        if (corePos != null) {
            NBTHelper.saveBlockPos(pTag, corePos);
        }
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        corePos = NBTHelper.readBlockPos(pTag);
        super.loadAdditional(pTag, pRegistries);
    }

    @Override
    public ItemInteractionResult onUse(Player pPlayer, InteractionHand pHand) {
        if (corePos != null && level.getBlockEntity(corePos) instanceof MultiBlockCoreEntity core) {
            return core.onUse(pPlayer, pHand);
        }
        return super.onUse(pPlayer, pHand);
    }

    @Override
    public InteractionResult onUseWithoutItem(Player pPlayer) {
        if (corePos != null && level.getBlockEntity(corePos) instanceof MultiBlockCoreEntity core) {
            return core.onUseWithoutItem(pPlayer);
        }
        return super.onUseWithoutItem(pPlayer);
    }

    @Override
    public ItemInteractionResult onUseWithItem(Player pPlayer, ItemStack pStack, InteractionHand pHand) {
        if (corePos != null && level.getBlockEntity(corePos) instanceof MultiBlockCoreEntity core) {
            return core.onUseWithItem(pPlayer, pStack, pHand);
        }
        return super.onUseWithItem(pPlayer, pStack, pHand);
    }

    @Override
    public void onBreak(@Nullable Player player) {
        if (corePos != null && level.getBlockEntity(corePos) instanceof MultiBlockCoreEntity core) {
            core.onBreak(player);
        }
        super.onBreak(player);
    }
}