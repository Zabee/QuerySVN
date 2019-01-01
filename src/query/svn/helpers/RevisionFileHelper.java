package query.svn.helpers;

import java.io.*;
import java.util.*;

/**
 * The final output of this class will be a list of revision number read from a
 * file
 * 
 * @author Zabee
 *
 */
public class RevisionFileHelper {

	private static RevisionFileHelper revisionFileReader = null;

	public static RevisionFileHelper getInstance() {
		return revisionFileReader == null ? revisionFileReader = new RevisionFileHelper() : revisionFileReader;
	}

	private String revisionDetailsFileName = System.getProperty("user.dir") + "\\revisionsDetails.txt";

	private FileOutputStream fileOutputStream = null;

	private RevisionFileHelper() {
		try {
			fileOutputStream = new FileOutputStream(revisionDetailsFileName);
		} catch (FileNotFoundException f) {
			f.printStackTrace();
		}
	}

	public List<Long> getRevisionList() {
		List<Long> revisionNumbers = new ArrayList<Long>();
		StringBuffer stringBuff = new StringBuffer();
		byte[] fileBytesBuff = new byte[1000];
		System.out.println("Writing to " + revisionDetailsFileName);
		// Read a file and fill the list
		try (FileInputStream fileInputStream = new FileInputStream(System.getProperty("revisionFilePath"))) {

			while ((fileInputStream.read(fileBytesBuff)) != -1) {
				stringBuff.append(new String(fileBytesBuff));
			}
			if (stringBuff.length() > 0) {
				String token = null;
				StringTokenizer tokenizer = new StringTokenizer(stringBuff.toString(), "\n");
				while (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken().trim();
					revisionNumbers.add(Long.valueOf(token));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return revisionNumbers;
	}

	public void write(String revisionStr) {

		try {
			fileOutputStream.write(revisionStr.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
