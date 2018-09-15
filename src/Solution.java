import java.io.*;
import java.util.*;

public class Solution {

    public static void main(String[] args) {
        int sum = 0;
        int a = 0;
        Scanner sc = new Scanner(System.in);
        int[] date = new int[6];
        for (int i = 0; i < 6; i++){
            date[i] = sc.nextInt();
            //System.out.println(date[i] + " " + i);
        }
        for (int i = 0; i < 6; i++){
            System.out.println(date[i] + " " + i);
        }
        sc.close();
        if (date[2] - date[5] > 0){
            sum = 10000;
        }
        else
        if (date[1] - date[4] > 0){
            sum = (date[1] - date[4]) * 500;
        }
        else
        if (date[0] - date[3] > 0){
            sum = (date[1] - date[4]) * 15;
        }
        System.out.println(sum);
    }
}