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

import de.devboost.commenttemplate.CommentTemplate;
import de.devboost.commenttemplate.VariableAntiQuotation;

@SuppressWarnings("unused")
public class HelloWorldSource {
	
	@VariableAntiQuotation("#%s#")
	@CommentTemplate
	public String helloWorld() {
		String greeting = "Hello";
		/*<html>
			<head><title>greeting World!</title></head>
			<body>*/
				for (int i = 1; i <= 5; i++) {
					String greeted = "World" + i;
					/*
					#greeting# #greeted#!<br/>*/
					if (greeted.equals("World2")) {
						/*
						#greeted#, you are the best!<br/>*/
					}
				}
			/*
			</body>
		</html>*/
		return null;
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
