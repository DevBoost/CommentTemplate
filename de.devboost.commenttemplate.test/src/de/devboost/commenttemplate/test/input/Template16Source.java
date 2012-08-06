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
import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;

public class Template16Source {

	private String LB = CommentTemplateCompiler.DEFAULT_LINE_BREAK;

	@SuppressWarnings("unused")
	@CommentTemplate
	public String generate() {
		/*
		line1

			
*/
			for (String next : new String[] {"l1", "l2"}) {
/*				next
*/
			}
		return "";
	}

	public String expectedResult() {
		return 
			LB +
			"line1" + LB + 
			LB + 
			"\t" + LB +
			"\tl1" + LB +
			"\tl2" + LB
			;
	}
}
