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

    public static void main(String[] args) throws IOException {

        // white cards
        File input = new File("C:\\Users\\Joris Schellekens\\Desktop\\cah\\white.txt");
        buildCards(input, Color.WHITE, Color.BLACK);

        // black cards
        input = new File("C:\\Users\\Joris Schellekens\\Desktop\\cah\\black.txt");
        buildCards(input, Color.BLACK, Color.WHITE);
    }

    /**
     * Build a pdf containing CAH cards
     * @param input file of text lines, separated by newline
     * @param background the background color
     * @param foreground the foreground color
     * @throws IOException
     */
    public static void buildCards(File input, Color background, Color foreground) throws IOException {

        // extract name
        String name = input.getName();
        name = name.substring(0, name.lastIndexOf("."));

        // output file
        File output = new File(input.getParentFile(), name + ".pdf");

        // build
        buildCards(input, output, background, foreground);
    }

    /**
     * build cards
     * @param input file of text lines, separated by newline
     * @param output a pdf file
     * @param background the background color
     * @param foreground the foreground color
     * @throws IOException
     */
    public static void buildCards(File input, File output, Color background, Color foreground) throws IOException {

        // read entire file
        String[] txts = new Scanner(input).useDelimiter("\\z").next().split("\n");
        for(int i=0;i<txts.length;i++)
        {
            String txt = txts[i].replaceAll("[\n\r]+","");
            if(!txt.endsWith(".") && !txt.endsWith("?") && !txt.endsWith("!"))
                txt = txt + ".";
            txt = txt.substring(0,1).toUpperCase() + txt.substring(1);
            txts[i] = txt;
        }

        PdfWriter writer = new PdfWriter(output.getAbsolutePath());
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document layoutDocument = new Document(pdfDocument);
        pdfDocument.setDefaultPageSize(new PageSize(PAGE_WIDTH, PAGE_HEIGHT));
        layoutDocument.setMargins(TOP_MARGIN,RIGHT_MARGIN,BOTTOM_MARGIN,LEFT_MARGIN);

        PdfFont font = PdfFontFactory.createFont("C:\\Windows\\Fonts\\tahomabd.ttf");
        int cellsPerPage = NOF_COLS * NOF_ROWS;
        for(int i=0;i<java.lang.Math.ceil(txts.length/(cellsPerPage + 0.0));i++)
        {
            Table table = new Table(4);
            for(int j=0;j<cellsPerPage;j++) {

                if(i*cellsPerPage + j >= txts.length)
                    continue;

                // add cell representing one white card
                Cell cardCell = new Cell();
                cardCell.setBackgroundColor(background);
                cardCell.setHeight(CELL_HEIGHT);
                cardCell.setWidth(CELL_WIDTH);
                cardCell.setBorder(new SolidBorder(foreground, 1));

                // title in Tahoma bold 14
                Paragraph txtParagraph = new Paragraph(txts[i * cellsPerPage + j]);
                txtParagraph.setFont(font);
                txtParagraph.setFontColor(foreground);

                int txtLen = txts[i*cellsPerPage+j].length();
                float fontSize = txtLen < 100 ? ( txtLen < 50 ? 14 : 12) : 10;
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
            if(i != java.lang.Math.ceil(txts.length/(cellsPerPage + 0.0)) - 1)
                layoutDocument.add(new AreaBreak());
        }

        layoutDocument.close();
    }
}
