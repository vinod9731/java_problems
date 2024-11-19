
import java.util.Scanner;

public class greeting {
    public static void main(String[] args) {
        System.out.println("enter your name");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.println("hello " + name+",have a good day");
    }
}
