package per.lningxx.stock.tools;

public class CommonUtils {

    public static boolean isBlank(Object x){
        return "".equals(nvl(x));
    }

    public static boolean notBlank(Object x){
        return !"".equals(nvl(x));
    }

    public static String nvl(Object x){
        if (x == null) {
            return "";
        }
        return x.toString();
    }
}
