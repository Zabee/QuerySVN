package query.svn.helpers;

import java.util.List;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * 
 * Decorate the file and console
 * 
 * @author Zabee
 *
 */
public class DecorHelper {
	private StringBuffer stringBuff = new StringBuffer();
	private long slNo = 0;
	
	String getDecorLine() {
		return "\n----------------------------------------------------------------------------------------------";
	}

	String getHeader(SVNLogEntry argSVNLogEntry) {
		return String.format(String.format("\nRevision: %d | Author : %s | Date %s", argSVNLogEntry.getRevision(),
				argSVNLogEntry.getAuthor(), argSVNLogEntry.getDate()));
	}

	void writeToConsole(SVNLogEntry argSVNLogEntry) {
		System.out.println(getDecorLine());
		System.out.println("\n" + argSVNLogEntry.getMessage());
		System.out.print(getDecorLine());
		System.out.println(getHeader(argSVNLogEntry));
		System.out.println("Changed paths:");
	}

	void appendToStringBuffer(StringBuffer argStringBuff, SVNLogEntry argSVNLogEntry) {
		argStringBuff.append(getDecorLine());
		slNo++;
		argStringBuff.append("\n(" + slNo +")" + argSVNLogEntry.getMessage());
		argStringBuff.append(getDecorLine());
		argStringBuff.append(getHeader(argSVNLogEntry));
		argStringBuff.append("Changed paths:");
	}

	public String decor(List<SVNLogEntry> argSVNLogEntries) {
		//For each revision
		argSVNLogEntries.stream().forEach(svnLogEntry -> {
			appendToStringBuffer(stringBuff, svnLogEntry);
			// OR
//			writeToConsole(svnLogEntry);
			int slNo = 0;
			//For each changed file
			if (svnLogEntry.getChangedPaths().size() > 0) {
				Set<String> theKeySet = svnLogEntry.getChangedPaths().keySet();
				theKeySet.stream().forEach(fileName -> {
					stringBuff.append("\n" + fileName);
//					System.out.println(fileName);
				});
			}
		});
		return stringBuff.toString();
	}

}
