package jp.ats.uploader;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import jp.ats.substrate.U;
import jp.ats.webkit.util.FileUploadFilter;

import org.apache.commons.fileupload.FileItem;

public class UploadManager {

	static final Charset charset = Charset.forName("UTF-8");

	static final Path repository = Paths.get(
		System.getProperty("java.io.tmpdir"),
		"ats-uploader-repository").toAbsolutePath();

	private static final Path databaseFile = Paths.get(
		repository.toString(),
		"database").toAbsolutePath();

	private static final Pattern fileNamePattern = Pattern.compile("[\\\\/]([^\\\\/]+)$");

	public static synchronized Record[] read() throws IOException {
		LinkedList<Record> records = readFromStorage();

		Collections.reverse(records);

		return records.toArray(new Record[records.size()]);
	}

	public static synchronized void delete(int id) throws IOException {
		LinkedList<Record> records = readFromStorage();

		OutputStream out = new BufferedOutputStream(
			Files.newOutputStream(databaseFile));

		try {
			for (Record record : records) {
				if (record.id == id) {
					Files.deleteIfExists(record.file);
					continue;
				}

				out.write(record.pack());
				out.flush();
			}
		} finally {
			out.close();
		}
	}

	public static synchronized void store(HttpServletRequest request)
		throws IOException {
		LinkedList<Record> records = readFromStorage();

		int counter;
		if (records.size() == 0) {
			counter = 1;
		} else {
			counter = records.getLast().id + 1;
		}

		FileItem file = FileUploadFilter.getFileItem("file");

		if (file != null && file.get().length > 0) {
			store(
				counter,
				request.getParameter("description"),
				file,
				request.getUserPrincipal().getName());
		}
	}

	public static synchronized Record getRecord(int id) throws IOException {
		LinkedList<Record> records = readFromStorage();

		for (Record record : records) {
			if (id == record.id) return record;
		}

		throw new IllegalStateException();
	}

	private static void store(
		int next,
		String description,
		FileItem file,
		String owner) throws IOException {
		Path created = Files.createTempFile(repository, "upload.", ".dat");

		Files.write(created, file.get());

		Files.write(databaseFile, new Record(
			next,
			description,
			getFileName(file.getName()),
			created,
			owner).pack(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
	}

	private static LinkedList<Record> readFromStorage() throws IOException {
		if (Files.notExists(repository)) {
			Files.createDirectory(repository);
		}

		if (Files.notExists(databaseFile)) {
			Files.createFile(databaseFile);
		}

		LinkedList<Record> records = U.newLinkedList();

		for (String line : Files.readAllLines(databaseFile, charset)) {
			records.add(Record.parse(line));
		}

		return records;
	}

	private static String getFileName(String target) {
		Matcher matcher = fileNamePattern.matcher(target);

		if (!matcher.find()) return target;

		return matcher.group(1);
	}
}
