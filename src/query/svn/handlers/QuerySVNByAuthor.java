package query.svn.handlers;

import java.util.List;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNLogEntry;

import query.svn.SVNListExecutorException;

public class QuerySVNByAuthor extends SVNQueryHandler {

	private static QuerySVNByAuthor querySVNByAuthor = null;

	public static QuerySVNByAuthor getInstance() {
		return querySVNByAuthor == null ? querySVNByAuthor = new QuerySVNByAuthor() : querySVNByAuthor;
	}

	private List<SVNLogEntry> theResultSVNLogEntries = null;

	private QuerySVNByAuthor() {
	}

	@Override
	public List<SVNLogEntry> listFilesFromSVN(final List<SVNLogEntry> argLogEntries, final String[] argCmd)
			throws SVNListExecutorException {
		// Don't worry proper validations are done, Exceptions like IndexOut.. IOBE will not occur
		String author = argCmd[1];

		// Huge list
		theResultSVNLogEntries = argLogEntries.stream()
				.filter((logEntry) -> logEntry.getAuthor().equalsIgnoreCase(author)).collect(Collectors.toList());

		if (theResultSVNLogEntries == null || theResultSVNLogEntries.isEmpty()) {
			throw new SVNListExecutorException(NO_RECORDS_FOUND);
		}

		return theResultSVNLogEntries;
	}

}
