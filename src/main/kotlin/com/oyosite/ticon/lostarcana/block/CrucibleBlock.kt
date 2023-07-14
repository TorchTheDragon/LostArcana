package com.oyosite.ticon.lostarcana.block

import com.oyosite.ticon.lostarcana.LostArcana
import com.oyosite.ticon.lostarcana.block.entity.CrucibleBlockEntity
import com.oyosite.ticon.lostarcana.mixin.MinecraftClientAccessor
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil
import net.minecraft.block.*
import net.minecraft.block.cauldron.CauldronBehavior
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION", "UnstableApiUsage")
open class CrucibleBlock: AbstractCauldronBlock(FabricBlockSettings.create(), CRUCIBLE_BEHAVIOR), BlockEntityProvider {

    private val RAYCAST_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0)
    protected val OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
        VoxelShapes.fullCube(), VoxelShapes.union(
            createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
            createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            RAYCAST_SHAPE
        ), BooleanBiFunction.ONLY_FIRST
    )
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = CrucibleBlockEntity(pos, state)

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape = OUTLINE_SHAPE
    override fun getRaycastShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape = RAYCAST_SHAPE

    override fun isFull(state: BlockState?): Boolean = true



    override fun onSyncedBlockEvent(state: BlockState?, world: World, pos: BlockPos?, type: Int, data: Int): Boolean {
        super.onSyncedBlockEvent(state, world, pos, type, data)
        val blockEntity = world.getBlockEntity(pos) ?: return false
        return blockEntity.onSyncedBlockEvent(type, data)
    }

    override fun scheduledTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        val blockPos = PointedDripstoneBlock.getDripPos(world, pos) ?: return
        val fluid = PointedDripstoneBlock.getDripFluid(world, blockPos)
        if (fluid !== Fluids.EMPTY && canBeFilledByDripstone(fluid)) {
            fillFromDripstone(state, world, pos, fluid)
        }
    }

    override fun canBeFilledByDripstone(fluid: Fluid): Boolean = fluid == Fluids.WATER

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if(FluidStorageUtil.interactWithFluidStorage(FluidStorage.SIDED.find(world, pos, null), player, hand))return ActionResult.SUCCESS
        /*FluidStorage.ITEM.find(player.getStackInHand(hand), ContainerItemContext.forPlayerInteraction(player, hand))?.also{ itemStorage ->
            FluidStorage.SIDED.find(world, pos, null)?.also{ crucibleStorage ->

            }

        }*/

        if(world.isClient){
            assert(MinecraftClient.getInstance() is MinecraftClientAccessor)
            val mca: MinecraftClientAccessor = MinecraftClient.getInstance() as MinecraftClientAccessor
            println(mca.blockEntityRenderDispatcher[world.getBlockEntity(pos, LostArcana.CRUCIBLE_BLOCK_ENTITY).get()])
        }


        return super.onUse(state, world, pos, player, hand, hit)
    }

    companion object{
        val CRUCIBLE_BEHAVIOR: Object2ObjectOpenHashMap<Item, CauldronBehavior> = CauldronBehavior.createMap().apply{defaultReturnValue(::dissolveItem)}

        fun dissolveItem(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, stack: ItemStack): ActionResult{
            //TODO(Implement this)
            return ActionResult.PASS
        }
    }
}