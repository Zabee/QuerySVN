package query.svn;

import javax.inject.Inject;

/**
 * Just a wrapper class
 *
 * @author Zabee
 * @created July 29, 2018
 * 
 */
public class QueryExecutor {

	@Inject
	static private ListSVNFiles listSVNFiles = new ListSVNFiles();

	public static void main(String[] args) {
//		ClassPathXmlApplicationContext classPathBeanXml = new ClassPathXmlApplicationContext("spring-beans.xml");
//		listSVNFiles = (ListSVNFiles) classPathBeanXml.getBean("listSVNFiles");
		listSVNFiles.start(args);
	}

}
