import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class Player {
    public Vector2 position = new Vector2(1.5f,1.5f);
    public Vector2 direction = new Vector2(1,0);
    public Vector2 cameraPlane = new Vector2(0,0.66f);

    public float speed = 5;
    public float turnRate = 90;

    public InputHandler input = new InputHandler();

    class InputHandler implements KeyListener {


        int keyStates[] = new int[256];

        @Override
        public void keyPressed(KeyEvent e) {
            if( e.getKeyCode() >= 0 && e.getKeyCode() < 256 ){
                keyStates[e.getKeyCode()] = 1;
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if( e.getKeyCode() >= 0 && e.getKeyCode() < 256 ){
                keyStates[e.getKeyCode()] = 0;
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
            
        }
    }

    public void Tick(double delta){
        if(input.keyStates[KeyEvent.VK_UP] == 1){
            move(delta*speed);
        }
        if(input.keyStates[KeyEvent.VK_DOWN] == 1){
            move(-delta*speed);
        }

        if(input.keyStates[KeyEvent.VK_LEFT] == 1){
            direction.rotate(-delta*turnRate);
            cameraPlane.rotate(-delta*turnRate);
        }
        if(input.keyStates[KeyEvent.VK_RIGHT] == 1){
            direction.rotate(delta*turnRate);
            cameraPlane.rotate(delta*turnRate);
        }
    }
    public void move(double amount){
        Vector2 newPosition = position.add(direction.normalized().multiply(amount));
        //Check Collision
        if(Main.map[newPosition.intX()][newPosition.intY()] == 0){
            position = newPosition;
        }
    }
}
