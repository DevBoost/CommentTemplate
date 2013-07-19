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
package de.devboost.commenttemplate.compiler;

import org.emftext.language.java.members.Method;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferenceableElement;
import org.emftext.language.java.references.ReferencesFactory;

public class JavaModelHelper {

	// TODO move to JaMoPP metamodel?
	public IdentifierReference createReference(ReferenceableElement element) {
		IdentifierReference reference = ReferencesFactory.eINSTANCE.createIdentifierReference();
		reference.setTarget(element);
		return reference;
	}

	// TODO move to JaMoPP metamodel
	public MethodCall createMethodCall(Method method) {
		MethodCall methodCall = ReferencesFactory.eINSTANCE.createMethodCall();
		methodCall.setTarget(method);
		return methodCall;
	}
}
