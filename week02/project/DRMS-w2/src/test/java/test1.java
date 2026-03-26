import com.dems.utils.RepairRecordUtil;
import org.junit.jupiter.api.Test;

public class test1 {
    @Test
    public void test1() {
        String userid="3444";
        Integer buildingId=-1;
        boolean empty = RepairRecordUtil.isEmpty(buildingId);
        System.out.println(empty);

    }
}
