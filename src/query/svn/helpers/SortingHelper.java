package query.svn.helpers;

import java.util.Comparator;

import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * To handle sorting argument. TODO - Mutiple sorting options irrespective of
 * list by option (author, date, revision number etc.,)
 * 
 * @author Zabee
 *
 */
public class SortingHelper implements Comparator<SVNLogEntry> {

	@Override
	public int compare(SVNLogEntry o1, SVNLogEntry o2) {
		return 0;
	}

}
