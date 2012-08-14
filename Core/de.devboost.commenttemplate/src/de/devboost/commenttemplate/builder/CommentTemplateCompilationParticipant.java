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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.emftext.language.java.resource.java.IJavaOptions;

import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;

public class CommentTemplateCompilationParticipant extends CompilationParticipant {

	private CommentTemplateBuilder builder = new CommentTemplateBuilder();
	
	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		ResourceSetImpl resourceSet = new ResourceSetImpl();
		//markers are already created by the JDT
		resourceSet.getLoadOptions().put(
				IJavaOptions.DISABLE_CREATING_MARKERS_FOR_PROBLEMS, Boolean.TRUE);
		for (BuildContext context : files) {
			URI uri = URI.createPlatformResourceURI(context.getFile().getFullPath().toString(), true);
			if (builder.isBuildingNeeded(uri)) {
				URI compiledURI = builder.build(resourceSet.getResource(uri, true));
				if (compiledURI != null) {
					IWorkspace workspace = context.getFile().getWorkspace();
					IFile compiledSrc = 
							workspace.getRoot().getFile(new Path(compiledURI.toPlatformString(true)));
					context.recordAddedGeneratedFiles(new IFile[] {compiledSrc});
					createSrcGenFolder(context.getFile().getProject());
				}
			}
		}
	}

	private void createSrcGenFolder(IProject project) {
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] entries;
		try {
			entries = javaProject.getRawClasspath();
			for (int i = 0; i < entries.length; ++i) {
				IPath path = entries[i].getPath();
				if (path.segmentCount() == 2 && path.segment(1).equals(CommentTemplateCompiler.SRC_GEN_FOLDER)) {
					return;
				}
			}
			IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			IClasspathEntry entry = JavaCore.newSourceEntry(project.getFullPath().append(
					CommentTemplateCompiler.SRC_GEN_FOLDER));
			newEntries[newEntries.length - 1] = entry;
			javaProject.setRawClasspath(newEntries, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
		
}
