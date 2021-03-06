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
 * This is a test template for CommentTemplate that verifies whether the
 * compiled generator does replace names of local variables that appear in 
 * comments with the value of the respective local variable. 
 */
public class Template3Source {

	@SuppressWarnings("unused")
	@CommentTemplate
	public String generate() {
		String someLocalVariable = "value";
		/* content someLocalVariable */
		return "";
	}

	public String expectedResult() {
		return " content value ";
	}
}
