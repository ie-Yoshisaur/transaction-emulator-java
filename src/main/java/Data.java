public class Data {
  private int value;
  private int dataId;

  public Data(int dataId, int value) {
    this.value = value;
    this.dataId = dataId;
  }

  public int getValue() { return value; }

  public void setValue(int value) { this.value = value; }

  public int getDataId() { return dataId; }

  public void setDataId(int dataId) { this.dataId = dataId; }
}
