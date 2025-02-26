package team.lodestar.lodestone.systems.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.*;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.systems.block.LodestoneBlockProperties;
import team.lodestar.lodestone.systems.datagen.LodestoneDatagenBlockData;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.*;

public abstract class LodestoneBlockTagsProvider extends BlockTagsProvider {

    public LodestoneBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    public void addTagsFromBlockProperties(Set<DeferredHolder<Block, ? extends Block>> blocks) {
        var blockList = blocks.stream().map(DeferredHolder::get).sorted(Comparator.comparingInt((Block b) -> BuiltInRegistries.BLOCK.getId(b))).toList();
        for (Block block : blockList) {
            LodestoneBlockProperties properties = (LodestoneBlockProperties) block.properties();
            LodestoneDatagenBlockData data = properties.getDatagenData();
            for (TagKey<Block> tag : data.getTags()) {
                tag(tag).add(block);
            }
        }
    }
}
