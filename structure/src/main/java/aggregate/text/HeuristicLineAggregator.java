package aggregate.text;

import aggregate.elements.CharacterRenderInfoGroup;
import aggregate.elements.IStructure;
import aggregate.elements.PageRenderInfo;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;

import java.util.*;

/**
 * Created by Joris Schellekens on 6/7/2017.
 */
public class HeuristicLineAggregator implements IStructure{

    private PageRenderInfo in;

    public HeuristicLineAggregator()
    {
    }

    /**
     *
     * @param boundingBox
     * @return
     */
    private List<CharacterRenderInfoGroup> getContentOfBox(Rectangle boundingBox)
    {
        List<CharacterRenderInfoGroup> retval = new ArrayList<>();
        for(CharacterRenderInfoGroup cg : in.getGrouping())
        {
            for(CharacterRenderInfo c : cg) {
                float x = c.getBoundingBox().getX();
                float y = c.getBoundingBox().getY();
                float w = c.getBoundingBox().getWidth();
                float h = c.getBoundingBox().getHeight();
                if (x >= boundingBox.getLeft() && y >= boundingBox.getBottom() && (x + w) <= boundingBox.getRight() && (y + h) <= boundingBox.getTop()) {
                    retval.add(cg);
                    break;
                }
            }
        }
        return retval;
    }

    private int countSplitsInBox(Rectangle boundingBox)
    {
        int retval = 0;
        List<CharacterRenderInfoGroup> groups = getContentOfBox(boundingBox);
        for(int i=1;i<groups.size();i++)
        {
            Rectangle r0 = groups.get(i-1).getBoundingBox();
            Rectangle r1 = groups.get(i).getBoundingBox();
            float b0 = r0.getBottom();
            float h0 = r0.getHeight();
            float b1 = r1.getBottom();
            float h1 = r1.getHeight();
            if(java.lang.Math.abs(b0-b1) > java.lang.Math.min(h0,h1)/2f)
                continue;
            retval++;
        }
        return retval;
    }

    @Override
    public PageRenderInfo process(PageRenderInfo in) {

        // copy input
        this.in = in;

        // processing
        List<CharacterRenderInfoGroup> grouping = new ArrayList<>();
        grouping.add(in.getGrouping().get(0));
        for(int i=1;i<in.getGrouping().size();i++)
        {
            CharacterRenderInfoGroup g0 = in.getGrouping().get(i-1);
            CharacterRenderInfoGroup g1 = in.getGrouping().get(i);

            // bounding rectangle
            CharacterRenderInfoGroup g = new CharacterRenderInfoGroup();
            g.addAll(g0);
            g.addAll(g1);

            // Calculate some useful statistics about line-height
            float avg = (g0.getBoundingBox().getHeight() + g1.getBoundingBox().getHeight())  / 2f;
            float var = (float) (Math.pow(g0.getBoundingBox().getHeight() - avg, 2) + Math.pow(g1.getBoundingBox().getHeight() - avg, 2));
            float dev = (float) Math.sqrt(var);

            /* If we suspect the merged box is larger than the average line height, we do not merge.
             * For this aggregator we are only interested in merging characters on the same line.
             */
            if(g.getBoundingBox().getHeight() > avg + 2.5 * dev) {
                grouping.add(g1);
                continue;
            }

            /* define the search-region
             * The search region is a rectangular window in which we expect similarity of breaking/non-breaking behaviour to be consistent.
             */
            float left = g0.getBoundingBox().getRight()-1f;
            float right = g1.getBoundingBox().getLeft()+1f;
            float height = 7f * (avg + dev * 1.65f);
            float bottom = java.lang.Math.min(g0.getBoundingBox().getBottom(), g1.getBoundingBox().getBottom());
            Rectangle searchRegion = new Rectangle(left, bottom - height / 2f, java.lang.Math.abs(right - left),height);
            if(searchRegion.getWidth() > g0.avgCharWidth() + 2f * g0.devCharWidth())
            {
                grouping.add(g1);
                continue;
            }

            // if there are no splits in the search-region it should be safe to merge the two groups
            if(countSplitsInBox(searchRegion) == 0)
            {
                grouping.get(grouping.size() - 1).addAll(g1);
            }
            else
            {
                grouping.add(g1);
            }

        }

        in.setGrouping(grouping);
        return in;
    }
}
