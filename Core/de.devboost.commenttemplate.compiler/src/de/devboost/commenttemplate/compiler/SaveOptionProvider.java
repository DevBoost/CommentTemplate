/*******************************************************************************
 * Copyright (c) 2006-2013
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package de.devboost.commenttemplate.compiler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.URI;
import org.emftext.language.java.resource.java.IJavaOptions;
import org.emftext.language.java.resource.java.util.JavaEclipseProxy;
import org.emftext.language.java.resource.java.util.JavaRuntimeUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class SaveOptionProvider {

	public Map<Object, Object> getSaveOptions(URI compiledURI) {
		Map<Object, Object> options = new LinkedHashMap<Object, Object>();
		if (!new JavaRuntimeUtil().isEclipsePlatformRunning()) {
			return options;
		}
		
		IFile compiledFile = new JavaEclipseProxy().getFileForURI(compiledURI);
		IProject project = compiledFile.getProject();
		String lineBreak = getLineBreak(project);
		options.put(IJavaOptions.LINE_DELIMITER_FOR_PRINTING, lineBreak);
		return options;
	}

	private String getLineBreak(IProject project) {
		String value = null;
		if (project != null) {
			value = getStoredValue(getPreferences(null));
		}
		if (value == null) {
			IPreferencesService preferencesService = Platform.getPreferencesService();
			if (preferencesService != null) {
				value = getStoredValue(preferencesService.getRootNode().node(DefaultScope.SCOPE));
			}
		}
		return value != null ? value : System.getProperty(Platform.PREF_LINE_SEPARATOR);
	}

	private Preferences getPreferences(IProject project) {
		IPreferencesService preferencesService = Platform.getPreferencesService();
		if (preferencesService == null) {
			return null;
		}
		if (project != null) {
			return preferencesService.getRootNode().node(ProjectScope.SCOPE).node(project.getName());
		}

		return preferencesService.getRootNode().node(InstanceScope.SCOPE);
	}

	private String getStoredValue(Preferences preferences) {
		if (preferences == null) {
			return null;
		}
		try {
			// be careful looking up for our node so not to create any nodes as side effect
			if (preferences.nodeExists(Platform.PI_RUNTIME)) {
				return preferences.node(Platform.PI_RUNTIME).get(Platform.PREF_LINE_SEPARATOR, null);
			}
		} catch (BackingStoreException e) {
			// ignore
		}
		return null;
	}
}
