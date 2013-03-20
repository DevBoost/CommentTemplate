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
package org.buildboost.buildext.commenttemplate;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.resource.JaMoPPUtil;

import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;

public class CommentTemplateApplication {

	public static void main(String[] args) {
		if (args.length < 2) {
			String name = CommentTemplateApplication.class.getName();
			System.out.println(name + ".main() Invalid number of arguments.");
			System.out.println("Usage: java " + name + " <pathToTemplateSourceFile> <classpath>");
			return;
		}
		
		String pathToSource = args[0];
		String classpath = args[1];
		new CommentTemplateApplication().run(pathToSource, classpath);
	}

	private void run(String pathToSource, String classpath) {
		URI sourceURI = URI.createFileURI(pathToSource);
		JaMoPPUtil.initialize();
		ResourceSet resourceSet = new ResourceSetImpl();
		
		for (String classpathEntry : classpath.split(";")) {
			classpathEntry = classpathEntry.trim();
			if ("".equals(classpathEntry) || classpathEntry == null) {
				continue;
			}
			URI uri = URI.createFileURI(classpathEntry);
			if ("jar".equals(uri.fileExtension())) {
				JavaClasspath.get(resourceSet).registerClassifierJar(uri);				
			} else {
				JavaClasspath.get(resourceSet).registerSourceOrClassFileFolder(uri);
			}
		}
		
		new CommentTemplateCompiler().compileAndSave(sourceURI, resourceSet);
	}
}
