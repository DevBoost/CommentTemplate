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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.emftext.language.java.resource.java.IJavaOptions;
import org.emftext.language.java.resource.java.mopp.JavaMarkerHelper;

import de.devboost.commenttemplate.CommentTemplatePlugin;
import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;
import de.devboost.eclipse.jdtutilities.AbstractCompilationParticipant;
import de.devboost.eclipse.jdtutilities.CompilationEvent;

public class CommentTemplateCompilationParticipant extends AbstractCompilationParticipant {

	private CommentTemplateBuilder builder = new CommentTemplateBuilder();
	
	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	@Override
	public void buildStarting(CompilationEvent event) {
		buildStartingInternal(new BuildContext[] {event.getContext()});
	}

	@Override
	public void buildFinished(Collection<CompilationEvent> events) {
		// do nothing
	}
	
	private void buildStartingInternal(BuildContext[] files) {
		// we must catch exceptions to avoid that the build process is blocked
		// if something goes wrong with CommentTemplate
		try {
			buildUnsafe(files);
		} catch (Throwable e) {
			CommentTemplatePlugin.logError("Error while building CommentTemplate class.", e);
		}
	}

	private void buildUnsafe(BuildContext[] contexts) {

		ResourceSetImpl resourceSet = new ResourceSetImpl();
		// markers are already created by the JDT
		Map<Object, Object> loadOptions = resourceSet.getLoadOptions();
		loadOptions.put(IJavaOptions.DISABLE_CREATING_MARKERS_FOR_PROBLEMS,
				Boolean.TRUE);

		for (BuildContext context : contexts) {
			IFile srcFile = context.getFile();
			IPath srcFilePath = srcFile.getFullPath();
			URI uri = URI.createPlatformResourceURI(srcFilePath.toString(), true);
			if (!builder.isBuildingNeeded(uri)) {
				continue;
			}
			
			Set<String> brokenVariableReferences = new LinkedHashSet<String>();
			Resource resource = resourceSet.getResource(uri, true);
			URI compiledURI = builder.build(resource, brokenVariableReferences);
			if (compiledURI == null) {
				continue;
			}
			
			IWorkspace workspace = srcFile.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			Path compiledSrcPath = new Path(compiledURI.toPlatformString(true));
			IFile compiledSrc = root.getFile(compiledSrcPath);
			try {
				srcFile.deleteMarkers(JavaMarkerHelper.MARKER_TYPE, false, IResource.DEPTH_ONE);
			} catch (CoreException e) {
				CommentTemplatePlugin.logError("Can't delete markers from " + srcFile, e);
			}
			if (!brokenVariableReferences.isEmpty()) {
				createWarnings(brokenVariableReferences, srcFile);
			}
			context.recordAddedGeneratedFiles(new IFile[] {compiledSrc});
			createSrcGenFolder(srcFile.getProject());
		}
	}

	private void createWarnings(Set<String> brokenVariableReferences,
			IFile compiledSrc) {
		String fileContent = readFile(compiledSrc);
		for (String reference : brokenVariableReferences) {
			for (int i = -1; (i = fileContent.indexOf(reference, i + 1)) != -1; ) {
				try {
					IMarker marker = compiledSrc.createMarker(JavaMarkerHelper.MARKER_TYPE);
					marker.setAttribute(IMarker.SEVERITY, org.eclipse.core.resources.IMarker.SEVERITY_WARNING);
					marker.setAttribute(IMarker.MESSAGE, "Variable '" + reference + "' not declared");
					marker.setAttribute(IMarker.CHAR_START, i);
					marker.setAttribute(IMarker.CHAR_END, i + reference.length());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String readFile(IFile file) {
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(file.getContents()));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				// TODO Using the Unix line break is probably not always correct
				stringBuilder.append(line + "\n");
			}

			bufferedReader.close();
			return stringBuilder.toString();
		} catch (IOException e) {
			CommentTemplatePlugin.logError("Can't read file " + file.getProjectRelativePath(), e);
		} catch (CoreException e) {
			CommentTemplatePlugin.logError("Can't read file " + file.getProjectRelativePath(), e);
		}
		return "";
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
			CommentTemplatePlugin.logError("Can't set classpath for project " + project.getName(), e);
		}
	}

	//@Override
	//public void buildFinished(IJavaProject project) {
	//		TODO fix code to remove markers AND annotations for unused variables which are used in a template fragment
	//		try {
	//			IMarker[] findMarkers = project.getProject().findMarkers("org.eclipse.jdt.core.problem", true, IResource.DEPTH_INFINITE);
	//			System.out.println(findMarkers.length);
	//			for (IMarker iMarker : findMarkers) {
	//				System.out.println(iMarker.getResource().getName());
	//				System.out.println(iMarker.getAttribute(IMarker.MESSAGE));
	//				iMarker.delete();
	//
	//				FileEditorInput editorInput= new FileEditorInput((IFile) iMarker.getResource());
	//				IAnnotationModel annotationModel = JavaPlugin.getDefault().getCompilationUnitDocumentProvider().getAnnotationModel(editorInput);
	//				if (annotationModel != null)
	//				for (Iterator<Object> i = annotationModel.getAnnotationIterator(); i.hasNext(); ) {
	//					Annotation annotation = (Annotation) i.next();
	//					System.out.println(annotation.getText());
	//					annotationModel.removeAnnotation(annotation);
	//				}
	//			}
	//
	//		} catch (CoreException e) {
	//			e.printStackTrace();
	//		}
	//}
}
