/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.event.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.class_10370;
import net.minecraft.class_10371;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
	@Shadow
	@Final
	private ServerPlayerEntity player;

	@Shadow
	private void method_65098(ItemStack stack) {
		throw new AssertionError();
	}

	@WrapOperation(method = "method_65085", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;method_65171(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/item/ItemStack;"))
	public ItemStack onPickItemFromBlock(BlockState state, WorldView world, BlockPos pos, Operation<ItemStack> operation, @Local class_10370 packet) {
		ItemStack stack = PlayerPickItemEvents.BLOCK.invoker().onPickItemFromBlock(player, pos, state, packet.includeData());

		if (stack == null) {
			return operation.call(state, world, pos);
		} else if (!stack.isEmpty()) {
			this.method_65098(stack);
		}

		// Prevent vanilla data-inclusion behavior
		return ItemStack.EMPTY;
	}

	@WrapOperation(method = "onPickFromInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPickBlockStack()Lnet/minecraft/item/ItemStack;"))
	public ItemStack onPickItemFromEntity(Entity entity, Operation<ItemStack> operation, @Local class_10371 packet) {
		ItemStack stack = PlayerPickItemEvents.ENTITY.invoker().onPickItemFromEntity(player, entity, packet.includeData());

		if (stack == null) {
			return operation.call(entity);
		} else if (!stack.isEmpty()) {
			this.method_65098(stack);
		}

		// Prevent vanilla data-inclusion behavior
		return ItemStack.EMPTY;
	}
}
