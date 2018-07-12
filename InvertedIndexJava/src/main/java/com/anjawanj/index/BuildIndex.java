package com.anjawanj.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class BuildIndex {

	public static void main(String args[]) throws InterruptedException, IOException {

		List<Path> all = new ArrayList<Path>();
		Path path = Paths.get(args[0]);

		addTree(path, all);

		ExecutorService executor = Executors.newFixedThreadPool(10);

		List<Path> toAddToIndex = new ArrayList<Path>();

		for (Path p : all) {

			String fileName = p.getFileName().toString();

			if (!isFileReadable(fileName)) {
				continue;
			}

			toAddToIndex.add(p);

		}

		CountDownLatch latch = new CountDownLatch(toAddToIndex.size());
		BuildIndexTask.LATCH = latch;

		for (Path p : toAddToIndex) {
			BuildIndexTask indexTask = new BuildIndexTask(p);

			executor.execute(indexTask);
		}

		latch.await();
		awaitTerminationAfterShutdown(executor);

		Calendar cal = Calendar.getInstance();
		String fileName = "Index" + cal.getTimeInMillis();

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(fileName)))) {
			Map<String, List<Posting>> index = Index.getInstance();
			out.writeObject(index);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public static boolean isFileReadable(String fileName) {
		if (fileName.endsWith(FileExtensions.TXT) || fileName.endsWith(FileExtensions.LOG)) {
			return true;
		} else {
			return false;
		}
	}

	public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
			}
		} catch (InterruptedException ex) {
			threadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	public static void addTree(Path directory, Collection<Path> all) throws IOException {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
			for (Path child : ds) {

				DosFileAttributes dfa = Files.readAttributes(child, DosFileAttributes.class);

				if (dfa.isDirectory() && !dfa.isHidden() && !dfa.isSystem() && !dfa.isSymbolicLink()) {
					addTree(child, all);
				}

				if (dfa.isRegularFile() && !dfa.isOther() && !dfa.isSymbolicLink() && !dfa.isSystem()
						&& !dfa.isHidden()) {
					all.add(child);
				}
			}
		}
	}

}
