package aggregate.text;

import aggregate.elements.CharacterRenderInfoGroup;
import aggregate.elements.IStructure;
import aggregate.elements.PageRenderInfo;
import aggregate.stats.ArrayStats;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joris Schellekens on 6/6/2017.
 */
public class SimpleLineAggregator implements IStructure{

    private PageRenderInfo in;

    public PageRenderInfo process(PageRenderInfo in) {

        // copy input
        this.in = in;

        // build
        List<CharacterRenderInfoGroup> crigs = buildLines();

        // return
        in.setGrouping(crigs);
        return in;
    }

    /**
     * Get all horizontal gaps on the line of the character denoted by fromIndex
     * @param fromIndex
     * @return
     */
    private float[] horizontalGaps(int fromIndex) {
        int toIndex = fromIndex;
        while (true) {
            int tmp = getRightNeighbour(toIndex, Float.MAX_VALUE, Float.MAX_VALUE);
            if (tmp == -1)
                break;
            else
                toIndex = tmp;
        }
        float[] retval = new float[toIndex - fromIndex];
        for (int i = 0; i < retval.length; i++) {
            float left = in.getCharacters().get(fromIndex + i).getBoundingBox().getLeft();
            float right = in.getCharacters().get(fromIndex + i + 1).getBoundingBox().getRight();
            retval[i] = java.lang.Math.abs(right - left);
        }
        return retval;
    }

    /**
     * Get all baseline gaps on the line of the character denoted by fromIndex
     * @param fromIndex
     * @return
     */
    private float[] baselineGaps(int fromIndex) {
        int toIndex = fromIndex;
        while (true) {
            int tmp = getRightNeighbour(toIndex, Float.MAX_VALUE, Float.MAX_VALUE);
            if (tmp == -1)
                break;
            else
                toIndex = tmp;
        }
        float[] retval = new float[toIndex - fromIndex];
        for (int i = 0; i < retval.length; i++) {
            float bot0 = in.getCharacters().get(fromIndex + i).getBoundingBox().getBottom();
            float bot1 = in.getCharacters().get(fromIndex + i + 1).getBoundingBox().getBottom();
            retval[i] = java.lang.Math.abs(bot0 - bot1);
        }
        return retval;
    }

    /**
     * Get the index of the immediate right neighbour
     * @param chIndex the character index of the current character
     * @param maxHGap the maximum allowed horizontal gap
     * @param maxBaselineGap the maximum allowed baseline jump
     * @return
     */
    private int getRightNeighbour(int chIndex, float maxHGap, float maxBaselineGap) {
        // find nearest CharacterRenderInfo object
        CharacterRenderInfo ch = in.getCharacters().get(chIndex);
        int rightNbIndex = (chIndex < in.getCharacters().size() - 1) ? chIndex + 1 : -1;
        if (rightNbIndex == -1)
            return rightNbIndex;

        CharacterRenderInfo rightNb = in.getCharacters().get(rightNbIndex);
        if (rightNb == null)
            return -1;
        if (rightNb.getBoundingBox().getX() <= ch.getBoundingBox().getX())
            return -1;
        if (java.lang.Math.abs(rightNb.getBoundingBox().getTop() - ch.getBoundingBox().getBottom()) > java.lang.Math.max(rightNb.getBoundingBox().getHeight(), ch.getBoundingBox().getHeight()) * 1.65f)
            return -1;

        // build rectangle representing gap
        float left = ch.getBoundingBox().getLeft();
        float right = rightNb.getBoundingBox().getRight();
        float top = java.lang.Math.max(rightNb.getBoundingBox().getTop(), ch.getBoundingBox().getTop());
        float bottom = java.lang.Math.max(rightNb.getBoundingBox().getBottom(), ch.getBoundingBox().getBottom());
        Rectangle rect = new Rectangle(left, bottom, right - left, top - bottom);
        if (rect.getWidth() > maxHGap)
            return -1;
        if (java.lang.Math.abs(rightNb.getBoundingBox().getBottom() - ch.getBoundingBox().getBottom()) > maxBaselineGap)
            return -1;

        // check for intersecting lines
        for (Line l : in.getLines()) {
            float x0 = (float) l.getBasePoints().get(0).getX();
            float y0 = (float) l.getBasePoints().get(0).getY();

            float x1 = (float) l.getBasePoints().get(1).getX();
            float y1 = (float) l.getBasePoints().get(1).getY();

            if (rect.intersectsLine(x0, y0, x1, y1))
                return -1;
        }

        // return
        return rightNbIndex;
    }

    private List<CharacterRenderInfoGroup> buildLines() {
        List<CharacterRenderInfoGroup> characterLinesList = new ArrayList<>();
        int fromIndex = 0;
        while (fromIndex < in.getCharacters().size()) {
            // attempt to find a single line
            int to = fromIndex;

            float[] hgaps = horizontalGaps(fromIndex);
            float avgHGap = ArrayStats.avg(hgaps);
            float devHGap = ArrayStats.deviance(hgaps);

            float[] bgaps = baselineGaps(fromIndex);
            float avgBGap = ArrayStats.avg(bgaps);
            float devBGap = ArrayStats.deviance(bgaps);

            while (true) {
                int tmp = getRightNeighbour(to, avgHGap + 2.5f * devHGap + 1f, avgBGap + 2.5f * devBGap + 1f);
                if (tmp == -1)
                    break;
                else
                    to = tmp;
            }

            // add character line
            characterLinesList.add(new CharacterRenderInfoGroup(in.getCharacters().subList(fromIndex, to + 1)));

            // next line
            fromIndex = (to + 1);
        }

        // return
        return characterLinesList;
    }
}
