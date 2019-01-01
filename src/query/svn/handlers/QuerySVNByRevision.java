package query.svn.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNLogEntry;

import query.svn.SVNListExecutorException;

/**
 * 
 * @author Zabee
 *
 */
public class QuerySVNByRevision extends SVNQueryHandler {

	private static QuerySVNByRevision queryByRevision = null;

	public static QuerySVNByRevision getInstance() {
		return queryByRevision == null ? queryByRevision = new QuerySVNByRevision() : queryByRevision;
	}

	private List<SVNLogEntry> theResultSVNLogEntries = new ArrayList<SVNLogEntry>();

	private QuerySVNByRevision() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SVNLogEntry> listFilesFromSVN(final List<SVNLogEntry> argLogEntries, final String[] argCommands)
			throws SVNListExecutorException {
		boolean isRevisionRange = argCommands.length - 1 == 3 ? true : false;
		long endRevisionPremitive = 0;
		Long startRevision = new Long(argCommands[1]);
		if (isRevisionRange) {
			Long endRevision = new Long(argCommands[2]);
			endRevisionPremitive = endRevision;
			theResultSVNLogEntries = argLogEntries.stream().filter(
					logEntry -> logEntry.getRevision() >= startRevision && logEntry.getRevision() <= endRevision)
					.collect(Collectors.toList());

		} else {
			theResultSVNLogEntries = argLogEntries.stream().filter(logEntry -> logEntry.getRevision() == startRevision)
					.collect(Collectors.toList());

		}

		if (theResultSVNLogEntries.isEmpty()) {
			throw new SVNListExecutorException(NO_RECORDS_FOUND + "	For revision " + startRevision
					+ (endRevisionPremitive == 0 ? "" : endRevisionPremitive));
		}

		return theResultSVNLogEntries;
	}
}
