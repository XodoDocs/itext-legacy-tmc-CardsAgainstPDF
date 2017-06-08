package aggregate.elements;

/**
 * Created by Joris Schellekens on 6/7/2017.
 */
public class RepeatStructure implements IStructure {

    private IStructure element;
    private int nofRepeats;

    public RepeatStructure(IStructure element, int nofRepeats)
    {
        this.element = element;
        this.nofRepeats = nofRepeats;
    }

    @Override
    public PageRenderInfo process(PageRenderInfo in) {
        PageRenderInfo retval = in;
        for(int i=0;i<nofRepeats;i++)
        {
            retval = element.process(retval);
        }
        return retval;
    }
}
