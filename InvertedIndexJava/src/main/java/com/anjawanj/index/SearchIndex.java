package com.anjawanj.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SearchIndex {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

		String indexFile = args[0];
		String searchTerm = args[1];
		ConcurrentHashMap<String, List<Posting>> retrievedIndex = null;

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(indexFile))) {

			// Method for deserialization of object
			retrievedIndex = (ConcurrentHashMap<String, List<Posting>>) in.readObject();

			Set<String> indexedWords = retrievedIndex.keySet();

			String searchTermInLower = searchTerm.toLowerCase();

			Optional<String> matchedIndex = indexedWords.stream().map(String::toLowerCase)
					.filter(line -> line.contains(searchTermInLower)).findFirst();

			String matchedWord = matchedIndex.get();

			List<Posting> postings = retrievedIndex.get(matchedWord);

			postings.forEach(new Consumer<Posting>() {
				@Override
				public void accept(Posting p) {
					System.out.println("Document ID:--" + p.getDocID() + "	Document Name---" + p.getDocumentName()
							+ "	Frequency---" + p.getTermFrequency());

					List<Integer> lineNos = p.getLineNos();

					lineNos.forEach(lineNo -> System.out.println(lineNo));
				}
			});

		}

	}

}
