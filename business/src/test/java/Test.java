import cn.hutool.core.util.NumberUtil;

import java.util.Arrays;

/**
 * @author ferry
 * @date 2025/12/15
 * @project train
 * @description
 */

public class Test {

    public static void main(String[] args) {
        String sell = "011001110";
        int n = sell.length();
        int num = NumberUtil.binaryToInt(sell);
        System.out.println(num);

        int[] bitmap = new int[n];
        for (int i = 0; i < n; i++) {
            char c = sell.charAt(i);
            if (c == '0') {
                bitmap[i] = 0;
            } else {
                bitmap[i] = 1;
            }
        }
        System.out.println(Arrays.toString(bitmap));
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + bitmap[i];
        }
        System.out.println(Arrays.toString(prefix));

    }
}
