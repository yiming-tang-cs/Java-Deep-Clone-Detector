package sense.concordia.java.deepclone.ui.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Yiming Tang
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sense.concordia.java.deepclone.messages"; //$NON-NLS-1$
	public static String Name;
	public static String NoProjects;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		super();
	}
}
