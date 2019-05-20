package org.sphaerica.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * This cell renderer can be used for displaying style information related
 * object values in list cells. The color value is displayed as a square of the
 * given color. The stroke width value is displayed as a circle of a matching
 * size.
 */
public class StyleCellRenderer implements ListCellRenderer {

    /**
     * Creates a combo box with this list cell renderer.
     *
     * @param params array of objects to display
     * @return combo box component
     */
    public static JComboBox createComboBox(Object[] params) {
        final JComboBox list = new JComboBox(params);
        list.setRenderer(new StyleCellRenderer());
        return list;
    }

    /**
     * The cell renderer component is reused.
     */
    private final JLabel label = new JLabel();

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        label.setBackground(isSelected ? Color.BLACK : Color.white);
        label.setForeground(isSelected ? Color.WHITE : Color.black);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(1, 3, 1, 3));

        if (value == null) {
            label.setIcon(null);
            label.setText("-");
        } else if (value instanceof Color) {
            label.setIcon(new ColorIcon((Color) value));
            label.setText(String.format("%x", ((Color) value).getRGB()));
        } else if (value instanceof Number) {
            int w = ((Number) value).intValue();
            label.setIcon(new SizeIcon(w));
            label.setText(w + "");
        } else {
            label.setIcon(null);
            label.setText(value.toString());
        }
        if (isSelected == cellHasFocus)
            label.setText(null);

        return label;
    }

}
