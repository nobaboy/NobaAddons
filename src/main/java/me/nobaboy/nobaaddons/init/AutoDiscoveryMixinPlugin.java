package me.nobaboy.nobaaddons.init;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This is taken and adapted from SkyHanni, which is licensed under the LGPL-2.1.
 * <br>
 * <a href="https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/mixins/init/SkyhanniMixinPlugin.java">Original source</a>
 */
public class AutoDiscoveryMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return false;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	public URL baseUrl(URL classUrl) {
		String string = classUrl.toString();
		if(classUrl.getProtocol().equals("jar")) {
			try {
				return URI.create(string.substring(4, string.lastIndexOf('!'))).toURL();
			} catch(MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		if(string.endsWith(".class")) {
			try {
				return URI.create(string.replace("\\", "/")
								.replace(getClass().getCanonicalName().replace(".", "/") + ".class", ""))
						.toURL();
			} catch(MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		return classUrl;
	}

	String mixinBasePackage = "me.nobaboy.nobaaddons.mixins.";
	String mixinBaseDir = mixinBasePackage.replace(".", "/");

	List<String> mixins = null;

	public void tryAddMixinClass(String className) {
		String norm = (className.endsWith(".class") ? className.substring(0, className.length() - ".class".length()) : className)
				.replace("\\", "/")
				.replace("/", ".");
		if(norm.startsWith(mixinBasePackage) && !norm.endsWith(".")) {
			mixins.add(norm.substring(mixinBasePackage.length()));
		}
	}

	public void walkDir(Path file) {
		System.out.println("Trying to find mixins from directory");
		try (Stream<Path> classes = Files.walk(file.resolve(mixinBaseDir))) {
			classes.filter(Files::isRegularFile)
					.map(it -> file.relativize(it).toString())
					.forEach(this::tryAddMixinClass);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getMixins() {
		if(mixins != null) return mixins;
		System.out.println("Trying to discover mixins");
		mixins = new ArrayList<>();
		URL classUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
		System.out.println("Found classes at " + classUrl);
		Path file;
		try {
			file = Paths.get(baseUrl(classUrl).toURI());
		} catch(URISyntaxException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Base directory found at " + file);
		if(Files.isDirectory(file)) {
			walkDir(file);
		} else {
			walkJar(file);
		}
		System.out.println("Found mixins: " + mixins);

		return mixins;
	}

	private void walkJar(Path file) {
		System.out.println("Trying to find mixins from jar file");
		try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
			ZipEntry next;
			while ((next = zis.getNextEntry()) != null) {
				tryAddMixinClass(next.getName());
				zis.closeEntry();
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}