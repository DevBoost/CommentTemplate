/*******************************************************************************
 * Copyright (c) 2006-2012
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

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;

public class CommentTemplateBuilder {

	public boolean isBuildingNeeded(URI uri) {
		return uri.lastSegment().endsWith(CommentTemplateCompiler.SOURCE_SUFFIX + ".java");
	}

	public URI build(Resource resource, Set<String> brokenVariableReferences) {
		if (!resource.getErrors().isEmpty()) {
			return null;
		}
		try {
			Resource compiledResource = 
					new CommentTemplateCompiler().compileAndSave(resource, brokenVariableReferences);
			if (compiledResource == null) {
				return null;
			}
			return compiledResource.getURI();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}

}
