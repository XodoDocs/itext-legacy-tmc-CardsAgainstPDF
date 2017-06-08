import aggregate.elements.*;
import aggregate.text.HeuristicLineAggregator;
import aggregate.text.LeftAlignedParagraphAggregator;
import aggregate.text.SimpleLineAggregator;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.File;
import java.io.IOException;

/**
 * Created by Joris Schellekens on 6/6/2017.
 */
public class Main {

    public static void main(String[] args) throws IOException {

      String[] names = {"7","20","11","13","10","9","26"};
      for(String name : names)
          processPdf(name);

    }

    private static void processPdf(String name) throws IOException {
        File input = new File("C:\\Users\\Joris Schellekens\\Desktop\\pdfs\\" + name + ".pdf");
        File output = new File(input.getParentFile(),name + "_marked.pdf");

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), new PdfWriter(output));
        IStructure s = new CompositeStructure()
                .add(new SimpleLineAggregator())
                .add(new RepeatStructure(new HeuristicLineAggregator(),5))
                .add(new LeftAlignedParagraphAggregator());

        for(int i=1;i<=pdfDocument.getNumberOfPages();i++) {

            PageRenderInfo renderInfo = s.process(new PageRenderInfo(pdfDocument.getPage(i)));

            for (CharacterRenderInfoGroup cl : renderInfo.getGrouping()) {
                PdfCanvas canvas = new PdfCanvas(renderInfo.getPage());
                canvas.setColor(Color.RED, false);
                canvas.rectangle(cl.getBoundingBox());
                canvas.stroke();
            }

        }
        pdfDocument.close();
    }
}
