package org.sphaerica.swing;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * This class helps printing components.
 */
public class PrintComponent implements Printable {

    private final Component printable;

    private PrintComponent(Component c) {
        printable = c;
    }

    /**
     * Displays a print dialog to the user.
     *
     * @param c componen to print
     */
    public static void print(Component c) {
        PrintComponent pc = new PrintComponent(c);
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(pc);
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0)
            return NO_SUCH_PAGE;

        final Graphics2D g2d = (Graphics2D) graphics;

        final double cw = pageFormat.getImageableWidth(), ch = pageFormat.getImageableHeight();
        final double scale = Math.min(cw / printable.getWidth(), ch / printable.getHeight());

        final AffineTransform transform = g2d.getTransform();

        transform.scale(scale, scale);
        transform.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setTransform(transform);

        printable.paint(graphics);

        return PAGE_EXISTS;
    }
}

