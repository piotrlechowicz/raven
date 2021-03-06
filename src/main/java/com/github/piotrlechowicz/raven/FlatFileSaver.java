package com.github.piotrlechowicz.raven;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * <p>FlatFileSaver class.</p>
 *
 * @author Piotr Lechowicz
 * @version $Id: $Id
 */
public class FlatFileSaver {

	private static final Logger log = Logger.getLogger(FlatFileSaver.class);

	private static final String DEFAULT_DELIMITER = " ";

	protected File file;
	protected String delimiter;

	/**
	 * <p>Constructor for FlatFileSaver.</p>
	 *
	 * @param path a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 */
	public FlatFileSaver(String path) throws IOException {
		this(path, false, DEFAULT_DELIMITER);
	}

	/**
	 * @param path
	 * @param delimiter
	 * @throws IOException
	 */
	public FlatFileSaver(String path, String delimiter) throws IOException {
		this(path, false, delimiter);
	}

	/**
	 * <p>Constructor for FlatFileSaver.</p>
	 *
	 * @param path   a {@link java.lang.String} object.
	 * @param append a boolean.
	 * @throws java.io.IOException if any.
	 */
	public FlatFileSaver(String path, boolean append) throws IOException {
		this(path, append, DEFAULT_DELIMITER);
	}

	/**
	 * @param path
	 * @param append
	 * @param delimiter
	 * @throws IOException
	 */
	public FlatFileSaver(String path, boolean append, String delimiter) throws IOException {
		file = new File(path);
		file.getParentFile().mkdirs();

		if (!append && file.exists()) {
			file.delete();
			file.createNewFile();
		}
		this.delimiter = delimiter;
	}

	/**
	 * <p>save.</p>
	 *
	 * @param fileRow a {@link com.github.piotrlechowicz.raven.FileRow} object.
	 * @throws java.io.IOException if any.
	 */
	public void save(FileRow fileRow) throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
			writer.println(formatFileRow(fileRow));
		}
	}

	/**
	 * <p>save.</p>
	 *
	 * @param line a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 */
	public void save(String line) throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
			writer.println(line);
		}
	}

	/**
	 * <p>save.</p>
	 *
	 * @param fileRows a {@link java.util.List} object.
	 * @throws java.io.IOException if any.
	 */
	public void save(List<? extends FileRow> fileRows) throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
			fileRows.stream().forEach(element -> writer.println(formatFileRow(element)));
		}
	}

	private String formatFileRow(FileRow fileRow) throws IllegalRowFormatException {
		StringBuilder builder = new StringBuilder(100);
		for (int i = 0; i < fileRow.getNumberOfValues(); i++) {
			try {
				builder.append(String.format(fileRow.getFormatAt(i), fileRow.getValueAt(i)));
			} catch (IllegalFormatException e) {
				throw new IllegalRowFormatException("error in row=" + i + ", errorDescription=" + fileRow.getErrorDescriptionAt(i), e, i);
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}
}
