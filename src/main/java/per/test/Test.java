package per.test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Test {

    class Ye{}
    class Fu extends Ye{}
    class Zi extends Fu{}

    public static void main(String[] args) {

        Map<Character, Integer> map = new TreeMap<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入待统计字符串：");
        String scStr = scanner.nextLine();

        for (int i = 0; i < scStr.length(); i++) {
            char c = scStr.charAt(i);
            Integer times = map.get(c);
            if (times == null)
                times = 0;
            map.put(c,times+1);
        }


        map.forEach((s, s2)->System.out.println(s + "=" + s2));
    }


}
