package jp.ats.uploader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Date;

import jp.ats.substrate.U;

public class Record {

	public final int id;

	public final String description;

	public final String fileName;

	public final Path file;

	public final String owner;

	Record(int id, String description, String fileName, Path file, String owner) {
		this.id = id;
		this.description = U.care(description);
		this.fileName = fileName;
		this.file = file;
		this.owner = owner;
	}

	public String getTimestamp() throws IOException {
		if (!exists()) return "";
		return U.formatDate("yyyy/MM/dd HH:mm:ss", new Date(
			Files.getLastModifiedTime(file).toMillis()));
	}

	public String getFileSize() throws IOException {
		if (!exists()) return "";
		return new DecimalFormat("#,##0").format(Files.size(file));
	}

	public boolean exists() {
		return Files.exists(file);
	}

	byte[] pack() {
		String packed = id
			+ "\t"
			+ U.care(description).replaceAll("\\s", " ")
			+ "\t"
			+ fileName
			+ "\t"
			+ file.toString()
			+ "\t"
			+ owner
			+ U.LINE_SEPARATOR;
		return packed.getBytes(UploadManager.charset);
	}

	static Record parse(String line) {
		String[] columns = U.care(new String[5]);

		String[] splitted = line.split("\\t");

		//レコードファイルの項目が増えた際の予防
		System.arraycopy(splitted, 0, columns, 0, splitted.length);

		return new Record(
			Integer.valueOf(columns[0]),
			columns[1],
			columns[2],
			Paths.get(columns[3]),
			columns[4]);
	}
}
