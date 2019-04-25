
public class Main {

    public static void main(String[] args)
    {
        MathServerSocket socketKeeper = new MathServerSocket(7755, 25);
        socketKeeper.start();
    }
}
