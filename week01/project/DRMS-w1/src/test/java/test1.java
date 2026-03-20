import org.junit.jupiter.api.Test;


public class test1 {
   @Test
    public void test1(){
      try {
          throw new RuntimeException("haha");
      }catch (Exception e){
          System.out.println(e.getMessage());
      }
   }
}
