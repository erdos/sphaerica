package org.sphaerica.app;

import org.sphaerica.swing.ComponentGroup;
import org.sphaerica.swing.SyntaxHighlighterUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * This GUI item is a script writer panel. Its main task is to display a code
 * editor for writing scripts. The code editor may invovle a syntax highlighter.
 * A direct connection to the worksheet scripting engine is maintained.
 */
@SuppressWarnings("serial")
public class ScripterPanel extends JPanel implements ActionListener {

    private final ApplicationWindow context;
    private final JTextField textfield;

    private final List<String> commands = new LinkedList<String>();

    private ListIterator<String> iter = commands.listIterator();

    ScripterPanel(final ApplicationWindow window) {

        context = window;

        JPanel input = new JPanel(new BorderLayout());

        input.setLayout(new BorderLayout());
        textfield = new JTextField();
        textfield.setAutoscrolls(true);
        textfield.setUI(new SyntaxHighlighterUI());

        final JButton button = new JButton();
        window.parent.resources.register(button, "run");
        button.setFocusPainted(false);

        textfield.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    actionPerformed(new ActionEvent(this, 0, "up"));
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    actionPerformed(new ActionEvent(this, 0, "down"));
                }
            }
        });

        textfield.addActionListener(this);
        textfield.setActionCommand("construct");
        button.addActionListener(this);
        button.setActionCommand("construct");

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                textfield.requestFocusInWindow();
            }

            public void componentHidden(ComponentEvent e) {
                textfield.setText("");
            }
        });

        input.add(textfield, BorderLayout.CENTER);
        input.add(button, BorderLayout.EAST);
        input.setOpaque(false);
        // input.setBorder(new EmptyBorder(2, 2, 2, 2));

        // p.setPreferredSize(new Dimension(200, 28));
        add(new ComponentGroup(input));
        setLayout(new GridLayout());
        setBorder(new EmptyBorder(4, 4, 4, 4));
        setOpaque(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String command = e.getActionCommand();

        if (command.equals("construct")) {
            context.scripter.evalInNewThread(textfield.getText());
            commands.add(textfield.getText());
            if (commands.size() > 20)
                commands.remove(0);
            textfield.setText("");

            iter = commands.listIterator(commands.size());

        } else if ("up".equals(command)) {
            if (iter.hasPrevious())
                textfield.setText(iter.previous());
        } else if ("down".equals(command)) {
            if (iter.hasNext())
                textfield.setText(iter.next());
            else
                textfield.setText("");
        }
    }
}
