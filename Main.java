import javax.swing.JFrame;
import java.util.concurrent.TimeUnit;

class Vector2{
    public double x;
    public double y;

    public Vector2(double a, double b){
        x = a;
        y = b;
    }
    public int intX(){
        return (int) x;
    }
    public int intY(){
        return (int) y;
    }

    public void rotate(double deg){
        //Converter de Deg pra rad
        double rad = deg/180.f * Math.PI;
        /* cos -sin
         * sin cos 
         */
        double newX = (this.x * Math.cos(rad) - (this.y * Math.sin(rad)));
        double newY = (this.x * Math.sin(rad) + (this.y * Math.cos(rad)));

        this.x = newX;
        this.y = newY;
    }

    public Vector2 multiply(double k){
        return new Vector2(this.x*k,this.y*k);
    }
    public Vector2 add(Vector2 other){
        return new Vector2(this.x+other.x,this.y+other.y);
    }
    public double len(){
        return Math.sqrt(this.x*this.x+this.y*this.y);
    }
    public Vector2 normalized(){
        double len = len();
        return new Vector2((x/len),(y/len));
    }
}

public class Main {

    static Vector2 mapSize = new Vector2(10,10);
    static Vector2 resolution = new Vector2(480,480);

    static int map[][] = {
        {1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,0,1,0,0,0,0,0,0,1},
        {1,0,1,0,0,1,0,0,0,1},
        {1,0,1,0,0,0,0,0,0,1},
        {1,0,1,0,0,0,0,0,0,1},
        {1,0,1,0,0,0,0,0,0,1},
        {1,0,1,1,1,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1},
    };
    public static void main(String[] args) throws Exception{
        GraphicsEngine.loadImg();
        Player player = new Player();


        JFrame screen = new JFrame("game");
        GraphicsEngine rcEngine = new GraphicsEngine(player,map,resolution);
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen.add(rcEngine);
        screen.setSize(resolution.intX(),resolution.intY());
        screen.addKeyListener(player.input);
        screen.setVisible(true);
        long lastFrameTime = System.currentTimeMillis();
        long currentFrameTime;
        while(true){
            currentFrameTime = System.currentTimeMillis();
            double delta = (currentFrameTime-lastFrameTime)/1000.0;
            lastFrameTime = System.currentTimeMillis();
            player.Tick(delta);
            rcEngine.setPlayerData(player);
            screen.repaint();
            
        }
    }
}