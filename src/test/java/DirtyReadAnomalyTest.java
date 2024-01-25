public class DirtyReadAnomalyTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    Thread thread1 = new Thread(() -> {
      transaction1.read(data);
      transaction1.commit();
    });

    Thread thread2 = new Thread(() -> {
      transaction2.read(data);
      int value = 0;
      if (transaction2.values.containsKey(data.dataId)) {
        value = transaction2.values.get(data.dataId);
      }
      transaction2.write(data.dataId, value + 1);
      transaction2.rollback();
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    int finalDataValue = data.value;
    int finalTransaction1Value = transaction1.values.get(data.dataId);
    if (finalDataValue == 0 && finalTransaction1Value == 1) {
      System.err.println(
          "Dirty Read Anomaly occurred: data.getValue() is 0 and transaction1.getValue() is 1");
    }
  }
}
