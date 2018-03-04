package dynamictreesbop.proxy;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;

import biomesoplenty.api.enums.BOPTrees;
import biomesoplenty.common.block.BlockBOPLeaves;
import dynamictreesbop.DynamicTreesBOP;
import dynamictreesbop.ModContent;
import dynamictreesbop.event.ModelBakeEventListener;
import dynamictreesbop.items.ItemMagicSeed;
import dynamictreesbop.items.ItemMapleSeed;
import dynamictreesbop.models.ModelLoaderBamboo;
import dynamictreesbop.renderers.RenderMagicSeed;
import dynamictreesbop.renderers.RenderMapleSeed;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
		registerClientEventHandlers();
		registerEntityRenderers();
		
		ModelLoaderRegistry.registerLoader(new ModelLoaderBamboo());
	}
	
	@Override
	public void init() {
		super.init();
		registerColorHandlers();
	}
	
	@Override public void postInit() {
		super.postInit();
	}

	public void registerColorHandlers() {	
		
		final int magenta = 0x00FF00FF; // for errors.. because magenta sucks.
		
		ModelHelper.regColorHandler(ModContent.floweringOakLeaves, new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				boolean inWorld = worldIn != null && pos != null;
				Block block = state.getBlock();
				
    			if (inWorld && tintIndex == 0 && TreeHelper.isLeaves(block)) {
					return ((BlockDynamicLeaves) block).getProperties(state).foliageColorMultiplier(state, worldIn, pos);
    			}
    			return 0xffffff;
			}
		});
		
		ModelHelper.regColorHandler(ModContent.palmFrondLeaves, new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				boolean inWorld = worldIn != null && pos != null;
				Block block = state.getBlock();
				
    			if (inWorld && tintIndex == 0 && TreeHelper.isLeaves(block)) {
					return ((BlockDynamicLeaves) block).getProperties(state).foliageColorMultiplier(state, worldIn, pos);
    			}
    			return 0xffffff;
			}
		});
		
		for (BlockDynamicLeaves leaves: TreeHelper.getLeavesMapForModId(DynamicTreesBOP.MODID).values()) {
			ModelHelper.regColorHandler(leaves, new IBlockColor() {
				@Override
				public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
					boolean inWorld = worldIn != null && pos != null;
					
					IBlockState primLeaves = leaves.getProperties(state).getPrimitiveLeaves();
					Block block = state.getBlock();
					
					if (primLeaves.getBlock() instanceof BlockBOPLeaves) {
		            	switch (BlockBOPLeaves.getColoringType((BOPTrees) primLeaves.getValue(((BlockBOPLeaves) primLeaves.getBlock()).variantProperty))) {
		            		case TINTED:
								if(inWorld && TreeHelper.isLeaves(block)) {
									return ((BlockDynamicLeaves) block).getProperties(state).foliageColorMultiplier(state, worldIn, pos);
								}
								return magenta;
		            		case OVERLAY:
		            			if (inWorld && tintIndex == 0) {
		    						if(TreeHelper.isLeaves(block)) {
		    							return ((BlockDynamicLeaves) block).getProperties(state).foliageColorMultiplier(state, worldIn, pos);
		    						}
		    						return magenta;
		            			}
		            		default:
		            			return 0xffffff;
		            	}
					} else {
						if(TreeHelper.isLeaves(block)) {
							return ((BlockDynamicLeaves) block).getProperties(state).foliageColorMultiplier(state, worldIn, pos);
						}
						return magenta;
					}
				}
			});
		}

		for(TreeFamily tree: ModContent.trees) {
			if (tree.getName().getResourcePath().equals("decayed")) continue;
			if (tree.getName().getResourcePath().equals("dead")) continue;
			BlockDynamicSapling sapling = (BlockDynamicSapling) tree.getCommonSpecies().getDynamicSapling().getBlock();
			ModelHelper.regDynamicSaplingColorHandler(sapling);
		}
		
		BlockDynamicSapling sapling = (BlockDynamicSapling) TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesBOP.MODID, "floweringoak")).getDynamicSapling().getBlock();
		ModelHelper.regColorHandler(sapling, new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
				return access == null || pos == null ? -1 : tintIndex != 0 ? 0xffffff : sapling.getSpecies(access, pos, state).getLeavesProperties().foliageColorMultiplier(state, access, pos);
			}
		});
	}
	
	public void registerClientEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new ModelBakeEventListener());
	}
	
	public void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(ItemMapleSeed.EntityItemMapleSeed.class, new RenderMapleSeed.Factory());
		RenderingRegistry.registerEntityRenderingHandler(ItemMagicSeed.EntityItemMagicSeed.class, new RenderMagicSeed.Factory());
	}
	
}
