package aggregate.elements;

import aggregate.source.CharacterRenderInfoGetter;
import aggregate.source.LineSegmentInfoGetter;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.layout.element.IBlockElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Joris Schellekens on 6/6/2017.
 */
public class PageRenderInfo {

    private PdfPage page;
    private List<CharacterRenderInfo> characters;
    private List<Line> lines;
    private List<CharacterRenderInfoGroup> grouping;

    public PageRenderInfo(PdfPage page)
    {
        this.page = page;
        this.characters = new CharacterRenderInfoGetter(page).getCharacterRenderInfos();
        this.grouping = new ArrayList<>();
        for(CharacterRenderInfo c : characters)
        {
            grouping.add(new CharacterRenderInfoGroup(Collections.singletonList(c)));
        }
        this.lines = new LineSegmentInfoGetter(page).getLines();
    }

    /**
     *
     * @return
     */
    public PdfPage getPage()
    {
        return page;
    }

    /**
     *
     * @return
     */
    public List<CharacterRenderInfo> getCharacters()
    {
        return characters;
    }

    /**
     *
     * @return
     */
    public List<Line> getLines()
    {
        return lines;
    }

    public List<CharacterRenderInfoGroup> getGrouping()
    {
        return grouping;
    }

    public void setGrouping(List<CharacterRenderInfoGroup> grouping)
    {
        this.grouping = grouping;
    }
}
