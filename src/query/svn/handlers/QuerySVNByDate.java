package query.svn.handlers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNLogEntry;

import query.svn.SVNListExecutorException;

/**
 * 
 * YYYY-MM-DD E.g. "2014-02-14"
 * 
 * @author Zabee
 * @created August 10, 2018
 * 
 */
public class QuerySVNByDate extends SVNQueryHandler {

	private static QuerySVNByDate queryByDate = null;

	public static QuerySVNByDate getInstance() {
		return queryByDate == null ? queryByDate = new QuerySVNByDate() : queryByDate;
	}

	public static Date parseDate(String date) {
		try {
			return date != null ? new SimpleDateFormat("yyyy-MM-dd").parse(date) : null;
		} catch (ParseException e) {
			return null;
		}
	}

	private List<SVNLogEntry> theResultSVNLogEntries = new ArrayList<SVNLogEntry>();

	private QuerySVNByDate() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SVNLogEntry> listFilesFromSVN(final List<SVNLogEntry> argLogEntries, final String[] argCommand)
			throws SVNListExecutorException {
		boolean isDateRange = ((argCommand.length - 1 == 3 ? true : false) && (argCommand[3] != null));
		String strStartDate = argCommand[1], strEndDate = argCommand[2];

		if (isDateRange) {
			argLogEntries
					.parallelStream().filter(logEntry -> excludeCIBuildCommits(logEntry)
							&& sameDate(strStartDate, logEntry.getDate()) && sameDate(strEndDate, logEntry.getDate()))
					.collect(Collectors.toList());
		} else {
			// Huge list - Date comparison may have to check
			theResultSVNLogEntries = argLogEntries.stream()
					.filter((logEntry) -> excludeCIBuildCommits(logEntry) && sameDate(strStartDate, logEntry.getDate()))
					.collect(Collectors.toList());
		}
		if (theResultSVNLogEntries.isEmpty()) {
			throw new SVNListExecutorException(NO_RECORDS_FOUND);
		}
		return theResultSVNLogEntries;
	}

	private boolean sameDate(final String argDateInput, final Date argDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(argDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		// Starts from 0
		String strMonth = String.valueOf(month + 1);
		String strDay = String.valueOf(day);
		if (strMonth.length() == 1) {
			strMonth = "0" + strMonth;
		}
		if (strDay.length() == 1) {
			strDay = "0" + strDay;
		}
		String strDate = year + "-" + strMonth + "-" + strDay;
		return argDateInput.equals(strDate);
	}
}

// TODO - Sort by date then do a binary search
// theResultSVNLogEntries = logEntries.stream().filter((logEntry) ->
// logEntry.getDate().equals(argDate.toString()))
// .collect(Collectors.toList());
//for (SVNLogEntry logEntry : argLogEntries) {
//	Date date = logEntry.getDate();
//	int year = date.getYear() + 1900;
//	int month = date.getMonth();
//	int day = date.getDate();
//	String author = logEntry.getAuthor();
//	if (/* !(author.contains("xyz")) && */!excludeCIBuildCommits(logEntry)
//			&& ((year == 2018 && month == 06) && (day >= 5 || day == 8))) {
//		theResultSVNLogEntries.add(logEntry);
//	}
//}
