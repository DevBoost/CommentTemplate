package de.devboost.commenttemplate.test.input;

import de.devboost.commenttemplate.compiler.CommentTemplateCompiler;

/**
 * We use this class to provide the default line break to the template test
 * cases. We do not directly refer to the {@link CommentTemplateCompiler}
 * because such a link cannot be resolved during the test by the JaMoPP
 * reference resolvers.
 */
public class LineBreak {

	public final static String LB = CommentTemplateCompiler.DEFAULT_LINE_BREAK;
}
