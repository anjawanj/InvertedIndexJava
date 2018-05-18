import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

class BuildIndex {

	public static void main(String args[]) {

		Collection<Path> all = new ArrayList<Path>();
		Path path = Paths.get(args[0]);
		HashMap<String, List<Posting>> index = new HashMap<String, List<Posting>>();
		StringTokenizer st = null;

		try {

			addTree(path, all);

			for (Path p : all) {

				String completeFileName = p.getFileName().toAbsolutePath().toString();

				if (completeFileName.endsWith(".txt") || completeFileName.endsWith(".log")) {

					BufferedReader reader = Files.newBufferedReader(p, Charset.forName("US-ASCII"));
					String currentLine = null;
					Integer lineNo = 0;

					while ((currentLine = reader.readLine()) != null) {
						lineNo++;
						st = new StringTokenizer(currentLine, " ");
						while (st.hasMoreTokens()) {
							String s = st.nextToken();
							if (index.containsKey(s)) {
								List<Posting> postings = index.get(s);

								Integer docID = completeFileName.hashCode();
								boolean isDocThere = false;
								for (Posting posting : postings) {
									if (posting.getDocID().equals(docID)) {
										posting.setTermFrequency(posting.getTermFrequency() + 1);
										posting.setLineNos(lineNo);
										isDocThere = true;
										break;
									}
								}

								if (!isDocThere) {
									Posting posting = new Posting();
									posting.setDocID(docID);
									posting.setTermFrequency(1);
									posting.setDocumentName(completeFileName);
									posting.setLineNos(lineNo);
									postings.add(posting);
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

				}

			}

			Calendar cal = Calendar.getInstance();
			String fileName = "Index" + cal.getTimeInMillis();
			File f = new File(fileName);
			FileOutputStream file = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(index);

			out.close();
			file.close();

		} catch (IOException e) {
			System.out.println(e.toString());
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
