import org.junit.jupiter.api.Test;

import java.util.UUID;


public class test1 {
   @Test
    public void test1(){
      try {
          throw new RuntimeException("haha");
      }catch (Exception e){
          System.out.println(e.getMessage());
      }
   }

   @Test
    public void test2(){
       UUID uuid = UUID.randomUUID();
       System.out.println(uuid);
   }
}
