import java.util.*;
public class condition {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("enter the number");
        int n =sc.nextInt();
      /* 
      if(n%2==0){
        System.err.println("the number is even");
        }
        else{
            System.out.println("the number is odd");
        }*/  
        switch (n) {
            case 1:System.err.println("january");
            break;
            case 2:System.err.println("februaru");
            break;
            case 3:System.err.println("march");
            break;
            case 4:System.err.println("april");
            break;
            case 5:System.err.println("may");
            break;
            case 6:System.err.println("june");
            break;
            case 7:System.err.println("july");
            break;
            case 8:System.err.println("august");
            break;
            case 9:System.err.println("september");
            break;
            case 10:System.err.println("october");
            break;
            case 11:System.err.println("november");
            break;
            case 12:System.err.println("december");
            break;
        
            default:System.err.println("invalid number");
                break;
        }
    }
}
