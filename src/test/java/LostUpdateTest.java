public class LostUpdateTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    Thread thread1 = new Thread(() -> {
      transaction1.read(data);
      int value = 0;
      if (transaction1.values.containsKey(data.dataId)) {
        value = transaction1.values.get(data.dataId);
      }
      transaction1.write(data.dataId, value + 42);
      transaction1.commit();
    });

    Thread thread2 = new Thread(() -> {
      transaction2.read(data);
      int value = 0;
      if (transaction2.values.containsKey(data.dataId)) {
        value = transaction2.values.get(data.dataId);
      }
      transaction2.write(data.dataId, value + 1);
      transaction2.commit();
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    int finalDataValue = data.dataId;
    int finalTransactionValue = transaction1.values.get(data.dataId);
    assert !(finalDataValue == 1 && finalTransactionValue == 42)
        : "Lost Update anomaly occurred: finalTransactionValue is 1 and finalTransactionValue is 42";
  }
}
