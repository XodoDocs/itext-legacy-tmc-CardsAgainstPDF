import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Joris Schellekens on 6/1/2017.
 */
public class CAHBuilder {

    // sizes
    private static final float PAGE_HEIGHT = 792;
    private static final float PAGE_WIDTH = 612;

    // margins
    private static final float TOP_MARGIN = 20;
    private static final float BOTTOM_MARGIN = 20;
    private static final float LEFT_MARGIN = 20;
    private static final float RIGHT_MARGIN = 20;

    // cell
    private static final float CELL_WIDTH  = 144;
    private static final float CELL_HEIGHT = 144;

    // grid
    private static final int NOF_ROWS = 5;
    private static final int NOF_COLS = 4;

    // branding
    private static final String BRANDING_SLOGAN = "Cards Against Pdf";
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;

    public static void main(String[] args) throws IOException {

        // IO
        File whiteInput = new File("C:\\Users\\Joris Schellekens\\Documents\\code\\cardsagainstpdf\\white.txt");
        File blackInput = new File("C:\\Users\\Joris Schellekens\\Documents\\code\\cardsagainstpdf\\black.txt");
        File output = new File(whiteInput.getParentFile(),"CardsAgainstPdf.pdf");

        // build
        buildCards(whiteInput, blackInput, output);
    }


    private static String[] readTXT(File input) throws FileNotFoundException {

        // read file
        String tmp = "";
        Scanner sc = new Scanner(input);
        while(sc.hasNextLine()) {
            tmp += sc.nextLine() + "\n";
        }
        sc.close();

        // split
        String[] txts = tmp.split("\n");
        for(int i=0;i<txts.length;i++)
        {
            String txt = txts[i].replaceAll("[\n\r]+","");
            if(!txt.endsWith(".") && !txt.endsWith("?") && !txt.endsWith("!"))
                txt = txt + ".";
            txt = txt.substring(0,1).toUpperCase() + txt.substring(1);
            txts[i] = txt;
        }
        return txts;
    }

    private static float fontSize(String text)
    {
        // if there are more than 100 characters, go to 12f
        if(text.length() > 100)
            return 10f;

        // if there are more than 50 characters, go to 12f
        if(text.length() > 50)
            return 12f;

        // default should now be 14f
        int longestWordLen = 0;
        for(String w : text.split(" "))
            longestWordLen = java.lang.Math.max(longestWordLen, w.length());

        if (longestWordLen >= 20)
            return 10f;

        if(longestWordLen >= 40)
            return 5f;

        // default
        return 14f;
    }

    private static void addTable(Document layoutDocument, String[] lines, Color foreground, Color background) throws IOException {

        PdfFont font = PdfFontFactory.createFont("C:\\Windows\\Fonts\\tahomabd.ttf");
        int cellsPerPage = NOF_COLS * NOF_ROWS;
        for(int i=0;i<java.lang.Math.ceil(lines.length/(cellsPerPage + 0.0));i++)
        {
            Table table = new Table(4);
            for(int j=0;j<cellsPerPage;j++) {

                if(i*cellsPerPage + j >= lines.length)
                    continue;

                // add cell representing one white card
                Cell cardCell = new Cell();
                cardCell.setBackgroundColor(background);
                cardCell.setHeight(CELL_HEIGHT);
                cardCell.setWidth(CELL_WIDTH);
                cardCell.setBorder(new SolidBorder(foreground, 1));

                // title in Tahoma bold 14
                Paragraph txtParagraph = new Paragraph(lines[i * cellsPerPage + j]);
                txtParagraph.setFont(font);
                txtParagraph.setFontColor(foreground);

                float fontSize = fontSize(lines[i*cellsPerPage+j]);
                txtParagraph.setFontSize(fontSize);
                txtParagraph.setMargin(5f);
                cardCell.add(txtParagraph);

                // logo

                // branding
                Paragraph brdParagraph = new Paragraph(BRANDING_SLOGAN);
                brdParagraph.setFontColor(foreground);
                brdParagraph.setFont(font);
                brdParagraph.setFontSize(5);
                brdParagraph.setMargin(5f);
                cardCell.add(brdParagraph);

                // add cell
                table.addCell(cardCell);
            }
            layoutDocument.add(table);
            if(i != java.lang.Math.ceil(lines.length/(cellsPerPage + 0.0)) - 1)
                layoutDocument.add(new AreaBreak());
        }
    }

    /**
     * build cards
     * @param blackInput file of text lines, separated by newline
     * @param output a pdf file
     * @throws IOException
     */
    public static void buildCards(File blackInput, File whiteInput, File output) throws IOException {

        String[] blackTxt = readTXT(blackInput);
        String[] whiteTxt = readTXT(whiteInput);

        PdfWriter writer = new PdfWriter(output.getAbsolutePath());
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document layoutDocument = new Document(pdfDocument);
        pdfDocument.setDefaultPageSize(new PageSize(PAGE_WIDTH, PAGE_HEIGHT));
        layoutDocument.setMargins(TOP_MARGIN,RIGHT_MARGIN,BOTTOM_MARGIN,LEFT_MARGIN);

        // add black
        addTable(layoutDocument, whiteTxt, WHITE, BLACK);

        // add white
        layoutDocument.add(new AreaBreak());
        addTable(layoutDocument, blackTxt, BLACK, WHITE);

        layoutDocument.close();
    }
}
