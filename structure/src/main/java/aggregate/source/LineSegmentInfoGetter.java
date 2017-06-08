package aggregate.source;

import com.itextpdf.kernel.geom.IShape;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.data.ClippingPathInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.PathRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Joris Schellekens on 6/6/2017.
 */
public class LineSegmentInfoGetter implements ITextExtractionStrategy{

    private List<Line> lineList = new ArrayList<>();

    public LineSegmentInfoGetter(PdfPage page)
    {
        PdfTextExtractor.getTextFromPage(page, this);
    }

    public List<Line> getLines()
    {
        return lineList;
    }

    public String getResultantText(){ return ""; }

    public void eventOccurred(IEventData iEventData, EventType eventType) {
        if(iEventData instanceof PathRenderInfo)
        {
            for(Subpath subpath : ((PathRenderInfo) iEventData).getPath().getSubpaths())
            {
                for(IShape shape : subpath.getSegments())
                {
                    if(shape instanceof Line)
                    {
                        lineList.add((Line) shape);
                    }
                }
            }
        }
        if(iEventData instanceof ClippingPathInfo)
        {
            for(Subpath subpath : ((ClippingPathInfo)iEventData).getClippingPath().getSubpaths())
            {
                for(IShape shape : subpath.getSegments())
                {
                    if(shape instanceof Line)
                    {
                        lineList.add((Line) shape);
                    }
                }
            }
        }
    }

    public Set<EventType> getSupportedEvents() {
        return null;
    }
}
