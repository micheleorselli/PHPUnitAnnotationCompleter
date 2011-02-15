package com.micheleorselli.phpunitannotationcompleter;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.Exceptions;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.loaders.DataObject;

/**
 *
 * @author Michele Orselli <michele.orselli@gmail.com>
 */
public class PHPUnitAnnotationCompletionProvider implements CompletionProvider {

    @Override
    public CompletionTask createTask(int i, JTextComponent jTextComponent) {

        if (i != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }

        DataObject currentFile = NbEditorUtilities.getDataObject(jTextComponent.getDocument());

        if (!currentFile.getName().endsWith("Test")) {
            return null;
        }

        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset) {

                String filter = null;

                String PREFIX = "@";
                
                String TAGS[] = new String[]{
                    "assert", "author", "backupGlobals", "backupStaticAttributes", "covers",
                    "dataProvider", "depends", "expectedException", "expectedExceptionMessage",
                    "group", "outputBuffering", "runTestsInSeparateProcesses", "runInSeparateProcess",
                    "test", "testdox", "ticket"
                };
              
                int startOffset = caretOffset - 1;

                try {
                    final StyledDocument bDoc = (StyledDocument) document;
                    final int lineStartOffset = getRowFirstNonWhite(bDoc, caretOffset);
                    final char[] line = bDoc.getText(lineStartOffset, caretOffset - lineStartOffset).toCharArray();
                    final int whiteOffset = indexOfWhite(line);
                    filter = new String(line, whiteOffset + 1, line.length - whiteOffset - 1);
                    if (whiteOffset > 0) {
                        startOffset = lineStartOffset + whiteOffset + 1;
                    } else {
                        startOffset = lineStartOffset;
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }

                if (!filter.startsWith(PREFIX)) {
                    completionResultSet.finish();

                    return;
                }

                for (String tag : TAGS)
                {
                    if ((PREFIX + tag).startsWith(filter)) {
                        completionResultSet.addItem(new PHPUnitAnnotationCompletionItem(PREFIX + tag, startOffset, caretOffset));
                    }
                    
                }
                completionResultSet.finish();

            }
        }, jTextComponent);

    }

    @Override
    public int getAutoQueryTypes(JTextComponent arg0, String arg1) {
        return 0;
    }

    static int getRowFirstNonWhite(StyledDocument doc, int offset)
            throws BadLocationException {
        Element lineElement = doc.getParagraphElement(offset);
        int start = lineElement.getStartOffset();
        while (start + 1 < lineElement.getEndOffset()) {
            try {
                if (doc.getText(start, 1).charAt(0) != ' ') {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1) +
                        ") on doc of length: " + doc.getLength(), start).initCause(ex);
            }
            start++;
        }
        return start;
    }

    static int indexOfWhite(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (Character.isWhitespace(c)) {
                return i;
            }
        }
        return -1;
    }
}
