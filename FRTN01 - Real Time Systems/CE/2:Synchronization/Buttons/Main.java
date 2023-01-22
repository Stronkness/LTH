public class Main {
    public static void main(String[] args) {
      final int regulPriority   = 8;
      final int buttonsPriority = 7;
      final int squarePriority  = 6;
      final int opcomPriority   = 5;

      Box box = new Box();

      FirstOrderProcess simulatedProcess = new FirstOrderProcess();

      Regul regul = new Regul(regulPriority, box, simulatedProcess);
      GUIOpCom opcom = new GUIOpCom(regul, opcomPriority);
      SquareWave squarewave = new SquareWave(regul, squarePriority);
      Buttons buttons = new Buttons(regul, squarewave, buttonsPriority, box);

      regul.start();
      opcom.start();
      squarewave.start();
      buttons.start();
    }
  }