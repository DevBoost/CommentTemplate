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

public class Template11Source {

	private String LB = LineBreak.LB;

	@SuppressWarnings("unused")
	@CommentTemplate
	public String generate() {
		/*first
*/
			for (String property : new String[] {"a", "b"}) {
				String nullable = "false";
				if (true) {
					if (true) {
/*						property nullable
						*/
					}
				}
/*			private property
*/			}
		return "";
	}

	public String expectedResult() {
		return 
				"first" + LB +
				//"\t@OneToOne(cascade={})" + LB +
				"\ta false" + LB +
				"\tprivate a" + LB +
				"\tb false" + LB +
				"\tprivate b" + LB;
	}
}
