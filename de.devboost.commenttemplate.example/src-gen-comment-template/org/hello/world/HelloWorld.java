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
package org.hello.world;

import java.io.FileWriter;

@SuppressWarnings("unused")
public class HelloWorld {
	public String helloWorld() {
		StringBuilder __content = new StringBuilder();
		String greeting = "Hello";
		__content.append("<html>\n");
		__content.append("\t<head><title>greeting World!</title></head>\n");
		__content.append("\t<body>");
		/*<html>
			<head><title>greeting World!</title></head>
			<body>*/
				for (int i = 1;i <= 5;i++) {
					String greeted = "World" + i;
	__content.append("\n");
	__content.append("\t\t");
	__content.append(greeting.replaceAll("\\n\\z","").replace("\n","\n\t\t"));
	__content.append(" ");
	__content.append(greeted.replaceAll("\\n\\z",""));
	__content.append("!<br/>");
					/*
					#greeting# #greeted#!<br/>*/
					if (greeted.equals("World2")) {
	__content.append("\n");
	__content.append("\t\t");
	__content.append(greeted.replaceAll("\\n\\z","").replace("\n","\n\t\t"));
	__content.append(", you are the best!<br/>");
						/*
						#greeted#, you are the best!<br/>*/
					}
				}
		__content.append("\n");
		__content.append("\t</body>\n");
		__content.append("</html>");
			/*
			</body>
		</html>*/
		return __content.toString();
	}
	
	public static void run() {
		try {
			FileWriter fileWriter = new FileWriter(
					"/Users/jjohannes/workspaces/devboost-demos/" +
					"de.devboost.commenttemplate.example/HelloWorld.html");
			fileWriter.write(new HelloWorld().helloWorld());
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stop() {}


}
