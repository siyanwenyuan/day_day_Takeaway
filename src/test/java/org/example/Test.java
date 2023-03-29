package org.example;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {

        Scanner sc=new Scanner(System.in);
        double score = sc.nextFloat();
        int num= (int) score;
        int flog;
        char string;
        if(num>90&&num<100){
            string='A';
            flog=1;
        }else if(num>80&&num<90){
            string='B';
            flog=2;
        }else if(num>70&&num<80){
            string='C';
            flog=3;
        }else if(num>60&&num<70){
            string='D';
            flog=4;
        }else if(num<60){
            string='E';
            flog=5;
        }


    }

    @org.junit.jupiter.api.Test
    void test(){
        Scanner sc=new Scanner(System.in);
        double v = sc.nextDouble();
        if(v>90&&v<100){
            System.out.println("grade=A");
        }else if(v>80&&v<90){
            System.out.println("grade=B");
        }else if(v>70&&v<80){
            System.out.println("grade=C");
        }else if(v>60&&v<70){
            System.out.println("grade=D");
        }else if(v<60){
            System.out.println("grade=E");
        }else{
            System.out.println("输入的成绩不符合规则");
        }
    }


}

