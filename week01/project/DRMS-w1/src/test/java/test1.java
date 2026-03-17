public class test1 {
    public static void main(String[] args) {
        String regex1 = "^(3125|3225)\\d{6}$";//学生工号的正则
        System.out.println("85004503".matches(regex1));
    }
}
