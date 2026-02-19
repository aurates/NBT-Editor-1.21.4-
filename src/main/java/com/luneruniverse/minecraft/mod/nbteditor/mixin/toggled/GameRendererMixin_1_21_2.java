package com.luneruniverse.minecraft.mod.nbteditor.mixin.toggled;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.luneruniverse.minecraft.mod.nbteditor.misc.Shaders;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.Reflection;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;

import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceType;

@Mixin(GameRenderer.class)
public class GameRendererMixin_1_21_2 {
	@Inject(method = "preloadPrograms", at = @At("HEAD"))
	private void preloadPrograms(ResourceFactory factory, CallbackInfo info) {
		LifecycledResourceManager manager = new LifecycledResourceManagerImpl(ResourceType.CLIENT_RESOURCES,
				MainUtil.client.getResourcePackManager().createResourcePacks());
		try {
			Class<?> keyClass = Reflection.getClass("net.minecraft.class_10156");
			int size = Shaders.SHADERS.size();
			Object typedKeys = java.lang.reflect.Array.newInstance(keyClass, size);
			for (int i = 0; i < size; i++) {
				java.lang.reflect.Array.set(typedKeys, i, Shaders.SHADERS.get(i).key.mcKey());
			}
			java.lang.reflect.Method preloadMethod = null;
			for (java.lang.reflect.Method m : ShaderLoader.class.getMethods()) {
				if (m.getName().equals("preload")) {
					preloadMethod = m;
					break;
				}
			}
			if (preloadMethod == null)
				throw new RuntimeException("Could not find ShaderLoader#preload method");
			preloadMethod.invoke(MainUtil.client.getShaderLoader(), manager, typedKeys);
		} catch (java.lang.reflect.InvocationTargetException e) {
			throw new RuntimeException("Could not preload shaders for loading UI", e.getCause());
		} catch (Exception e) {
			throw new RuntimeException("Could not preload shaders for loading UI", e);
		} finally {
			manager.close();
		}
	}
}
