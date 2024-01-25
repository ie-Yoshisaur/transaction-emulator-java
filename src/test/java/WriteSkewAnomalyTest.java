public class WriteSkewAnomalyTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data1 = new Data(1, 0);
    final Data data2 = new Data(2, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    Thread thread1 = new Thread(() -> {
      transaction1.read(data1);
      transaction1.read(data2);
      int value = 0;
      if (transaction1.values.containsKey(data1.dataId)) {
        value = transaction1.values.get(data1.dataId);
      }
      transaction1.write(data2.dataId, value + 1);
      transaction1.commit();
    });

    Thread thread2 = new Thread(() -> {
      transaction2.read(data2);
      transaction2.read(data1);
      int value = 0;
      if (transaction2.values.containsKey(data2.dataId)) {
        value = transaction2.values.get(data2.dataId);
      }
      transaction2.write(data1.dataId, value + 1);
      transaction2.commit();
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    int finalTransaction1Value1 = transaction1.values.get(data1.dataId);
    int finalTransaction1Value2 = transaction1.values.get(data2.dataId);
    assert !(finalTransaction1Value1 == 1 && finalTransaction1Value2 == 1)
        : "Write Skew Anomaly occurred: finalTransaction1Value1 is 1 and finalTransaction1Value2 is 1";
  }
}
