package de.devboost.commenttemplate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class CommentTemplatePlugin extends Plugin {
	
	private static final String PLUGIN_ID = "de.devboost.commenttemplate";
	
	private static CommentTemplatePlugin instance;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		CommentTemplatePlugin.instance = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		CommentTemplatePlugin.instance = null;
		super.stop(context);
	}

	public static CommentTemplatePlugin getInstance() {
		return instance;
	}
	
	/**
	 * Helper method for error logging.
	 * 
	 * @param message the error message to log
	 * @param throwable the exception that describes the error in detail (can be null)
	 * 
	 * @return the status object describing the error
	 */
	public static IStatus logError(String message, Throwable throwable) {
		return log(IStatus.ERROR, message, throwable);
	}
	
	/**
	 * Helper method for logging.
	 * 
	 * @param type the type of the message to log
	 * @param message the message to log
	 * @param throwable the exception that describes the error in detail (can be null)
	 * 
	 * @return the status object describing the error
	 */
	protected static IStatus log(int type, String message, Throwable throwable) {
		IStatus status;
		if (throwable != null) {
			status = new Status(type, CommentTemplatePlugin.PLUGIN_ID, 0, message, throwable);
		} else {
			status = new Status(type, CommentTemplatePlugin.PLUGIN_ID, message);
		}
		final CommentTemplatePlugin pluginInstance = CommentTemplatePlugin.getInstance();
		if (pluginInstance == null) {
			System.err.println(message);
			if (throwable != null) {
				throwable.printStackTrace();
			}
		} else {
			pluginInstance.getLog().log(status);
		}
		return status;
	}
}
