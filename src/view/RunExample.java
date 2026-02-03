package view;

import controller.Controller;
import exception.MyException;

public class RunExample extends Command {
    private Controller ctr;

    public RunExample(String key, String desc, Controller ctr) {
        super(key, desc);
        this.ctr = ctr;
    }

    @Override
    public void execute() {
        try {
            ctr.allStep();
            // The output is now logged to a file.
        } catch (MyException e) {
            // Catch all interpreter exceptions
            System.out.println("!!! RUNTIME ERROR !!!");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("!!! UNEXPECTED JAVA ERROR !!!");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}