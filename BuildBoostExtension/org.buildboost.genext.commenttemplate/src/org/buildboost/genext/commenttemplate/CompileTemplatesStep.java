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
package org.buildboost.genext.commenttemplate;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.IConstants;
import de.devboost.buildboost.ant.AbstractAntTargetGenerator;
import de.devboost.buildboost.ant.AntTarget;
import de.devboost.buildboost.steps.ClasspathHelper;
import de.devboost.buildboost.util.XMLContent;

/**
 * The {@link CompileTemplatesStep} generates a script that calls the EMF
 * code generators to obtain code from Ecore models. 
 */
public class CompileTemplatesStep extends AbstractAntTargetGenerator {

	public final static String MAIN_TASK = "comment-template-compile";

	private CommentTemplateSource source;

	public CompileTemplatesStep(CommentTemplateSource source) {
		this.source = source;
	}

	public Collection<AntTarget> generateAntTargets() throws BuildException {
		XMLContent classpath = new ClasspathHelper().getClasspath(source, true);
		XMLContent runtimeClasspath = new ClasspathHelper("",";").getClasspath(source, true);

		File sourceFile = source.getFile();
		String sourceFilePath = sourceFile.getAbsolutePath();

		XMLContent sb = new XMLContent();
		sb.append("<delete dir=\"temp_eclipse_workspace\" />");
		sb.append("<mkdir dir=\"temp_eclipse_workspace\" />");
		sb.append(IConstants.NL);
		
		sb.append("<echo message=\"Compiling CommentTemplate " + sourceFilePath + "\" />");
		sb.append("<java classname=\"de.devboost.commenttemplate.app.CommentTemplateApplication\" failonerror=\"true\">");
		sb.append("<arg value=\"" + sourceFilePath + "\"/>");
		sb.append("<arg value=\"" + runtimeClasspath + "\"/>");
		sb.append("<classpath>");
		sb.append(classpath);
		sb.append("</classpath>");
		sb.append("</java>");
		sb.append(IConstants.NL);
		
		String qualifiedName = sourceFile.getAbsolutePath().substring(
				source.getProjectDir().getAbsolutePath().length()).replace('/', '-');
		return Collections.singleton(new AntTarget(MAIN_TASK + 
				qualifiedName, sb));
	}
}
