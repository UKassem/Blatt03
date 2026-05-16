package calculator; //Damit die Klasse im gleichen „Ordner“ (Package) ist


public class Sub implements Operation {
    @Override
    public int doOperation(int a, int b){
        return a - b; //Subtraktion
    }
}
