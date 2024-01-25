import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class LostUpdateTest {
  @Test
  public void testLostUpdateTest() throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    threadPool.submit(() -> {
      transaction1.read(data);
      int value = transaction1.getValue(data.getDataId());
      transaction1.write(data.getDataId(), value + 42);
      transaction1.commit();
    });

    threadPool.submit(() -> {
      transaction2.read(data);
      int value = transaction2.getValue(data.getDataId());
      transaction2.write(data.getDataId(), value + 1);
      transaction2.commit();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    int finalDataValue = data.getValue();
    int finalTransactionValue = transaction1.getValue(data.getDataId());
    if (finalDataValue == 1 && finalTransactionValue == 42) {
      fail(
          "Lost Update anomaly occurred: finalTransactionValue is 1 and finalTransactionValue is 42");
    }
  }
}
