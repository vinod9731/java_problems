
import java.util.Scanner;

public class exs {
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        System.out.println("enter the marks of Englisg:");
        int a=sc.nextInt();
        System.out.println("enter the marks of maths:");
        int b=sc.nextInt();
        System.out.println("enter the marks of science:");
        int c =sc.nextInt();
        System.out.println("enter yhe marks of coa");
        int d=sc.nextInt();
        System.out.println("enter the marks of toc:");
        int e=sc.nextInt();
        
        int percentage = ((a+b+c+d+e)/500)*100;
        System.out.println("percentage is "+percentage+"%");




    }
}
