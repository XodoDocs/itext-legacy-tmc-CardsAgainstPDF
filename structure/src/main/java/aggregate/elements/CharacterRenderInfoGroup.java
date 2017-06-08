package aggregate.elements;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joris Schellekens on 6/7/2017.
 */
public class CharacterRenderInfoGroup extends ArrayList<CharacterRenderInfo> {

    public static CharacterRenderInfoGroup collapse(List<CharacterRenderInfoGroup> characterRenderInfoGroups)
    {
        CharacterRenderInfoGroup retval = new CharacterRenderInfoGroup();
        for(CharacterRenderInfoGroup c : characterRenderInfoGroups)
        {
           retval.addAll(c);
        }
        return retval;
    }

    public CharacterRenderInfoGroup(List<CharacterRenderInfo> chars) {
        this.addAll(chars);
    }

    public CharacterRenderInfoGroup(CharacterRenderInfo c)
    {
        this.add(c);
    }

    public CharacterRenderInfoGroup()
    {
    }

    public float avgCharWidth()
    {
        float avg = 0f;
        for(CharacterRenderInfo c : this)
        {
            avg += c.getBoundingBox().getWidth();
        }
        avg /= size();
        return avg;
    }

    public float varCharWidth()
    {
        float avg = avgCharWidth();
        float var = 0f;
        for(CharacterRenderInfo c : this)
        {
            var += java.lang.Math.pow(avg - c.getBoundingBox().getWidth(), 2);
        }
        return var;
    }

    public float devCharWidth()
    {
        return (float) Math.sqrt(varCharWidth());
    }

    public Rectangle getBoundingBox()
    {
        float minX = get(0).getBoundingBox().getX();
        float minY = get(0).getBoundingBox().getY();
        float maxX = get(0).getBoundingBox().getRight();
        float maxY = get(0).getBoundingBox().getTop();
        for(CharacterRenderInfo c : this)
        {
            minX = java.lang.Math.min(minX, c.getBoundingBox().getX());
            minY = java.lang.Math.min(minY, c.getBoundingBox().getY());
            maxX = java.lang.Math.max(maxX, c.getBoundingBox().getRight());
            maxY = java.lang.Math.max(maxY, c.getBoundingBox().getTop());
        }
        return new Rectangle(minX, minY, java.lang.Math.abs(maxX- minX), java.lang.Math.abs(maxY-minY));
    }

    public String toString()
    {
        String s  = "";
        int index = 0;
        while(index < size())
        {
            int nextIndex = index;
            while(nextIndex < size() && get(nextIndex).getBoundingBox().getX() > get(index).getBoundingBox().getX())
                nextIndex++;
            s += CharacterRenderInfo.toString(subList(index, nextIndex+1));
            index = nextIndex+1;
        }
        return s;
    }

}
