package query.svn.handlers;

import java.util.List;

import org.tmatesoft.svn.core.SVNLogEntry;

import query.svn.SVNListExecutorException;

/**
 * Not exactly an functional interface but still let it be.
 * 
 * @author Zabee
 * 
 *
 */
@FunctionalInterface
public interface SVNQueryInterface {

	public String NO_RECORDS_FOUND = "No records found for the given input!";

	public List<SVNLogEntry> listFilesFromSVN(final List<SVNLogEntry> argLogEntries, final String[] argCommand)
			throws SVNListExecutorException;
}
