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
package de.devboost.commenttemplate.test;

import java.util.Arrays;
import java.util.List;

import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;
import junit.framework.TestCase;

public class IndentationTest extends TestCase {

	public void testIndentationHandling() {
		assertConversion(0, "/* abc */", new String[] {" abc "});
		assertConversion(0, "/* abc \n def */", new String[] {" abc ", " def "});
		
		// tabs before the first lines of the comment must be remove from the
		// other lines (if these start with tabs)
		assertConversion(3, "\t\t\t/* abc \n def */", new String[] {" abc ", " def "});
		assertConversion(3, "\t\t\t/* abc \n\t\t\t def */", new String[] {" abc ", " def "});
		assertConversion(3, "\t\t\t/* abc \n\t\t\t\t def */", new String[] {" abc ", "\t def "});
		assertConversion(3, "\t\t\t/* abc \n\t\t \t\t def */", new String[] {" abc ", "\t def "});
		
		assertConversion(3, "\n\t\t\t/*package\n\t\t\t*/", new String[] {"package",""});

		assertConversion(1, "/*\t\t\t abc */", new String[] {"\t\t abc "});
		assertConversion(1, "/*\t\t\t abc \n*/", new String[] {"\t\t abc ", ""});
		assertConversion(1, "\n/*\t\t\t abc \n*/", new String[] {"\t\t abc ", ""});
		assertConversion(1, "\n/*\t\t\tabc;\n*/", new String[] {"\t\tabc;", ""});
		
		assertConversion(3, "/*\t\t\t abc */", new String[] {" abc "});
		assertConversion(3, "/*\t\t\t\t abc */", new String[] {"\t abc "});
		assertConversion(3, "/*\t\t\t\t abc \n */", new String[] {"\t abc ", " "});

		assertConversion(3, "\t/*\t\t abc */", new String[] {" abc "});
		assertConversion(3, "\t/*\t\t abc \n\t\t\t def */", new String[] {" abc ", " def "});
		
		assertConversion(1, "\t/*\t\t abc */", new String[] {"\t\t abc "});
	}

	private void assertConversion(int leadingTabs, String comment, String[] expected) {
		CommentTemplateCompiler compiler = new CommentTemplateCompiler();
		List<String> linesToPrint = compiler.getLinesToPrint(comment, leadingTabs, true);
		assertEquals(Arrays.asList(expected), linesToPrint);
	}
}
