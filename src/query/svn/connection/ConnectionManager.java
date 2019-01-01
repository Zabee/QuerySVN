package query.svn.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * The SVN connection manager. The final output of this class will be a
 * SVNReposity connection object ready to fire queries.
 * 
 * @author Zabee
 *
 */

public class ConnectionManager {

	private static ConnectionManager connectionManager;
	private static SystemPropertyLoader sysPropLoader = null;
	private static SVNConnectiviyHelper svnConnectiviyHelper = null;
	private static String url = null;
	private static String userName = null;
	private static String password = null;
	private static SVNRepository repository = null;
	private static List<SVNLogEntry> logEntries = null;

	static {
		sysPropLoader = () -> {
			Properties properties = System.getProperties();
			try (FileInputStream fileInputStream = new FileInputStream(
					System.getProperty("user.dir") + "\\system.properties")) {
				properties.load(fileInputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};

		svnConnectiviyHelper = () -> {
			try {
				url = System.getProperty("svnURL");
				userName = System.getProperty("svnUserName");
				password = System.getProperty("svnPassword");
				Long.getLong("startRevision");
				Long.getLong("endRevision");

				DAVRepositoryFactory.setup();
				repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
				@SuppressWarnings("deprecation")
				ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName,
						password);
				repository.setAuthenticationManager(authManager);
			} catch (Exception e) {
				System.out.println("something went wrong while setting up SVN connection");
				e.printStackTrace();
			}
		};

	}

	public static ConnectionManager initializeSVNConnectivity() {
		doSVNSetup();
		return connectionManager == null ? new ConnectionManager() : connectionManager;
	}

	private static void doSVNSetup() {
		sysPropLoader.loadProperties();
		svnConnectiviyHelper.setupSVNConnectivity();
	}

	private ConnectionManager() {
	}

	@SuppressWarnings("unchecked")
	public List<SVNLogEntry> getLogsFromConnection() throws SVNException {
		System.out.println("Fetching from SVN. Please wait..");
		Collection<SVNLogEntry> theLogEntries = repository.log(new String[] { "" }, null, 0, -1, true, true);
		if (theLogEntries == null) {
			System.out.println("Something went wrong while fetching details from SVN");
			System.exit(-1);
		}
		return new ArrayList<SVNLogEntry>(theLogEntries);
	}

	@FunctionalInterface
	interface SVNConnectiviyHelper {
		public void setupSVNConnectivity();
	}

	@FunctionalInterface
	interface SystemPropertyLoader {
		public void loadProperties();
	}
}
