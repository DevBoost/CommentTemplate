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
package org.buildboost.genext.commenttemplate.stages;

import java.io.File;

import org.buildboost.genext.commenttemplate.CommentTemplateSourceFileFinder;
import org.buildboost.genext.commenttemplate.steps.CompileTemplatesStepProvider;

import de.devboost.buildboost.AutoBuilder;
import de.devboost.buildboost.BuildContext;
import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AntScript;
import de.devboost.buildboost.discovery.EclipseTargetPlatformAnalyzer;
import de.devboost.buildboost.discovery.PluginFinder;
import de.devboost.buildboost.model.IUniversalBuildStage;
import de.devboost.buildboost.stages.AbstractBuildStage;

/**
 * This build stage compiles CommentTemplate template classes to plain Java
 * source files.
 */
public class CompileTemplatesStage extends AbstractBuildStage implements IUniversalBuildStage {

	private String artifactsFolder;
	
	public void setArtifactsFolder(String artifactsFolder) {
		this.artifactsFolder = artifactsFolder;
	}

	public AntScript getScript() throws BuildException {
		File artifactsFolderDir = new File(artifactsFolder);

		BuildContext context = createContext(true);
		context.addBuildParticipant(new EclipseTargetPlatformAnalyzer(artifactsFolderDir));
		context.addBuildParticipant(new PluginFinder(artifactsFolderDir));
		context.addBuildParticipant(new CommentTemplateSourceFileFinder(artifactsFolderDir));		
		context.addBuildParticipant(new CompileTemplatesStepProvider());
		
		AutoBuilder builder = new AutoBuilder(context);
		
		AntScript script = new AntScript();
		script.setName("Compile CommentTemplate template classes");
		script.addTargets(builder.generateAntTargets());
		
		return script;
	}

	@Override
	public int getPriority() {
		// Must run after compiling CommentTemplate itself
		return CompileCommentTemplateStage.PRIORITY + 1;
	}
}
