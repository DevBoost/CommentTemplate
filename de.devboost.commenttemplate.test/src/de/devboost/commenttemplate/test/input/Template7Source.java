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
package de.devboost.commenttemplate.test.input;

import java.util.Arrays;
import java.util.List;

import de.devboost.commenttemplate.CommentTemplate;
import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;

public class Template7Source {

	private String LB = CommentTemplateCompiler.DEFAULT_LINE_BREAK;

	@SuppressWarnings("unused")
	@CommentTemplate
	public String generate() {
		List<String> lines = Arrays.asList(new String[] {"line2", "line3"});
		/*line1
		*/
		for (String nextLine : lines) {
			/*nextLine
			*/
		}
		/*line4
			line5
		line6*/
		return "";
	}

	public String expectedResult() {
		return "line1" + LB +
				"line2" + LB +
				"line3" + LB +
				"line4" + LB +
				"\tline5" + LB +
				"line6";
	}
}
