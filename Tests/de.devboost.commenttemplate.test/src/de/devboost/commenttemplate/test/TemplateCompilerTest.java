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
package de.devboost.commenttemplate.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.tools.JavaFileObject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.resource.JavaSourceOrClassFileResourceFactoryImpl;
import org.emftext.language.java.resource.java.util.JavaResourceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.devboost.commenttemplate.CommentTemplate;
import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;
import de.devboost.commenttemplate.test.input.Template1Source;
import de.devboost.onthefly_javac.CompilationResult;
import de.devboost.onthefly_javac.OnTheFlyJavaCompiler;

@RunWith(Parameterized.class)
public class TemplateCompilerTest {
	
	private File templateFile;
	
	public TemplateCompilerTest(File templateFile) {
		super();
		this.templateFile = templateFile;
	}
	
	@Parameters
	public static Collection<Object[]> getTestData() {
		File[] templateFiles = getTemplateFiles();
		Collection<Object[]> fileList = new ArrayList<Object[]>();
		for (File templateFile : templateFiles) {
			fileList.add(new Object[] {templateFile});
		}
		return fileList;
	}

	private static File[] getTemplateFiles() {
		
		String templatePackage = getTemplatePackage();
		String srcPath = getSrcFolder().getPath();
		String pathToTemplates = srcPath + File.separatorChar + templatePackage.replace('.', File.separatorChar);
		File templateDir = new File(pathToTemplates);
		File[] templateFiles = templateDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				return name.endsWith("Source.java");
			}
		});
		
		assertTrue("Found too few test templates", templateFiles.length >= 11);

		return templateFiles;
	}

	private static String getTemplatePackage() {
		Class<Template1Source> clazz = Template1Source.class;
		String templatePackage = clazz.getPackage().getName();
		return templatePackage;
	}

	private static File getSrcFolder() {
		String srcPath = "src";
		File srcFolder = new File(srcPath);
		return srcFolder;
	}

	@Test
	public void testTemplateCompilation() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, FileNotFoundException, IOException {
		
		String templateFileName = templateFile.getName();
		String templateClassName = templateFileName.replace(".java", "");
		String compiledClassName = templateClassName.replace("Source", "");

		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("java", new JavaSourceOrClassFileResourceFactoryImpl());
		
		JavaClasspath.get(rs).registerClassifier(CommentTemplate.class);
		String absoluteSrcFolderPath = getSrcFolder().getAbsolutePath();
		System.out.println("testTemplateCompilation() absoluteSrcFolderPath = " + absoluteSrcFolderPath);
		URI sourceFolderURI = URI.createFileURI(absoluteSrcFolderPath);
		JavaClasspath.get(rs).registerSourceOrClassFileFolder(sourceFolderURI);
		
		Resource resource = rs.getResource(URI.createFileURI(templateFile.getAbsolutePath()), true);
		assertFalse("Original resource must not be empty.", resource.getContents().isEmpty());
		EList<Diagnostic> errors = resource.getErrors();
		for (Diagnostic diagnostic : errors) {
			System.out.println("Error: " + diagnostic.getMessage() + " at line " + diagnostic.getLine());
		}
		assertTrue("Resource must not contain errors.", errors.isEmpty());

		EcoreUtil.resolveAll(resource);
		Set<EObject> unresolvedProxies = JavaResourceUtil.findUnresolvedProxies(resource);
		for (EObject proxy : unresolvedProxies) {
			System.out.println("Found unresolved proxy: "+ proxy);
		}
		assertTrue("There must not be unresolved proxy objects.", unresolvedProxies.isEmpty());
		
		boolean success = new CommentTemplateCompiler().compile(resource, new LinkedHashSet<String>());
		assertTrue("Template must be compilable", success);
		assertFalse("Original resource must not be empty after compilation.", resource.getContents().isEmpty());

		CompilationUnit cu = (CompilationUnit) resource.getContents().get(0);
		cu.getClassifiers().get(0).setName(compiledClassName);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		resource.save(outputStream, null);
		
		String compiledSourceCode = outputStream.toString();
		System.out.println("compiledSourceCode (" + templateFileName + ") =>" + compiledSourceCode + "<=");
		
		String className = getTemplatePackage() + "." + compiledClassName;
		
		OnTheFlyJavaCompiler compiler = new OnTheFlyJavaCompiler();
		CompilationResult result = compiler.compile(className, compiledSourceCode);
		boolean compiledSuccessfully = result.isSuccess();
		if (!compiledSuccessfully) {
			List<javax.tools.Diagnostic<? extends JavaFileObject>> compilationErrors = result.getDiagnosticsCollector().getDiagnostics();
			for (javax.tools.Diagnostic<? extends JavaFileObject> compilationError : compilationErrors) {
				System.out.println(compilationError.getMessage(Locale.ENGLISH));
			}
		}
		assertTrue("Compilation must be successful.", compiledSuccessfully);
		
		Class<?> loadedClass = result.loadClass(className);
		String generatedString = instantiateAndInvoke(loadedClass, "generate");
		System.out.println("generatedString (" + templateFileName + ") =>" + generatedString + "<=");
		String expectedResult = instantiateAndInvoke(loadedClass, "expectedResult");
		System.out.println("expectedResult  (" + templateFileName + ") =>" + expectedResult + "<=");
		List<Byte> generatedBytes = new ArrayList<Byte>();
		for (Byte nextByte : generatedString.getBytes()) {
			generatedBytes.add(nextByte);
		}
		List<Byte> expectedBytes = new ArrayList<Byte>();
		for (Byte nextByte : expectedResult.getBytes()) {
			expectedBytes.add(nextByte);
		}
		System.out.println("generatedString (" + templateFileName + ") =>" + generatedBytes + "<=");
		System.out.println("expectedResult  (" + templateFileName + ") =>" + expectedBytes + "<=");
		assertEquals("Unexpected generation result.", expectedResult, generatedString);
	}

	private String instantiateAndInvoke(Class<?> loadedClass, String methodName)
			throws InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		Object newInstance = loadedClass.newInstance();
		Method method = loadedClass.getMethod(methodName, (Class<?>[]) null);
		Object returnValue = method.invoke(newInstance, new Object[] {});
		assertNotNull("Return value must not be null.", returnValue);
		assertTrue("Return value must be a string.", returnValue instanceof String);
		String generatedString = (String) returnValue;
		return generatedString;
	}
}
