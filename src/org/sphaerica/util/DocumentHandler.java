package org.sphaerica.util;

import org.sphaerica.app.Dialogues;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Implements the basic document handling functionalities for the application.
 * These include file creation, file opening, closing, saving, saving as, etc.
 * Subclasses must provide concrete behaviour by overriding the corresponding
 * methods.
 */
public class DocumentHandler {
    private File file;
    private final JFileChooser fileChooser = new JFileChooser();

    private final DocumentState state;

    /**
     * Inner representation of the state of the currently opened document.
     */
    public interface DocumentState {
        /**
         * Concrete implementation should override this method to provide
         * default behavior for opening document files.
         *
         * @param f file to open
         * @throws FileNotFoundException
         * @throws IOException
         */
        void readFile(File f) throws FileNotFoundException,
                IOException;

        /**
         * Implementations should override this method to provide default
         * behavior for writing and saving document files.
         *
         * @param file to save to.
         * @throws IOException
         */
        void writeFile(File f) throws IOException;

        /**
         * Implementations should override this method to clear inner state of
         * the document handling system.
         */
        void clearContent();
    }

    public DocumentHandler(DocumentState state) {
        this.state = state;
    }

    public void newFile() {
        file = null;
        state.clearContent();
    }

    /**
     * Displays an open dialog to the user.
     *
     * @throws IOException
     */
    public void open() throws IOException {
        if (fileChooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
            return;
        try {
            open(fileChooser.getSelectedFile());
        } catch (FileNotFoundException e) {
            Dialogues.showErrorDialog(null, e.getLocalizedMessage());
        }
    }

    /**
     * Opens the given file using the readFile function.
     *
     * @param s File object to open.
     * @throws FileNotFoundException file can not be found.
     * @throws IOException
     */
    public void open(File s) throws FileNotFoundException, IOException {
        state.readFile(s);
        file = s;
    }

    /**
     * Returns the current file opened by the user.
     *
     * @return
     */
    public final File getFile() {
        return file;
    }

    /**
     * Saves the current file to its default locatin. If no save function has
     * been invoked, it displays a Save As dialog to the suer.
     *
     * @throws IOException
     */
    public void save() throws IOException {
        if (file == null)
            saveAs();
        else
            saveAs(file);
    }

    /**
     * Opens "Save As" dialog for saving the file on a new location.
     *
     * @throws IOException
     */
    public void saveAs() throws IOException {
        if (fileChooser.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
            return;
        file = fileChooser.getSelectedFile();

        saveAs(file);
    }

    /**
     * Saves current state to the given file without opening a dialog window.
     *
     * @param f file to save into
     * @throws IOException
     */
    public void saveAs(File f) throws IOException {
        file = f;
        try {
            state.writeFile(f);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
