package control.ObserverPatron;

import control.ObserverPatron.OSolution;

import java.util.Observer;

public class HandlerObserver {

    private static OSolution oSolution;

    public static void init(){
        if(oSolution == null)
            oSolution = new OSolution();
    }

    public static void removeObservers() {
        if (oSolution != null){
            oSolution.deleteObservers();
            oSolution = null;
        }
    }

    public static void addObserver(Observer o){
        oSolution.addObserver(o);
    }

    public static void removeObserver(Observer o){
        oSolution.deleteObserver(o);
    }

    public static OSolution getoSolution() {
        return oSolution;
    }
}
