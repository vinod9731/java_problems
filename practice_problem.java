/* 1. print the sum of odd number of n using function method
import java.util.*;

public class practice_problem {
    public static int sumofodd(int n){
        int sum = 0;
        for(int i=0; i<=n;i++){
            if(i%2!=0){
                sum=sum+i;
            }  
        }
        return sum;
    }
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        System.out.println(sumofodd(n));

        
    }
}
 */

 /*--------------------------------------------------------------------------------------
 2.calculate the greater number from 2 number from input
 import java.util.*;

 public class practice_problem {
     public static int greater(int a,int b){
        if(a>b){
            return a;
        }
        else{
            return b;
        }
     }
     public static void main(String[] args) {
         Scanner sc = new Scanner(System.in);
         int a = sc.nextInt();
         int b = sc.nextInt();
         System.out.println(greater(a, b));
 
         
     }
 } */
 /*-----------------------------------------------------------------------------------------
 3. finding the circumference of circle using the radius
 import java.util.*;

 public class practice_problem {
     public static int greater(int a){
        return (int) (2*3.14*a);
        
     }
     public static void main(String[] args) {
         Scanner sc = new Scanner(System.in);
         int a = sc.nextInt();
         
         System.out.println(greater(a));
         
     }
 } */
// find the fibonacci series number
 public class practice_problem {
    static void printFibonacciNumbers(int n)
    {
        int f1 = 0, f2 = 1, i;
        System.out.print(f1 + " ");
        if (n < 1)
            return;
         
        for (i = 1; i < n; i++) {
            System.out.print(f2 + " ");
            int next = f1 + f2;
            f1 = f2;
            f2 = next;
        }
    }
 
    // Driver Code
    public static void main(String[] args)
    {
        printFibonacciNumbers(7);
    }
}