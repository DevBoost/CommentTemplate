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
package org.dropsbox.autobuild.genext.commenttemplate;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.discovery.AbstractFileFinder;
import de.devboost.buildboost.model.IArtifact;
import de.devboost.buildboost.model.IBuildContext;
import de.devboost.buildboost.util.ArtifactUtil;

public class CommentTemplateSourceFinder extends AbstractFileFinder<CommentTemplateSource> {

	public CommentTemplateSourceFinder(File directory) {
		super(directory);
	}

	public Collection<IArtifact> discoverArtifacts(IBuildContext context) throws BuildException {
		Collection<CommentTemplateSource> genModels = new ArrayList<CommentTemplateSource>();
		traverse(context, genModels);
		return new ArtifactUtil().getSetOfArtifacts(genModels);
	}

	protected CommentTemplateSource createArtifactFromFile(File file) {
		return new CommentTemplateSource(file);
	}

	protected FileFilter getFileFilter() {
		return new FileFilter() {
			
			public boolean accept(File file) {
				return file.getName().endsWith("Source.java") && file.isFile();
			}
		};
	}
}
