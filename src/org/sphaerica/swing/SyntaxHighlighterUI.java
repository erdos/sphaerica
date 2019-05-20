package org.sphaerica.swing;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.*;
import java.awt.*;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Syntax Highlishter UI class for JTextComponent instances. This class
 * provides basic support of syntax highlighting for lisp like programming langues.
 * Some keywords, number tokens and parentheses are colored. Also, matching parentheses
 * are displayed in respect to the current position of the caret.
 */
public class SyntaxHighlighterUI extends BasicTextFieldUI implements CaretListener {
    public EditorKit getEditorKit(JTextComponent compo) {
        return new SyntaxHighlighter();
    }

    public void installListeners() {
        super.installListeners();
        super.getComponent().addCaretListener(this);
    }

    public void uninstallListeners() {
        super.uninstallListeners();
        super.getComponent().removeCaretListener(this);
    }

    public void installDefaults() {
        super.installDefaults();
        final Font f = Font.decode(Font.MONOSPACED).deriveFont(16.0f);
        super.getComponent().setFont(f);
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        super.getComponent().repaint();
    }
}

class SyntaxHighlighter extends StyledEditorKit implements ViewFactory {
    private static final long serialVersionUID = 4743099590632106858L;

    @Override
    public ViewFactory getViewFactory() {
        return this;
    }

    @Override
    public String getContentType() {
        return "text/lisp";
    }

    @Override
    public View create(Element elem) {
        return new LispView(elem);
    }
}

enum LispSyntaxToken {
    COMMENT("(;.*$)", new Color(63, 95, 191)),
    NUMBER("([+-]?[0-9]+)", new Color(32, 43, 12)),
    PARENTH("(\\(|\\))", new Color(12, 3, 124)),
    KEYWORD("(if|lambda|macro|car|cdr|cons|quote)", new Color(43, 126, 43));

    final Pattern pattern;
    final Color fg;

    LispSyntaxToken(String pattern, Color foreground) {
        this.pattern = Pattern.compile(pattern);
        this.fg = foreground;
    }
}

class LispView extends FieldView {

    public final int TAB_SIZE = 4;

    public LispView(Element element) {
        super(element);
        getDocument().putProperty(PlainDocument.tabSizeAttribute, TAB_SIZE);
    }

    protected int drawUnselectedText(Graphics graphics, int x, int y, int p0,
                                     int p1) throws BadLocationException {

        final Document doc = getDocument();
        final String text = doc.getText(p0, p1 - p0);
        final Segment segment = getLineBuffer();

        int cursor = ((JTextComponent) super.getContainer()).getCaret().getDot();
        int open = -1, close = -1;

        if (doc.getText(cursor, 1).charAt(0) == '(') {
            open = cursor;
            for (int ct = 0, j = cursor; j < doc.getLength(); j++) {
                char c = doc.getText(j, 1).charAt(0);
                if (c == ')') ct--;
                else if (c == '(') ct++;
                if (ct == 0) {
                    close = j;
                    break;
                }
            }
        } else if (cursor > 0 && doc.getText(cursor - 1, 1).charAt(0) == ')') {
            close = cursor - 1;
            for (int ct = 0, j = cursor - 1; j > -1; j--) {
                char c = doc.getText(j, 1).charAt(0);
                if (c == ')') ct--;
                else if (c == '(') ct++;
                if (ct == 0) {
                    open = j;
                    break;
                }
            }
        }

        final SortedMap<Integer, Integer> positions = new TreeMap<Integer, Integer>();
        final SortedMap<Integer, LispSyntaxToken> tokens = new TreeMap<Integer, LispSyntaxToken>();

        for (LispSyntaxToken token : LispSyntaxToken.values()) {
            final Matcher matcher = token.pattern.matcher(text);
            while (matcher.find()) {
                int start = matcher.start(1);
                positions.put(start, matcher.end());
                tokens.put(start, token);
            }
        }

        int i = this.getStartOffset();
        for (int position : positions.keySet()) {
            final int start = position, end = positions.get(start);
            final LispSyntaxToken token = tokens.get(position);

            if (i > start) // handling overlapping areas.
                continue;
            if (i < start) {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, start - i, segment);

                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

            i = end;
            doc.getText(p0 + start, i - start, segment);

            if ((start == open && close == -1) || (start == close && open == -1))
                graphics.setColor(Color.red);
            else if (start == open || start == close)
                graphics.setColor(Color.orange);
            else
                graphics.setColor(token.fg);

            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
        }

        // Paint possible remaining text black
        if (i < text.length()) {
            graphics.setColor(Color.black);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }
        return x;
    }

}