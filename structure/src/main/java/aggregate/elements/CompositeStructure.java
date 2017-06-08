package aggregate.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joris Schellekens on 6/7/2017.
 */
public class CompositeStructure implements IStructure {

    private List<IStructure> structureList = new ArrayList<>();

    public CompositeStructure add(IStructure iStructure)
    {
        structureList.add(iStructure);
        return this;
    }

    @Override
    public PageRenderInfo process(PageRenderInfo in) {
        PageRenderInfo retval = in;
        for(IStructure iStructure : structureList)
        {
            retval = iStructure.process(retval);
        }
        return retval;
    }
}
