package aggregate.stats;

/**
 * Created by Joris Schellekens on 6/6/2017.
 */
public class ArrayStats {

    public static float avg(float[] fs)
    {
        float v = 0f;
        for(float f : fs)
            v += f;
        v/=fs.length;
        return v;
    }

    public static float variance(float[] fs)
    {
        float a = avg(fs);
        float var = 0f;
        for(float f : fs)
        {
            var += ((f-a)*(f-a));
        }
        var /= fs.length;
        return var;
    }

    public static float deviance(float[] fs)
    {
        return (float) Math.sqrt(variance(fs));
    }
}
