package com.anjawanj.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BuildIndexTask implements Runnable {

	static Map<String, List<Posting>> index = null;
	public static CountDownLatch LATCH = null;

	private Path path = null;

	static {
		index = Index.getInstance();
	}

	public BuildIndexTask(Path path) {
		this.path = path;
	}

	@Override
	public void run() {
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("ISO-8859-1"))) {
			String completeFileName = path.getFileName().toAbsolutePath().toString();
			String currentLine = null;
			int lineNo = 0;

			while ((currentLine = reader.readLine()) != null) {
				lineNo++;
				StringTokenizer st = new StringTokenizer(currentLine, " ");
				while (st.hasMoreTokens()) {
					String s = st.nextToken();
					s = s.trim();
					// ignoring special characters for noww
					// add support to remove them and add line to index afterwards
					if (Patterns.SPECIAL_CHARACTERS.matcher(s).find()) {
						continue;
					}

					if (index.containsKey(s)) {
						List<Posting> postings1 = index.get(s);

						CopyOnWriteArrayList<Posting> postings = new CopyOnWriteArrayList<>(postings1);

						Integer docID = completeFileName.hashCode();

						Optional<Posting> posting = postings.stream()
								.filter(p -> p != null && p.getDocID().equals(docID))
								.collect(Collectors.reducing((a, b) -> null));

						Posting posting1 = null;
						if (posting.isPresent()) {
							posting1 = posting.get();
							posting1.setTermFrequency(posting1.getTermFrequency() + 1);
							posting1.setLineNos(lineNo);
						} else {
							Posting posting2 = new Posting();
							posting2.setDocID(docID);
							posting2.setTermFrequency(1);
							posting2.setDocumentName(completeFileName);
							posting2.setLineNos(lineNo);
							postings.add(posting2);
						}

					} else {
						List<Posting> postings = new ArrayList<Posting>();
						Posting posting = new Posting();
						posting.setDocID(completeFileName.hashCode());
						posting.setTermFrequency(1);
						posting.setDocumentName(completeFileName);
						posting.setLineNos(lineNo);
						postings.add(posting);
						index.put(s, postings);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			LATCH.countDown();
		}

	}

}
