package query.svn;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNLogEntry;

//import query.svn.handlers.QuerySVNByComment;
//import query.svn.handlers.QuerySVNByDate;
import query.svn.handlers.*;

/**
 * 
 * @author Zabee
 *
 */
public class QueryFactory {

	// Get files list by Author -a

	// Get files list by revision number -r

	// Get files list by revision numbers range -rr. Comma seperated

	// Get files list by date -d

	// Get files list by date range -dr

	// Get files list by -a and date - d OR date range -dd
	private static SVNQueryHandler svnQueryHandler = null;

	public static SVNQueryHandler getQuerySVNHandler(String[] argParsedCmd) throws SVNListExecutorException {
		try {
			List<String> parsedCmdList = Arrays.asList(argParsedCmd);
			parsedCmdList = parsedCmdList.parallelStream().filter(cmd -> cmd != null && cmd.length() > 1)
					.collect(Collectors.toList());
			validateForArgument(argParsedCmd, parsedCmdList.size());
			switch (argParsedCmd[0]) {
			case "-a": // Fetch by Author
				svnQueryHandler = QuerySVNByAuthor.getInstance();
				svnQueryHandler.setMethodComapreCriteria(SVNLogEntry.class.getMethod("getAuthor", null));
				break;

			case "-r": // For both revision and revision range
			case "-rr":
				svnQueryHandler = QuerySVNByRevision.getInstance();
				svnQueryHandler.setMethodComapreCriteria(SVNLogEntry.class.getMethod("getRevision", null));
				break;

			case "-d": // For both date and date range
			case "-dr":
				svnQueryHandler = QuerySVNByDate.getInstance();
				svnQueryHandler.setMethodComapreCriteria(SVNLogEntry.class.getMethod("getDate", null));
				break;

			case "-da":
			case "-dra":
			case "-dc":
			case "-drc":
				svnQueryHandler = QuerySVNByDate.getInstance();
				svnQueryHandler.setMethodComapreCriteria(SVNLogEntry.class.getMethod("getDate", null));
				break;

			case "-c": // Fetch by comment
				svnQueryHandler = QuerySVNByComment.getInstance();
				svnQueryHandler.setMethodComapreCriteria(SVNLogEntry.class.getMethod("getMessage", null));
				break;

			default:
				throw new SVNListExecutorException("Invalid command!! Please see help and try again.");
			}
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			System.out.println("Something has gone wrong. Sorting has take a hit, please don't sorting to work");
		}
		return svnQueryHandler;
	}

	private static void validateForArgument(final String[] argParsedCmd, final int numberOfArguments)
			throws SVNListExecutorException {
		boolean invalidCommand = false;
		switch (numberOfArguments) {
		case 1:
			invalidCommand = true;
			break;
		case 2: // For remaining valid commands
			if (argParsedCmd[0].equalsIgnoreCase("-dr") || argParsedCmd[0].equalsIgnoreCase("-rr")) {
				invalidCommand = true;
			}
			if (argParsedCmd[0].equalsIgnoreCase("-r")) {
				validateForRevisionNumbers(argParsedCmd[1]);
			}
			break;
		case 3: // For revision and date range
			if (!(argParsedCmd[0].equalsIgnoreCase("-dr") || argParsedCmd[0].equalsIgnoreCase("-rr"))) {
				invalidCommand = true;
			}
			if (argParsedCmd[0].equalsIgnoreCase("-rr")) {
				validateForRevisionNumbers(argParsedCmd[1]);
				validateForRevisionNumbers(argParsedCmd[2]);
			}
			break;
		default:
			invalidCommand = true;
		}
		if (invalidCommand) {
			throw new SVNListExecutorException("Number of arguments don't match!! Please see help and try again.");
		}
	}

	private static void validateForRevisionNumbers(final String argRevisionNumber) throws SVNListExecutorException {
		try {
			new Long(argRevisionNumber);
		} catch (Exception e) {
			throw new SVNListExecutorException("The revision number should be digits");
		}
	}
}