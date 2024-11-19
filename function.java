/*import java.util.*;   1.  print the name using the function
public class function {
    public static void printmyname(String name) {
        System.out.println(name);
        return;
    }
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        String name=sc.next();

        printmyname(name);//calling the function

        
    }
}
 */
/*2. using the function calculate the sum of two number
 import java.util.*;
public class function {
    public static int calcsum(int a,int b){
        int sum=a+b;
        return sum;
    }
    
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int a=sc.nextInt();
        int b=sc.nextInt();

        int sum =calcsum(a, b);

        System.out.println(sum);//calling the function

        
    }
}
 */


 /*3. make a function to multiply 2 number and return the product
   import java.util.*;
public class function {
    public static int calcsum(int a,int b){
        int product=a*b;
        return product;
    }
    
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int a=sc.nextInt();
        int b=sc.nextInt();

        int product =calcsum(a, b);

        System.out.println("product of two number is "+product);//calling the function

        
    }
}
  */

/*4. find the factorial of number using the function method
 * import java.util.*;
public class function {
    public static void factorial(int a){
        int fact=1;
        if(a<0){
            System.out.println("invalid");
        }
        else{
            for(int i=a; i>=1;i--){
                fact=fact*i;
            }
            System.out.println(fact);

        }
    }
    
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int a=sc.nextInt();
        factorial(a);
    
    }
}
 */

import java.util.*;
public class function {
    public static void factorial(int a){
        
        if(a==0){
            System.out.println("number is even");
        }
        else{
            for(int i=1;i<=10;i++){
                System.out.println(a+"*"+i+ "=" +a*i);
            }
            
            
        }
        return;
    }
    
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int a=sc.nextInt();
        
        
    factorial(a);
    
    }
}
