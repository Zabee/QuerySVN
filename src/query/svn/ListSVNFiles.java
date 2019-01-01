package query.svn;

import java.util.*;

import javax.inject.Inject;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import query.svn.connection.ConnectionManager;
import query.svn.handlers.SVNQueryHandler;
import query.svn.helpers.DecorHelper;
import query.svn.helpers.RevisionFileHelper;

/**
 * Executor class
 * 
 * @author Zabee
 * 
 *         Get files list by Author -a, revision number -r, revision numbers,
 *         range -rr, date -d date range -dr, -c comment (DONE)
 * 
 *         TODO - (1) Change code for all options- code to handle command line
 *         args (DONE)
 * 
 *         TODO - (2) Test the code for all options: Date and Date range is
 *         pending
 * 
 *         TODO - (3) Combine options - Change the design for PIPES (DONE)
 * 
 *         TODO - (4) Build SortingHelper, handle sorting options integrate and
 *         test
 * 
 *         TODO - (5) Storing and Retrieving the logs to and from file. File
 *         name should contains the HASH code branch URL
 * 
 *         (a) Write a logic to store the fetched logs into a file for the first
 *         time
 * 
 *         (b) Store the latest log entry in file
 * 
 *         (c) For subsequent hits to SVN pass the starting revision number (the
 *         stored one) and -1 as ending revision number
 * 
 *         (d) Remove the -f option
 * 
 *         TODO - (5) Springify the app
 * 
 *         TODO - (6) Handle possible types of error and log them using log4j -
 *         less important
 * 
 *         TODO - (7) Create an executable
 * 
 *         TODO - (8) Create an installer - optional
 * 
 *         TODO - (9) Distribute
 */

public class ListSVNFiles {

	private static RevisionFileHelper revisionFileHelper = RevisionFileHelper.getInstance();
	private static SVNQueryHandler svnQueryHandler = null;

	private static ConnectionManager connectionManager = null;
	private static List<SVNLogEntry> logEntries = null;
	private static List<String> svnCommands = Arrays.asList("-a", "-r", "-rr", "-d", "-dr", "-c", "-f");
	private static List<SVNLogEntry> fileListFromSVN = null;
	private int keyedInCmdLen = 0;

//	@Inject
//	RevisionFileHelper revisionFileHelper;

//	@Inject
//	ConnectionManager connectionManager;

	// @Inject
//	List<SVNLogEntry> logEntries;
//	@Inject
//	List<String> svnCommands;

//	@Inject
//	List<SVNLogEntry> fileListFromSVN;

//	@Inject
	DecorHelper decorHelper = new DecorHelper();

	public void start(String[] args) {
		connectionManager = ConnectionManager.initializeSVNConnectivity();
		Scanner scanner = null;
		String commandLine = null;
		try {
			// For the first time
			logEntries = connectionManager.getLogsFromConnection();
			logEntries.stream().forEach(System.out::println);
			while (true) {
				String[][] commands = new String[6][4]; // Need to retune of this line :/

				System.out.println("******************************************************");
				System.out.println("Please enter a command from below. Use pipe | for combining the commands:");
				System.out.println("Help:");
				System.out.println("	 -a  authorName(By Author)");
				System.out.println("	 -r  revisionNumber(By Revision)");
				System.out.println("	 -rr revisionFrom RevisionTo(By Revision Range)");
				System.out.println("	 -d  date (By Date) YYYY-MM-DD");
				System.out.println("	 -dr dateFrom dateTo(By Date Range) YYYY-MM-DD");
				System.out.println("	 -c  comment (By Commit Message)");
				System.out.println("	 -f  (Gets a fresh copies from SVN)");
				System.out.println("	 -exit");
//				System.out.println("******************************************************");
				System.out.print("Your command goes here:");

				scanner = new Scanner(System.in);
				try {

					commandLine = scanner.nextLine();

					if (commandLine.contains("-f")) {
						// For subsequent times
						logEntries = connectionManager.getLogsFromConnection();
						continue;
					}

					parseCommand(commandLine, commands);

					fileListFromSVN = new ArrayList<SVNLogEntry>(logEntries);
					for (int i = 0; i < keyedInCmdLen; i++) {
						svnQueryHandler = QueryFactory.getQuerySVNHandler(commands[i]);
						fileListFromSVN = svnQueryHandler.listFilesFromSVN(fileListFromSVN, commands[i]);
						//I don't know why I have this if condition and this likely to be removed
						if (!isCommandValid(commands[i])) {
							continue;
						}
					}
				} catch (SVNListExecutorException invalidCmdException) {
					System.out.println(invalidCmdException.getMessage());
					revisionFileHelper.write(invalidCmdException.getMessage());
					continue;
				}

				// Let's prettify and sort the output
				Collections.sort(fileListFromSVN, svnQueryHandler);
				String toOutputFile = decorHelper.decor(fileListFromSVN);
				revisionFileHelper.write(toOutputFile);
			}
		} catch (SVNException sException) {
			sException.printStackTrace();
		} finally {
		}
		try {
			scanner.close();
		} catch (Exception e) {// Close silently.
		}
	}

	private boolean isCommandValid(String[] argCmd) {
		if (svnQueryHandler == null || !svnCommands.contains(argCmd[0].toLowerCase())) {
			System.out.println("Invalid command please see the help section above");
			return false;
		}
		return true;
	}

	private void parseCommand(String argUserInput, String[][] commands) throws SVNListExecutorException {
		try {
			if (argUserInput.contains("exit")) {
				System.out.println("Tool terminated");
				System.exit(-1);
			}
			boolean multiCmd = false;
			StringTokenizer multiCmdTokenizer = new StringTokenizer(argUserInput, "|");
			String command = null;
			StringTokenizer cmdTokenizer = null;
			int i = 0, j = 0;
			if (argUserInput.contains("|")) {
				while (multiCmdTokenizer.hasMoreTokens()) {
					j = 0;
					multiCmd = true;
					command = multiCmdTokenizer.nextToken();
					if (command.contains("-c")) {
						// Special treatment
						parseCommentCommad(argUserInput, commands, i, j);
					} else {
						cmdTokenizer = new StringTokenizer(command);
						while (cmdTokenizer.hasMoreTokens()) {
							commands[i][j++] = cmdTokenizer.nextToken();
						}
					}
					i++;
				}
			}
			keyedInCmdLen = i;
			if (!multiCmd) {
				keyedInCmdLen = 1;
				if (argUserInput.contains("-c")) {
					// Special treatment
					parseCommentCommad(argUserInput, commands, 0, 0);
				} else {
					cmdTokenizer = new StringTokenizer(argUserInput);
					while (cmdTokenizer.hasMoreTokens()) {
						commands[0][j++] = cmdTokenizer.nextToken();
					}
				}
			}
		} catch (IndexOutOfBoundsException | java.util.NoSuchElementException indexOutBounds) {
			throw new SVNListExecutorException("Number of arguments don't match!! Please see help and try again.");
		}
	}

	private void parseCommentCommad(String argUserInput, String[][] commands, int i, int j)
			throws SVNListExecutorException {
		commands[i][0] = "-c";
		// +3 lengthOf(-c) + lengthOf(" ")
		commands[i][1] = argUserInput.substring(argUserInput.indexOf("-c") + 3);

	}
}

class WaitMessagThread extends Thread {
	@Override
	public void run() {
		System.out.println("I really appreciate your patience. Please wait...");
	}
}