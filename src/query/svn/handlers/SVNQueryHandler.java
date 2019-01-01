package query.svn.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

/**
 * 
 * @author Zabee
 *
 */
public abstract class SVNQueryHandler implements SVNQueryInterface, Comparator<SVNLogEntry> {

	private Method methodComapreCriteria = null;

	// It took two or more glances for me to figure out my own code. Yes, this
	// method for sorting.
	// Default sorting - ascending order.
	@Override
	public int compare(SVNLogEntry logEntry1, SVNLogEntry logEntry2) {
		List<Method> methodsList = Arrays.asList(logEntry1.getClass().getMethods());

		for (Method method : methodsList) {
			if (method.getName().equalsIgnoreCase(methodComapreCriteria.getName())) {
				try {
					Object object1 = method.invoke(logEntry1, null);
					Method anotherMethod = logEntry2.getClass().getMethod(method.getName(), null);
					Object object2 = anotherMethod.invoke(logEntry2, null);

					if (object1 instanceof String) {
						String str1 = String.valueOf(object1);
						String str2 = String.valueOf(object2);
						return str1.compareTo(str2);

					} else if (object1 instanceof Date) {
						Date date1 = (Date) object1;
						Date date2 = (Date) object2;
						return date1.compareTo(date2);
					}

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}

		}
		return 0;
	}

	public boolean excludeCIBuildCommits(SVNLogEntry logEntry) {
		// logEntry.getChangedPaths().size() ==1 &&
		Map<String, SVNLogEntryPath> theChangeList = logEntry.getChangedPaths();
		if (theChangeList.size() == 1) {
			for (String key : theChangeList.keySet()) {
				if (key.contains(".jar") || key.contains("Version.java")) {
					System.out.println("Ignore List :" + logEntry);
					return false;
				}
			}
		} else if (theChangeList.size() == 2) {
			for (String key : theChangeList.keySet()) {
				if (key.contains("build.number") || key.contains("Version.java")) {
					System.out.println("Ignore List :" + logEntry);
					return false;
				}
			}
		}
		return true;

	}

	public void setMethodComapreCriteria(Method methodComapreCriteria) {
		this.methodComapreCriteria = methodComapreCriteria;
	}

}
