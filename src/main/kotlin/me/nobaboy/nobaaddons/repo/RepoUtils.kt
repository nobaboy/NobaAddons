package me.nobaboy.nobaaddons.repo

import me.nobaboy.nobaaddons.utils.ErrorManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

/**
 * This is taken and modified from SkyHanni, which is licensed under the LGPL-2.1
 *
 * [Original source](https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/data/repo/RepoUtils.kt)
 *
 * Modifications include removing 2 unused functions
 */
object RepoUtils {
	fun recursiveDelete(path: Path) = recursiveDelete(path.toFile())

	fun recursiveDelete(file: File) {
		if(file.isDirectory && !Files.isSymbolicLink(file.toPath())) {
			file.listFiles().forEach(::recursiveDelete)
		}
		file.delete()
	}

	/**
	 * Modified from https://www.journaldev.com/960/java-unzip-file-example
	 */
	fun unzipIgnoreFirstFolder(zipFilePath: String, destinationDirectory: String) {
		val dir = File(destinationDirectory)
		// create output directory if it doesn't exist
		if(!dir.exists()) dir.mkdirs()
		val fis: FileInputStream
		// buffer for read and write data to file
		val buffer = ByteArray(1024)
		try {
			fis = FileInputStream(zipFilePath)
			val zis = ZipInputStream(fis)
			var ze = zis.nextEntry
			while(ze != null) {
				if(!ze.isDirectory) {
					var fileName = ze.name
					fileName = fileName.substring(fileName.split("/").toTypedArray()[0].length + 1)
					val newFile = File(destinationDirectory + File.separator + fileName)
					// create directories for sub directories in zip
					File(newFile.parent).mkdirs()
					if(!isInTree(dir, newFile)) {
						throw RuntimeException("Detected invalid zip file; this is a potential security risk! Please report this in the NobaAddons discord")
					}
					val fos = FileOutputStream(newFile)
					var len: Int
					while(zis.read(buffer).also { len = it } > 0) {
						fos.write(buffer, 0, len)
					}
					fos.close()
				}
				// close this ZipEntry
				zis.closeEntry()
				ze = zis.nextEntry
			}
			// close last ZipEntry
			zis.closeEntry()
			zis.close()
			fis.close()
		} catch(e: IOException) {
			ErrorManager.logError("Failed to unzip repository", e, ignorePreviousErrors = true)
		}
	}

	@Suppress("NAME_SHADOWING")
	@Throws(IOException::class)
	private fun isInTree(rootDirectory: File, file: File): Boolean {
		var rootDirectory = rootDirectory
		var file: File? = file
		file = file!!.canonicalFile
		rootDirectory = rootDirectory.canonicalFile
		while(file != null) {
			if(file == rootDirectory) return true
			file = file.parentFile
		}
		return false
	}
}