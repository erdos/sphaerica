package org.sphaerica.worksheet;

import org.sphaerica.SphaericaInfo;
import org.sphaerica.worksheet.AbstractCurve.CurveVisitor;
import org.sphaerica.worksheet.AbstractPoint.PointVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorksheetExport implements SphericalObjectVisitor, PointVisitor, CurveVisitor {
    private Element elem = null;
    private final Document doc;
    private final Map<SphericalObject, String> map;

    public static void saveXML(final Worksheet sheet, final File file) {
        try {
            final Map<SphericalObject, String> map = new HashMap<SphericalObject, String>();
            for (SphericalObject o : sheet.getConstruction())
                map.put(o, "#" + map.size());

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("sphaerica");

            root.setAttribute("version", String.valueOf(SphaericaInfo.VERSION));
            root.setAttribute("xmlns:style", "http://sphaerica.org/save-xml/style");
            doc.appendChild(root);

            Element construction = doc.createElement("construction");
            root.appendChild(construction);

            for (SphericalObject obj : sheet.getConstruction())
                new WorksheetExport(construction, obj, map);

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WorksheetExport(Element parent, SphericalObject obj, Map<SphericalObject, String> id) {
        doc = parent.getOwnerDocument();
        map = id;

        obj.apply(this);
        if (elem == null)
            throw new RuntimeException("could not save " + obj + " object!");

        // if this node has no children, it makes no sense to refer to it.
        if (obj.getChildren().iterator().hasNext())
            elem.setAttribute("id", id.get(obj));

        for (Map.Entry<String, Object> entry : obj.getAppearance().entrySet())
            elem.setAttribute("style:" + entry.getKey(), entry.getValue().toString());
        parent.appendChild(elem);
    }

    @Override
    public void visit(AbstractCurve curve) {
        curve.applyCurveVisitor(this);
    }

    @Override
    public void visit(AbstractPoint point) {
        point.applyPointVisitor(this);
    }

    @Override
    public void visit(Polygon poly) {
        elem = doc.createElement("poly");
        final Document doc = elem.getOwnerDocument();
        for (SphericalObject point : poly.getParents()) {
            Element parent = doc.createElement("point");
            parent.setTextContent(map.get(point));
            elem.appendChild(parent);
        }
    }

    @Override
    public void visit(Midpoint midpoint) {
        elem = doc.createElement("midpoint");
        elem.setAttribute("a", map.get(midpoint.a));
        elem.setAttribute("b", map.get(midpoint.b));
    }

    @Override
    public void visit(FreePoint freepoint) {
        elem = doc.createElement("point");
        elem.setAttribute("location", freepoint.getLocation().encode());
    }

    @Override
    public void visit(ParametricPoint parametric) {
        elem = doc.createElement("parametric");
        elem.setAttribute("curve", map.get(parametric.parent));
        elem.setAttribute("param", String.valueOf(parametric.getParam()));
        elem.setAttribute("speed", String.valueOf(parametric.getSpeed()));

    }

    @Override
    public void visit(Intersection intersection) {
        elem = doc.createElement("intersection");
        elem.setAttribute("a", map.get(intersection.firstObject));
        elem.setAttribute("b", map.get(intersection.secondObject));
    }

    @Override
    public void visit(LineSegment s) {
        elem = doc.createElement("segment");
        elem.setAttribute("from", map.get(s.a));
        elem.setAttribute("to", map.get(s.b));
    }

    @Override
    public void visit(Circle circle) {
        elem = doc.createElement("circle");
        elem.setAttribute("origo", map.get(circle.getOrigo()));
        if (circle.getRadii() != null)
            elem.setAttribute("radii", map.get(circle.getRadii()));
    }
}
