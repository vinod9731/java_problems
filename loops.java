public record loops() {
    public static void main(String[] args) {
         int sum =0;
        for(int n=0; n<5;n++){
            
            sum=sum+n;
        }
        System.err.println(sum);
    }
}
