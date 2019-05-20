package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;
import java.util.Map.Entry;

public final class WorksheetImport extends DefaultHandler {

    private final WorksheetWorker worker;
    private final Map<String, SphericalObject> ids;
    private final Stack<Map<String, String>> attrStack;
    private final List<String> texts;

    // current state of the automaton
    // TODO: implement by creating state objects instead of markers.
    private Tag state;

    enum Tag {
        ROOT, CONSTRUCTION, POLY
    }


    private WorksheetImport(Worksheet sheet) {
        this.worker = sheet.createWorker();
        this.ids = new HashMap<String, SphericalObject>();
        // this.cache = new LinkedList<SphericalObject>();
        this.attrStack = new Stack<Map<String, String>>();
        this.texts = new LinkedList<String>();
    }

    public static void loadXML(final Worksheet sheet, final File file) throws SAXException, IOException {

        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(new WorksheetImport(sheet));
        reader.parse(new InputSource(new FileInputStream(file)));

    }

    public void startDocument() {
        state = null;
    }

    public void endDocument() {
        // TODO give some feedback.
        worker.undoManager.clear();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        final Map<String, String> attrs;
        {
            attrs = new HashMap<String, String>();
            for (int i = attributes.getLength(); i-- > 0; )
                attrs.put(attributes.getQName(i), attributes.getValue(i));
            attrStack.push(attrs);

        }

        if (state == null) {
            if ("sphaerica".equals(localName))
                state = Tag.ROOT;
            else
                throw new SAXException("unexpected root tag");
        } else if (state == Tag.ROOT) {
            if ("construction".equals(localName))
                state = Tag.CONSTRUCTION;
            else
                throw new SAXException("format error, unexpected " + qName + " tag.");
        } else if (state == Tag.CONSTRUCTION) {

            if ("poly".equals(localName)) {
                state = Tag.POLY;
                return;
            }

            final SphericalObject obj;

            if ("point".equals(localName)) {
                obj = worker.point(UnitVector.decode(attrs.get("location")));
            } else if ("midpoint".equals(localName)) {
                AbstractPoint a = (AbstractPoint) ids.get(attributes.getValue("a"));
                AbstractPoint b = (AbstractPoint) ids.get(attributes.getValue("b"));
                obj = worker.midpoint(a, b);
            } else if ("segment".equals(localName)) {
                AbstractPoint a = (AbstractPoint) ids.get(attributes.getValue("from"));
                AbstractPoint b = (AbstractPoint) ids.get(attributes.getValue("to"));
                obj = worker.segment(a, b);
            } else if ("circle".equals(localName)) {
                AbstractPoint origo = (AbstractPoint) ids.get(attributes.getValue("origo"));
                String radii = attributes.getValue("radii");
                if (radii == null)
                    obj = worker.line(origo);
                else
                    obj = worker.circle(origo, (AbstractPoint) ids.get(radii));
            } else if ("parametric".equals(localName)) {
                AbstractCurve curve = (AbstractCurve) ids.get(attributes.getValue("curve"));
                double param = Double.parseDouble(attributes.getValue("param"));
                obj = worker.point(curve, param);
            } else if ("intersection".equals(localName)) {
                AbstractCurve a = (AbstractCurve) ids.get(attributes.getValue("a"));
                AbstractCurve b = (AbstractCurve) ids.get(attributes.getValue("b"));
                obj = worker.intersection(a, b);
            } else
                throw new SAXException("unexpected elem");

            final Map<String, Object> app = obj.getAppearance();

            for (Entry<String, String> pair : attrs.entrySet()) {
                if (!pair.getKey().startsWith("style:"))
                    continue;
                String key = pair.getKey().substring(6), value = pair.getValue();
                app.put(key, ObjectAppearanceFactory.decode(key, value));
            }

            if (attributes.getValue("id") != null)
                ids.put(attributes.getValue("id"), obj);

        } else if (state == Tag.POLY) {
            // dontcare
        } else
            System.err.println(qName);
    }

    public void characters(char ch[], int start, int length) {
        CharBuffer buffer = CharBuffer.wrap(ch, start, length);
        if (buffer.toString().trim().equals(""))
            return;
        texts.add(buffer.toString());
    }

    public void endElement(String uri, String localName, String qName) {

        final Map<String, String> attrs = attrStack.pop();

        if (state == Tag.POLY && "poly".equals(localName)) {
            List<AbstractPoint> points = new LinkedList<AbstractPoint>();
            for (String key : texts)
                points.add((AbstractPoint) ids.get(key));
            texts.clear();
            SphericalObject obj = worker.poly(points.toArray(new AbstractPoint[0]));

            // XXX i am repeating code here.
            Map<String, Object> app = obj.getAppearance();
            for (Entry<String, String> pair : attrs.entrySet()) {
                if (!pair.getKey().startsWith("style:"))
                    continue;
                String key = pair.getKey().substring(6), value = pair.getValue();
                app.put(key, ObjectAppearanceFactory.decode(key, value));
            }

            state = Tag.CONSTRUCTION;
        }
    }

}