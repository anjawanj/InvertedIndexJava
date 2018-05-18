package Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

public class SearchIndex {

	public static void main(String[] args) {
		
		String indexFile = args[0];
		String searchTerm  = args[1];
		
		HashMap<String,List<Posting>> object1 = null;
        try
        {   
            // Reading the object from a file
            FileInputStream file = new FileInputStream(indexFile);
            ObjectInputStream in = new ObjectInputStream(file);
             
            // Method for deserialization of object
            object1 = (HashMap<String,List<Posting>>)in.readObject();
           
			found: {
				for (String ss : object1.keySet()) {
					if (ss.equals(searchTerm)) {
						System.out.println("Found");
						List<Posting> poss = object1.get(ss);

						for (Posting p : poss) {
							System.out.println("Document ID:--" + p.docID + "	Document Name---" + p.documentName
									+ "	Frequency---" + p.getTermFrequency());
							System.out.println("Line Numbers");
							for (Integer no : p.getLineNos()) {
								System.out.println("Number--" + no);
							}
						}
						break found;
					}					
					
				}
				System.out.println("Not Found");
			}
           
            in.close();
            file.close();        
           
        }
         
        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
         
        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
 

	}

}
