package steady.red.easyj;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;

public class EasyJ {

	public static void print(String value) {
		System.out.print(value);
	}

	public static void println(String value) {
		System.out.println(value);
	}

	public static List<String> getFilePathesListing(final File rootDirectory, final String[] extensions) {
		List<String> resultList = new ArrayList<String>();

		if (rootDirectory.exists() == true) {
			for (File file : getFilesListing(rootDirectory, extensions)) {
				resultList.add(file.getAbsolutePath());
			}
		}

		return resultList;
	}

	public static List<URL> getURLsListing(final File rootDirectory, final String[] extensions) {
		try {
			List<URL> resultList = new ArrayList<URL>();

			for (File file : getFilesListing(rootDirectory, extensions)) {
				resultList.add(file.toURI().toURL());
			}

			return resultList;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static String pquote(final String value) {
		return "\"" + value + "\"";
	}

	public static List<File> getFilesListing(final File rootDirectory, final String[] extensions) {
		List<File> resultList = new ArrayList<File>();

		final FileFilter fileFilter = new FileFilter() {
			final Set<String> extensionsSet = new HashSet<String>(Arrays.asList(extensions));

			@Override
			public boolean accept(File file) {
				if (file.isDirectory() == true) {
					return true;
				}

				String fileExtension = Files.getFileExtension(file.getName());

				return extensionsSet.contains(fileExtension);
			}
		};

		for (File file : createFileTraverser(fileFilter).preOrderTraversal(rootDirectory)) {
			if (file.isDirectory() == false) {
				resultList.add(file);
			}
		}

		return resultList;
	}

	public static void checkDirectory(File directory, String directoryType) {
		if (directory.exists() == false) {
			directory.mkdirs();
		}

		if (directory.exists() == false) {
			throw new RuntimeException("Could not create " + directoryType + " directory: \"" + directory.getAbsolutePath() + "\"");
		}

		if (directory.exists() == false) {
			throw new RuntimeException("Designated " + directoryType + " directory is not a directory: \"" + directory.getAbsolutePath() + "\"");
		}                            
	}

	public static TreeTraverser<File> createFileTraverser(final FileFilter fileFilter) {
		return new TreeTraverser<File>() {
			@Override
			public Iterable<File> children(File file) {
				// check isDirectory() just because it may be faster than listFiles() on a non-directory
				if (file.isDirectory()) {
					File[] files = file.listFiles(fileFilter);
					if (files != null) {
						return Collections.unmodifiableList(Arrays.asList(files));
					}
				}

				return Collections.emptyList();
			}
		};
	}


	/**
	 * Returns the string representation of this map.  The string displays the
	 * contents of the map, i.e. <code>[one:1, two:2, three:3]</code>.
	 *
	 * @param self a Map
	 * @return the string representation
	 * @since 1.0
	 */
	public static String toMapString(Map self) {
		return toMapString(self, -1);
	}

	/**
	 * Returns the string representation of this map.  The string displays the
	 * contents of the map, i.e. <code>[one:1, two:2, three:3]</code>.
	 *
	 * @param self a Map
	 * @param maxSize stop after approximately this many characters and append '...'
	 * @return the string representation
	 * @since 1.0
	 */
	public static String toMapString(Map self, int maxSize) {
		return (self == null) ? "null" : InvokerHelper.toMapString(self, maxSize);
	}

	/**
	 * Returns the string representation of the given collection.  The string
	 * displays the contents of the collection, i.e.
	 * <code>[1, 2, a]</code>.
	 *
	 * @param self a Collection
	 * @return the string representation
	 * @see #toListString(java.util.Collection)
	 * @since 1.0
	 */
	public static String toString(AbstractCollection self) {
		return toListString(self);
	}

	/**
	 * Returns the string representation of the given list.  The string
	 * displays the contents of the list, similar to a list literal, i.e.
	 * <code>[1, 2, a]</code>.
	 *
	 * @param self a Collection
	 * @return the string representation
	 * @since 1.0
	 */
	public static String toListString(Collection self) {
		return toListString(self, -1);
	}

	/**
	 * Returns the string representation of the given list.  The string
	 * displays the contents of the list, similar to a list literal, i.e.
	 * <code>[1, 2, a]</code>.
	 *
	 * @param self a Collection
	 * @param maxSize stop after approximately this many characters and append '...'
	 * @return the string representation
	 * @since 1.7.3
	 */
	public static String toListString(Collection self, int maxSize) {
		return (self == null) ? "null" : InvokerHelper.toListString(self, maxSize);
	}

	/**
	 * Returns the string representation of this array's contents.
	 *
	 * @param self an Object[]
	 * @return the string representation
	 * @see #toArrayString(java.lang.Object[])
	 * @since 1.0
	 */
	public static String toString(Object[] self) {
		return InvokerHelper.toArrayString(self);
	}

	/**
	 * Deletes a directory with all contained files and subdirectories.
	 * <p>The method returns
	 * <ul>
	 * <li>true, when deletion was successful</li>
	 * <li>true, when it is called for a non existing directory</li>
	 * <li>false, when it is called for a file which isn't a directory</li>
	 * <li>false, when directory couldn't be deleted</li>
	 * </ul>
	 *
	 *
	 * @param self a File
	 * @return true if the file doesn't exist or deletion was successful
	 * @since 1.6.0
	 */
	public static boolean deleteDir(final File self) {
		if (!self.exists())
			return true;
		if (!self.isDirectory())
			return false;

		File[] files = self.listFiles();
		if (files == null)
			// couldn't access files
			return false;

		// delete contained files
		boolean result = true;
		for (File file : files) {
			if (file.isDirectory()) {
				if (!deleteDir(file))
					result = false;
			} else {
				if (!file.delete())
					result = false;
			}
		}

		// now delete directory itself
		if (!self.delete())
			result = false;

		return result;
	}

	/**
	 * Deletes a directory with all contained files and subdirectories.
	 * <p>The method returns
	 * <ul>
	 * <li>true, when deletion was successful</li>
	 * <li>true, when it is called for a non existing directory</li>
	 * <li>false, when it is called for a file which isn't a directory</li>
	 * <li>false, when directory couldn't be deleted</li>
	 * </ul>
	 *
	 *
	 * @param self a File
	 * @return true if the file doesn't exist or deletion was successful
	 * @since 1.6.0
	 */
	public static boolean deleteDirectoryContents(final File self) {
		if (!self.exists())       
			return false;

		if (!self.isDirectory())
			return false;

		File[] files = self.listFiles();
		if (files == null)
			// couldn't access files
			return false;

		// delete contained files
		boolean result = true;
		for (File file : files) {
			if (file.isDirectory()) {
				if (!deleteDir(file))
					result = false;
			} else {
				if (!file.delete())
					result = false;
			}
		}

		return result;
	}

}

