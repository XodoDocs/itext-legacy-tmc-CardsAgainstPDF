package fluent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joris Schellekens on 6/6/2017.
 */
public class ListQuery<T> {

    private ListQuery parent;
    private ListQuery<T>[] children;

    private static enum QueryType
    {
        ALL,
        WHERE,
        AND,
        OR,
        NOT
    }
    private QueryType type;

    public interface QueryFunction<T>
    {
        boolean accept(List<T> l, T item);
    }
    private QueryFunction<T> function;

    public ListQuery(){
        type = QueryType.ALL;
        parent = null;
        children = null;
    }

    public ListQuery where(QueryFunction<T> function)
    {
        ListQuery retval = new ListQuery();
        retval.type = QueryType.WHERE;
        retval.children = new ListQuery[]{this};
        this.parent = retval;
        retval.function = function;
        return retval;
    }

    public ListQuery and(ListQuery q0)
    {
        ListQuery retval = new ListQuery();
        retval.type = QueryType.AND;

        // linkage
        retval.children = new ListQuery[]{this, q0};
        this.parent = retval;
        q0.parent = retval;

        // return
        return retval;
    }

    public ListQuery and(QueryFunction<T> function)
    {
        return and(new ListQuery().where(function));
    }

    public ListQuery or(ListQuery q0)
    {
        ListQuery retval = new ListQuery();
        retval.type = QueryType.OR;

        // linkage
        retval.children = new ListQuery[]{this, q0};
        this.parent = retval;
        q0.parent = retval;

        // return
        return retval;
    }

    public ListQuery or(QueryFunction<T> function)
    {
        return or(new ListQuery().where(function));
    }

    public ListQuery not()
    {
        ListQuery retval = new ListQuery();
        retval.type  = QueryType.NOT;
        retval.children = new ListQuery[]{this};
        this.parent = retval;
        return retval;
    }

    public List<T> execute(List<T> in)
    {
        return execute(in, this);
    }

    private List<T> execute(List<T> in, ListQuery<T> root)
    {
        // ALL
        if(root.type == QueryType.ALL)
            return in;

        // WHERE
        else if(root.type == QueryType.WHERE)
        {
            List<T> retval = new ArrayList<>();
            for(T item : execute(in, root.children[0]))
            {
                if(root.function.accept(in, item))
                    retval.add(item);
            }
            return retval;
        }

        // AND
        else if(root.type == QueryType.AND)
        {
            List<T> l0 = execute(in, root.children[0]);
            List<T> l1 = execute(in, root.children[1]);
            l0.retainAll(l1);
            return l0;
        }

        // OR
        else if(root.type == QueryType.OR)
        {
            List<T> l0 = execute(in, root.children[0]);
            List<T> l1 = execute(in, root.children[1]);
            l0.removeAll(l1);
            l0.addAll(l1);
            return l0;
        }

        // default
        return null;
    }

    public String toString()
    {
        return toString(this, 0);
    }

    private String toString(ListQuery<T> node, int level)
    {
        return type.toString();
    }
}
