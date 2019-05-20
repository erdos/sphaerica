package org.sphaerica.app;

import org.sphaerica.data.Resources;
import org.sphaerica.math.UnitVector;
import org.sphaerica.swing.*;
import org.sphaerica.util.TangoColorScheme;
import org.sphaerica.worksheet.*;
import org.sphaerica.worksheet.AbstractPoint.PointVisitor;
import org.sphaerica.worksheet.Polygon;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * GUI element for displaying information on a SphericalObject instance. This
 * class is referred by the WindowFooter class.
 */
@SuppressWarnings("serial")
public final class ObjectPropertiesPanel extends JPanel implements
        ActionListener, SphericalObjectVisitor, PointVisitor, ChangeListener {

    private final ApplicationWindow context;
    private JPanel panel;
    private SphericalObject obj = null;
    private final JComponent inspectButton;

    ObjectPropertiesPanel(ApplicationWindow con) {
        context = con;

        setLayout(new BorderLayout());

        inspectButton = new ComponentGroup(new IconButton(SimpleIcons.PLUS,
                "inspect-open", this));

        setBorder(null);
        setFont(getFont().deriveFont(23f));

        closeInspector();
    }

    /**
     * Creates a check box item displaying the visibility property of the object
     * in parameter. The user can interact with this gui item to change the
     * display of the object.
     *
     * @param object
     * @return new JCheckbox instance
     */
    public JCheckBox createVisibleBox(final SphericalObject object) {
        JCheckBox visible = new JCheckBox();
        context.parent.resources.register(visible, "visible");
        // visible.setOpaque(false);
        visible.setModel(new JToggleButton.ToggleButtonModel() {
            public boolean isSelected() {
                if (!object.getAppearance().containsKey(
                        ObjectAppearanceFactory.KEY_VISIBILITY))
                    return false;
                else
                    return (Boolean) object.getAppearance().get(
                            ObjectAppearanceFactory.KEY_VISIBILITY);
            }

            public void setSelected(boolean s) {
                context.worker.change(object,
                        ObjectAppearanceFactory.KEY_VISIBILITY, s);
                context.worksheet.fireChangeListeners();
            }
        });
        visible.setUI(new JCheckBoxIconUI(SimpleIcons.CIRCLE_SOLID,
                SimpleIcons.CIRCLE_DOTTED));
        return visible;
    }

    /**
     * Creates a checkbox component displaying the real status of the object.
     * For details on this object property, please see SphericalObject.isReal()
     */
    private JCheckBox createRealBox(final SphericalObject object) {
        final JCheckBox real = new JCheckBox();
        context.parent.resources.register(real, "real");
        // real.setOpaque(false);
        real.setModel(new JToggleButton.ToggleButtonModel() {
            public boolean isSelected() {
                return object.isReal();
            }
        });

        real.setActionCommand("inspect-close");
        real.addActionListener(this);

        real.setUI(new JCheckBoxIconUI(SimpleIcons.RECT_DOTTED,
                SimpleIcons.RECT_DOTTED_X));
        return real;
    }

    /**
     * Makes the component display information on the given object.
     *
     * @param object the target of the component.
     */
    void inspect(final SphericalObject object) {
        if (this.obj != null)
            this.obj.removeChangeListener(this);
        this.obj = object;
        this.obj.addChangeListener(this);

        if (panel != null)
            this.remove(panel);

        panel = new JPanel();

        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(true);
        panel.setBackground(Color.white);

        panel.add(createRealBox(object));
        panel.add(createVisibleBox(object));

        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 6);
        Border solidBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
                Color.LIGHT_GRAY);

        panel.setBorder(BorderFactory.createCompoundBorder(solidBorder,
                emptyBorder));

        final JButton deleteButton = new IconButton(SimpleIcons.X, "delete",
                this); // IconButton.createCloseButton(this);
        panel.add(deleteButton);

        if (object.getAppearance().containsKey("size")) {
            final JComboBox combo = StyleCellRenderer
                    .createComboBox(new Integer[]{2, 3, 4, 5, 6, 7});
            combo.setSelectedItem(object.getAppearance().get("size"));
            combo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    context.worker.change(object,
                            ObjectAppearanceFactory.KEY_SIZE,
                            combo.getSelectedItem());
                    context.worksheet.fireChangeListeners();
                }
            });
            panel.add(combo);
        }

        if (object.getAppearance().containsKey(
                ObjectAppearanceFactory.KEY_COLOR)) {
            final JComboBox combo = StyleCellRenderer
                    .createComboBox(TangoColorScheme.colors());
            combo.setBorder(null);
            combo.setSelectedItem(object.getAppearance().get(
                    ObjectAppearanceFactory.KEY_COLOR));
            combo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    context.worker.change(object,
                            ObjectAppearanceFactory.KEY_COLOR,
                            combo.getSelectedItem());
                    context.worksheet.fireChangeListeners();
                }
            });
            panel.add(combo);
        }

        object.apply(this);

        this.remove(inspectButton);
        this.add(panel, BorderLayout.WEST);
        this.revalidate();

    }

    private void insertOpt(String key, JComponent compo) {
        panel.add(compo);
    }

    /**
     * Removes object properties panel from this component. This method has no
     * visible effect if the inspector component is not present.
     */
    private final void closeInspector() {
        if (this.obj != null)
            this.obj.removeChangeListener(this);
        if (this.panel != null)
            this.remove(panel);
        this.repaint();
        this.obj = null;
        this.panel = null;
        this.add(inspectButton, BorderLayout.WEST);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String command = e.getActionCommand();
        if (command.equals("inspect-close")) {
            closeInspector();
        } else if (command.equals("delete")) {
            context.worker.remove(this.obj);
            closeInspector();
        } else if (command.equals("inspect-open")) {
            if (this.panel != null) {
                closeInspector();
                return;
            }
            new SwingWorker<SphericalObject, Object>() {
                @Override
                public SphericalObject doInBackground() {
                    try {
                        return context.worker.object();
                    } catch (InterruptedException ex) {
                        this.cancel(false);
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        inspect(this.get());
                    } catch (InterruptedException e) {
                    } catch (CancellationException c) {
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    /**
     * Creates gui handler for any curve instance. The default implementation
     * displays the length of the curve using the default angle unit.
     */
    @Override
    public void visit(final AbstractCurve curve) {

        JLabel length = new JLabel() {
            public String getText() {
                if (!curve.isReal())
                    return "-";
                return context.parent.resources.angle(curve.getLength());
            }

            public int getWidth() {
                return (int) getPreferredSize().getWidth();
            }
        };
        length.setHorizontalAlignment(JLabel.CENTER);

        insertOpt("length", length);
    }

    /**
     * Creates gui handler for any point instance. The default implementation
     * displays the location of the point in the default angle units.
     */
    @Override
    public void visit(final AbstractPoint point) {
        final Resources res = context.parent.resources;
        JLabel coords = new JLabel() {
            public String getText() {
                UnitVector loc = point.getLocation();
                if (!point.isReal())
                    return "-";
                return "(" + res.angle(loc.getAzimuth()) + ", "
                        + res.angle(loc.getInclination()) + ")";
            }
        };
        coords.setHorizontalAlignment(JLabel.CENTER);
        coords.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 3));
        insertOpt("coordinates", coords);

        point.applyPointVisitor(this);
    }

    /**
     * Creates a gui handler for the Polygon instance. The default information
     * creates a component which displays the area in steradians of the polygon.
     */
    @Override
    public void visit(final Polygon poly) {
        JLabel area = new JLabel() {
            public String getText() {
                if (!poly.isReal())
                    return "-";
                return String.format("%1$.2f sr", poly.getArea());
            }
        };
        area.setHorizontalAlignment(JLabel.CENTER);

        insertOpt("area", area);
    }

    /**
     * Repaints the component.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    /**
     * Creates gui handlers for the given Midpoint instance. The default
     * implementation does nothing.
     */
    @Override
    public void visit(Midpoint midpoint) {
    }

    /**
     * Creates gui handlers for this FreePoint instance. The default
     * implementation does nothing.
     */
    @Override
    public void visit(FreePoint freepoint) {
    }

    /**
     * Creates gui handlers for a parametric point. This method creates two gui
     * items by default. First, it creates a slider for displaying the current
     * parameter of this ParametricPoint instance. Second, it also creates a
     * slider for the speed of the parameter. This property can be used with
     * animation.
     */
    @Override
    public void visit(final ParametricPoint parametric) {

        // current parameter value
        final NextSlider param = new NextSlider(0f, 1f,
                (float) parametric.getParam(), 0.2f) {

            public float getValue() {
                return (float) parametric.getParam();
            }

            public void setValue(float p) {
                super.setValue(p);
                parametric.setParam(super.getValue());
                context.worksheet.fireChangeListeners();
            }
        };

        // first derivate of parameter
        final NextSlider delta = new NextSlider(-0.01f, 0.01f,
                (float) parametric.getSpeed(), 0.005f) {

            public float getValue() {
                return (float) parametric.getSpeed();
            }

            public void setValue(float p) {
                super.setValue(p);
                parametric.setSpeed(super.getValue());
            }
        };

        // NextSlider.installBorders(param);
        // param.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // NextSlider.installBorders(delta);
        // delta.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel gp = PanelFactory.createGridPanel(0, 1, 1, 1, param, delta);
        gp.setBorder(BorderFactory.createLineBorder(Color.gray));
        gp.setPreferredSize(new Dimension(160, 18));
        insertOpt("param", gp);

    }

    /**
     * Created gui elements for the given Intersection instance. The current
     * implementation does nothing.
     */
    @Override
    public void visit(Intersection intersection) {

    }
}
