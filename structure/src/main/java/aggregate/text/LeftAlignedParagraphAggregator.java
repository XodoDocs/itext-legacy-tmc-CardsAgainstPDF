package aggregate.text;

import aggregate.elements.CharacterRenderInfoGroup;
import aggregate.elements.IStructure;
import aggregate.elements.PageRenderInfo;
import aggregate.stats.ArrayStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Joris Schellekens on 6/7/2017.
 */
public class LeftAlignedParagraphAggregator implements IStructure{

    private PageRenderInfo in;

    @Override
    public PageRenderInfo process(PageRenderInfo in) {
        this.in = in;

        List<CharacterRenderInfoGroup> ungrouped = new ArrayList<>(in.getGrouping());
        int nofUngrouped = ungrouped.size();

        List<CharacterRenderInfoGroup> grouping = new ArrayList<>();
        while(nofUngrouped > 0)
        {
            List<CharacterRenderInfoGroup>  lPara = leftAligned(ungrouped.get(0));
            List<CharacterRenderInfoGroup> cPara = centerAligned(ungrouped.get(0));
            List<CharacterRenderInfoGroup> para = new ArrayList<>();

            if(cPara.size() > lPara.size())
            {
                para.addAll(cPara);
            }
            else
            {
                para.addAll(lPara);
            }

            if(para.size() > 1) {
                CharacterRenderInfoGroup flat = CharacterRenderInfoGroup.collapse(para);
                grouping.add(flat);
            }

            ungrouped.removeAll(para);
            nofUngrouped = ungrouped.size();
        }

        in.setGrouping(grouping);
        return in;
    }

    private List<CharacterRenderInfoGroup> findEntireParagrap(CharacterRenderInfoGroup seedGroup, List<CharacterRenderInfoGroup> tmp)
    {
        if(tmp.size() == 1)
            return Collections.singletonList(seedGroup);

        // sort according to y-position
        java.util.Collections.sort(tmp,Collections.reverseOrder( new Comparator<CharacterRenderInfoGroup>() {
            @Override
            public int compare(CharacterRenderInfoGroup o1, CharacterRenderInfoGroup o2) {
                float y0 = o1.getBoundingBox().getY();
                float y1 = o2.getBoundingBox().getY();
                if(y0 < y1)
                    return -1;
                if(java.lang.Math.abs(y0-y1) < 1f)
                    return 0;
                return 1;
            }}
        ));

        // average y-gap
        float[] yGaps = new float[tmp.size()-1];
        for(int i=1;i<tmp.size();i++)
        {
            float y0 = tmp.get(i-1).getBoundingBox().getBottom();
            float y1 = tmp.get(i).getBoundingBox().getTop();
            yGaps[i-1] = java.lang.Math.abs(y0 - y1);
        }
        float avgYGap = ArrayStats.avg(yGaps);
        float devYGap = ArrayStats.deviance(yGaps);

        System.out.println("starting from : " + seedGroup);

        int startIndex = tmp.indexOf(seedGroup);

        // find beginning of paragraph
        while(startIndex > 0)
        {
            float bottom = tmp.get(startIndex-1).getBoundingBox().getBottom();
            float h0 = tmp.get(startIndex-1).getBoundingBox().getHeight();

            float top = tmp.get(startIndex).getBoundingBox().getTop();
            float h1 = tmp.get(startIndex).getBoundingBox().getHeight();

            float yGap = java.lang.Math.abs(bottom - top);
            float avgHeight = (h0 + h1) / 2f;
            float devHeight = (float) Math.sqrt(Math.pow(h0 - avgHeight, 2) + Math.pow(h1 - avgHeight, 2));

            // do not merge lines that differ too much in height
            if(devHeight > 2f)
                break;

            // gap should not be wider than height of line
            if(yGap > avgHeight + devHeight * 1.65)
                break;

            // gap should not be wider than average gap
            if(yGap > avgYGap + devYGap * 1.65)
                break;

            System.out.println("\tgoing up : " + tmp.get(startIndex));

            // default
            startIndex--;
        }

        // find end of paragraph
        int endIndex = tmp.indexOf(seedGroup);
        while(endIndex < tmp.size() - 1)
        {
            float bottom = tmp.get(endIndex).getBoundingBox().getBottom();
            float h0 = tmp.get(endIndex).getBoundingBox().getHeight();

            float top = tmp.get(endIndex+1).getBoundingBox().getTop();
            float h1 = tmp.get(endIndex+1).getBoundingBox().getHeight();

            float yGap = java.lang.Math.abs(bottom -top);
            float avgHeight = (h0 + h1) / 2f;
            float devHeight = (float) Math.sqrt(Math.pow(h0 - avgHeight, 2) + Math.pow(h1 - avgHeight, 2));

            // do not merge lines that differ too much in height
            if(devHeight > 2f)
                break;

            // gap should not be wider than height of line
            if(yGap > (avgHeight + devHeight * 1.65))
                break;

            // gap should not be wider than average gap
            if(yGap > avgYGap + devYGap * 1.65)
                break;

            System.out.println("\tgoing down : " + tmp.get(endIndex));

            // default
            endIndex++;
        }

        // iterate
        return tmp.subList(startIndex, endIndex+1);
    }

    private List<CharacterRenderInfoGroup> leftAligned(CharacterRenderInfoGroup characterRenderInfoGroup)
    {

        float left = characterRenderInfoGroup.getBoundingBox().getLeft();
        float margin = characterRenderInfoGroup.avgCharWidth() + 1.65f * characterRenderInfoGroup.devCharWidth();

        // make list of all elements that are left aligned with this element
        List<CharacterRenderInfoGroup> tmp = new ArrayList<>();
        for(CharacterRenderInfoGroup c : in.getGrouping())
        {
            float cLeft = c.getBoundingBox().getLeft();
            if(cLeft >= (left - margin/2f) && cLeft <= (left + margin/2f))
            {
                tmp.add(c);
            }
        }

        return findEntireParagrap(characterRenderInfoGroup, tmp);
    }

    private List<CharacterRenderInfoGroup> centerAligned(CharacterRenderInfoGroup characterRenderInfoGroup)
    {

        float center = (characterRenderInfoGroup.getBoundingBox().getLeft() + characterRenderInfoGroup.getBoundingBox().getRight()) / 2f;
        float margin = characterRenderInfoGroup.avgCharWidth() + 1.65f * characterRenderInfoGroup.devCharWidth();

        // make list of all elements that are left aligned with this element
        List<CharacterRenderInfoGroup> tmp = new ArrayList<>();
        for(CharacterRenderInfoGroup c : in.getGrouping())
        {
            float cCenter = (c.getBoundingBox().getLeft() + c.getBoundingBox().getRight()) / 2f;
            if(cCenter >= (center - margin/2f) && cCenter <= (center + margin/2f))
            {
                tmp.add(c);
            }
        }

        return findEntireParagrap(characterRenderInfoGroup, tmp);
    }

}
