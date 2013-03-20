package com.tw.demo.ui.autoedit;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.tw.demo.hKDsl.Expression;
import com.tw.demo.hKDsl.Model;
import com.tw.demo.hKDsl.Statement;

public class InterpreterAutoEdit implements IAutoEditStrategy {

	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		for (String lineDelimiter : document.getLegalLineDelimiters()) {
			if (command.text.equals(lineDelimiter)) {					
				Object computedResult = computeResult(document, command);
				if (computedResult != null)
					command.text = lineDelimiter + "// = " + computedResult.toString() + lineDelimiter;					
			}
		}
	}
	
	private Object computeResult(IDocument document, final DocumentCommand command) {
		return ((IXtextDocument) document)
				.readOnly(new IUnitOfWork<Object, XtextResource>() {
					public Object exec(XtextResource state)
							throws Exception {
						Expression expr = findExpression(command, state);
						if (expr == null)
							return null;
						return 0;
					}
				});
	}
	
	protected Expression findExpression(final DocumentCommand command, XtextResource state) {
		if(!state.getContents().isEmpty()) {
			Model m = (Model) state.getContents().get(0);
			for (Statement expr : m.getStatements()) {
				if (expr instanceof Expression) {
					ICompositeNode node = NodeModelUtils.getNode(expr);
					if (node.getOffset() <= command.offset
							&& (node.getOffset() + node.getLength()) >= command.offset) {
						return (Expression) expr;
					}
				}
			}
		}
		return null;
	}
}
