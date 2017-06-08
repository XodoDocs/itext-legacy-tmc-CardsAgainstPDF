package aggregate.source;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Joris Schellekens on 6/6/2017.
 */
public class CharacterRenderInfoGetter implements ITextExtractionStrategy
{
    private List<CharacterRenderInfo> characterRenderInfoList= new ArrayList<>();

    public CharacterRenderInfoGetter(PdfPage page)
    {
        PdfTextExtractor.getTextFromPage(page, this);
    }

    public List<CharacterRenderInfo> getCharacterRenderInfos()
    {
        java.util.Collections.sort(characterRenderInfoList);
        return characterRenderInfoList;
    }

    public String getResultantText(){ return ""; }

    public void eventOccurred(IEventData iEventData, EventType eventType) {
        if(iEventData instanceof TextRenderInfo)
        {
            for(TextRenderInfo info : ((TextRenderInfo) iEventData).getCharacterRenderInfos())
            {
                characterRenderInfoList.add(new CharacterRenderInfo(info));
            }
        }
    }

    public Set<EventType> getSupportedEvents() {
        return null;
    }
}
