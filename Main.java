
import app.Facade;
import jcurses.system.Toolkit;

public class Main {
    public static void main(String[] args) {
        Facade facade = new Facade();
        Toolkit.init();
        facade.start();
        Toolkit.shutdown();
    }
}