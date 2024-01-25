import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class DirtyReadAnomalyTest {
  @Test
  public void testDirtyReadAnomaly() throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    threadPool.submit(() -> {
      transaction1.read(data);
      transaction1.commit();
    });

    threadPool.submit(() -> {
      transaction2.read(data);
      int currentValue = transaction2.getValue(data.getDataId());
      transaction2.write(data.getDataId(), currentValue + 1);
      // When transaction1 reads data at this point, a Dirty Read Anomaly may
      // occur
      transaction2.rollback(); // The data value will be rolled back to 0
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    // Check if Dirty Read Anomaly occurred
    int finalDataValue = data.getValue();
    int finalTransaction1Value = transaction1.getValue(data.getDataId());
    if (finalDataValue == 0 && finalTransaction1Value == 1) {
      fail(
          "Dirty Read Anomaly occurred: data.getValue() is 0 and transaction1.getValue() is 1");
    }
  }
}
