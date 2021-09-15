package sense.concordia.java.deepclone.ui.handlers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.SelectionUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import sense.concordia.java.deepclone.core.util.Util;

import sense.concordia.java.deepclone.ui.messages.Messages;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
@SuppressWarnings("restriction")
public class CloneDetectionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection currentSelection = HandlerUtil.getCurrentSelectionChecked(event);
		List<?> list = SelectionUtil.toList(currentSelection);

		Set<IJavaProject> javaProjectSet = new HashSet<>();

		if (list != null)
			try {
				for (Object obj : list)
					if (obj instanceof IJavaElement) {
						IJavaElement jElem = (IJavaElement) obj;
						switch (jElem.getElementType()) {
						case IJavaElement.JAVA_PROJECT:
							javaProjectSet.add((IJavaProject) jElem);
							break;
						}
					}

				Shell shell = HandlerUtil.getActiveShellChecked(event);

				if (javaProjectSet.isEmpty())
					MessageDialog.openError(shell, Messages.Name, Messages.NoProjects);
				else {
					Util.deepclonedetect(javaProjectSet.toArray(new IJavaProject[javaProjectSet.size()]));
					
				}

			} catch (JavaModelException e) {
				JavaPlugin.log(e);
				throw new ExecutionException("Failed to detect deep clone!", e);
			}
		return null;

	}
}
