package query.svn.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNLogEntry;

import query.svn.SVNListExecutorException;

/**
 * Class to query SVN for given repository, revision number and display the
 * result below format
 * 
 * ------------------------------------------------------------------------
 * r1234 | Zabee | 2020-10-25 02:59:50 +0530 (Wed, 25 Oct 2020) Changed paths:
 * 
 * @author Zabee
 *
 */
public class QuerySVNByComment extends SVNQueryHandler {

	private static QuerySVNByComment queryByComment = null;

	public static QuerySVNByComment getInstance() {
		return queryByComment == null ? queryByComment = new QuerySVNByComment() : queryByComment;
	}

	private List<SVNLogEntry> theResultSVNLogEntries = new ArrayList<SVNLogEntry>();

	private QuerySVNByComment() {
	}

	/** {@inheritDoc} */
	@Override
	public List<SVNLogEntry> listFilesFromSVN(List<SVNLogEntry> argLogEntries, String[] argCommand)
			throws SVNListExecutorException {

		String commentSearchKey = argCommand[1];
		// Huge list
		theResultSVNLogEntries = argLogEntries.stream()
				.filter((logEntry) -> logEntry.getMessage().toUpperCase().contains(commentSearchKey.toUpperCase()))
				.collect(Collectors.toList());
		for(SVNLogEntry svnLogEntry : argLogEntries) {
			System.out.println("Checkin comment:" + svnLogEntry.getMessage());
		}
		if (theResultSVNLogEntries.isEmpty()) {
			throw new SVNListExecutorException(NO_RECORDS_FOUND);
		}
		return theResultSVNLogEntries;
	}

}