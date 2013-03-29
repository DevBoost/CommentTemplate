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

import de.devboost.commenttemplate.CommentTemplate;

/**
 * This is a test template for CommentTemplate that verifies whether replacing
 * a variable name with the content of another comment template method works. 
 */
public class Template5Source {

	private String LB = LineBreak.LB;

	@SuppressWarnings("unused")
	@CommentTemplate
	public String generate() {
		String subLine = generateLine3();
		/*line1
		line2
			subLine
		line4*/
		return "";
	}

	@CommentTemplate
	private String generateLine3() {
		/*line3*/
		return "";
	}

	public String expectedResult() {
		return "line1" + LB +
				"line2" + LB +
				"\tline3" + LB +
				"line4";
	}
}
