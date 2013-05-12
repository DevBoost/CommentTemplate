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
package de.devboost.commenttemplate.builder;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;

import de.devboost.commenttemplate.CommentTemplatePlugin;
import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;

public class CommentTemplateBuilder {

	public boolean isBuildingNeeded(URI uri) {
		String lastSegment = uri.lastSegment();
		String suffix = CommentTemplateCompiler.SOURCE_SUFFIX + ".java";
		return lastSegment.endsWith(suffix);
	}

	public URI build(Resource resource, Set<String> brokenVariableReferences) {
		
		List<Diagnostic> errors = resource.getErrors();
		if (!errors.isEmpty()) {
			return null;
		}
		
		try {
			CommentTemplateCompiler compiler = new CommentTemplateCompiler();
			Resource compiledResource = 
					compiler.compileAndSave(resource, brokenVariableReferences);
			if (compiledResource == null) {
				return null;
			}
			return compiledResource.getURI();
		} catch (Exception e) {
			String message = "Exception in " + getClass().getSimpleName();
			CommentTemplatePlugin.logError(message, e);
		}
		return null;
	}
}
