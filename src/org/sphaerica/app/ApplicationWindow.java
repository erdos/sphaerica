package org.sphaerica.app;

import org.sphaerica.SphaericaInfo;
import org.sphaerica.display.SphereDisplayPanel;
import org.sphaerica.display.SphereDisplayPanel.SphereCanvasCallback;
import org.sphaerica.math.UnitVector;
import org.sphaerica.swing.FadeComponent;
import org.sphaerica.swing.InfoPanelFactory;
import org.sphaerica.swing.NextSlider;
import org.sphaerica.swing.PrintComponent;
import org.sphaerica.util.DocumentHandler;
import org.sphaerica.util.DocumentHandler.DocumentState;
import org.sphaerica.util.Selection;
import org.sphaerica.worksheet.*;
import org.sphaerica.worksheet.Worksheet.ScriptHandle;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ApplicationWindow extends JFrame implements ActionListener,
        DocumentState, SphereCanvasCallback, ChangeListener, ScriptHandle {

    final int PANEL_WIDTH = 260; // right panel size in pixels

    final Worksheet worksheet = new Worksheet();
    final WorksheetWorker worker = worksheet.createWorker();
    final WorksheetAnimator animator = worksheet.createAnimator();
    final WorksheetScripter scripter = new WorksheetScripter(worker);
    final DocumentHandler documentHandling = new DocumentHandler(this);

    final SphereDisplayPanel editor = new SphereDisplayPanel(worksheet, this);

    private final JComponent messages;

    private final WindowHeader header;
    private final WindowFooter footer;

    final ApplicationContext parent;

    public ApplicationWindow(ApplicationContext context) {
        super();
        parent = context;
        worksheet.addChangeListener(this);

        scripter.setHandle(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionPerformed(new ActionEvent(this, 0, "close"));
            }
        });

        /*
         * The content component is the content pane of the window. It contains
         * the main areas of the window such as the editor, header and footer.
         */
        final JPanel content;

        {
            content = new JPanel(new BorderLayout());
            content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            setContentPane(content);
            setJMenuBar(new WindowMenu(this));
            setTitle(SphaericaInfo.NAME);
            setMinimumSize(new Dimension(720, 480));
            setPreferredSize(new Dimension(800, 600));
            setSize(getPreferredSize());
            setLocationRelativeTo(null);
        }

        header = new WindowHeader(this);
        footer = new WindowFooter(this);

        {
            messages = new JPanel();
            messages.setLayout(new BoxLayout(messages, BoxLayout.PAGE_AXIS));
            messages.setOpaque(true);

            ActionListener listener = new ActionListener() {
                JComponent msg;

                @Override
                public void actionPerformed(ActionEvent e) {
                    final String action = e.getActionCommand();
                    if ("close".equals(action)) {
                        System.out.println("interrupt action called");
                        worker.getSelection().interruptSelection();
                    } else if (Selection.BEFORE.equals(action)) {
                        Map<Class<?>, String> map = new HashMap<Class<?>, String>();
                        map.put(AbstractCurve.class, "request.curve");
                        map.put(AbstractPoint.class, "request.point");
                        map.put(Circle.class, "request.circle");
                        map.put(SphericalObject.class, "request.object");

                        final Class<?> selclass = worker.getSelection()
                                .getSelectionClass();
                        final String message = parent.resources.translate(map
                                .get(selclass));
                        msg = InfoPanelFactory.createStatusPanel(message, this);
                        messages.add(msg);
                        messages.revalidate();
                    } else if (Selection.AFTER.equals(action)) {
                        if (msg == null)
                            return;
                        new FadeComponent(msg);
                        msg = null;
                    }
                }
            };

            worker.getSelection().addListener(listener);
        }

        editor.setLayout(new BorderLayout());
        editor.add(messages, BorderLayout.NORTH);

        content.add(header, BorderLayout.NORTH);
        content.add(editor, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String action = e.getActionCommand();
        if (action.equals("zoom")) {
            editor.getArcBall().setScale(
                    ((NextSlider) e.getSource()).getValue());
            editor.repaint();
        } else if (action.equals("undo"))
            worker.getUndoable().undo();
        else if (action.equals("redo"))
            worker.getUndoable().redo();
        else if (action.equals("about"))
            Dialogues.showAboutDialog(editor);
        else if (action.equals("close")) {
            destroy();
        } else if (action.equals("save"))
            try {
                documentHandling.save();
            } catch (IOException e1) {
                error(e1);
            }
        else if ("save-as".equals(action)) {
            try {
                documentHandling.saveAs();
            } catch (IOException e1) {
                error(e1);
            }
        } else if (action.equals("print"))
            PrintComponent.print(editor);
        else if (action.equals("color.default")) {

        } else
            parent.actionPerformed(e);
    }

    @Override
    public void objectSelected(SphericalObject obj) {
        if (worker.accept(obj))
            worker.select(obj);
        else if (worker.accept(AbstractPoint.class)
                && (obj instanceof AbstractCurve)) {
            final AbstractCurve curve = (AbstractCurve) obj;
            AbstractPoint point = worker.point(curve,
                    curve.fInverse(editor.getCursorVector()));
            ObjectAppearanceFactory.show(point);
            worker.select(point);
        }
    }

    @Override
    public void locationSelected(UnitVector vec) {
        if (worker.accept(FreePoint.class)) {
            AbstractPoint p = worker.point(vec);
            ObjectAppearanceFactory.show(p);
            worker.select(p);
        }
    }

    @Override
    public void repainted() {
        // System.out.println("repainted!");
    }

    private void destroy() {
        setVisible(false);
        animator.pause();
        dispose();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // we need to repaint the display asap. TODO: experiment by adjusting
        // values..
        editor.repaint(1000);
    }

    /**
     * Displays the given Throwable instance on the GUI.
     */
    @Override
    public void error(Throwable thr) {
        JComponent err = InfoPanelFactory.createErrorPanel(parent.resources,
                thr);
        messages.add(err);
        messages.revalidate();
        thr.printStackTrace();
        if (thr.getCause() != null)
            thr.getCause().printStackTrace();
    }

    /**
     * Displays the given object on the GUI.
     */
    @Override
    public void result(Object obj) {
        // TODO Auto-generated method stub

    }

    /**
     * Prints given text message on the GUI.
     */
    @Override
    public void print(String str) {
        // TODO Auto-generated method stub

    }

    /**
     * Displays given error message on the GUI.
     */
    @Override
    public void err(String str) {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeFile(File f) {
        WorksheetExport.saveXML(worksheet, f);
    }

    @Override
    public void readFile(File f) throws IOException {
        try {
            WorksheetImport.loadXML(worksheet, f);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearContent() {
        worksheet.getConstruction().clear();
    }

}