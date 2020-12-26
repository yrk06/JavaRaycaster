import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.TimeUnit;

import java.awt.*;
public class GraphicsEngine extends JPanel {
    
    public int map[][];
    public Vector2 playerPos;
    public Vector2 playerDirection;
    public Vector2 cameraPlane;

    public Vector2 screenSize;

    static int maxViewDistance = 8;
    static boolean fogEnabled = false;

    static int wall[][] = new int[128][128];

    private BufferedImage frame;

    long lastFrameTime = System.currentTimeMillis();
    long currentFrameTime;

    public static void loadImg() throws Exception{
        BufferedImage texture = ImageIO.read(new File("parede.png"));
        for(int x = 0; x<128;x++){
            for(int y = 0; y <128; y++){
                wall[x][y] = texture.getRGB(x, y);
            }
        }
    }

    public GraphicsEngine(Player player, int nMap[][],Vector2 nScreensize){
        setPlayerData(player);
        setMap(nMap);
        screenSize = nScreensize;
        frame = new BufferedImage(screenSize.intX(),screenSize.intY(),BufferedImage.TYPE_4BYTE_ABGR);
        clearFrame();
    }

    public void clearFrame(){
        for(int x = 0; x < screenSize.x;x++){
            for(int y = 0; y < screenSize.y;y++){
                frame.setRGB(x, y, Color.GRAY.getRGB());
            }
        }
    }

    public void setMap(int nMap[][]){
        map = nMap;
    }
    public void setPlayerData(Player player){
        playerPos = player.position;
        playerDirection = player.direction;
        cameraPlane = player.cameraPlane;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //setBackground(Color.GRAY);
        //Run Raycasting Engine
        for(int xPx = 0; xPx < screenSize.intX(); xPx++){
            //Para cada coluna da tela
            
            //Precisamos definir a direção do raio
            float percentOnPlane = (xPx - screenSize.intX()/2.f)/(screenSize.intX()/2.f);

            //Vetor de direção do raio
            Vector2 rayDirection = playerDirection.add(cameraPlane.multiply(percentOnPlane));

            //Distancia até o proximo lado X ou Y
            Vector2 sideDistance = new Vector2(0,0);

            //Distancia entre 2 X e 2 Y consecutivos
            Vector2 deltaDist = new Vector2(Math.abs(1/rayDirection.x),Math.abs(1/rayDirection.y));

            //Onde no mapa nós estamos
            Vector2 mapPosition = new Vector2(playerPos.intX(),playerPos.intY());

            Vector2 step = new Vector2(0,0);

            //Calcular Step inicial e side distances

            if(rayDirection.x < 0){
                step.x = -1;
                sideDistance.x = (playerPos.x - mapPosition.x) * deltaDist.x;
            }
            else {
                step.x = 1;
                //Aqui somamos 1, pois estamos calculando para a borda da Direita. ou seja, a borda do proximo
                // quadrado
                sideDistance.x = (mapPosition.x + 1 - playerPos.x) * deltaDist.x;
            }

            if(rayDirection.y < 0){
                step.y = -1;
                sideDistance.y = (playerPos.y - mapPosition.y) * deltaDist.y;
            }
            else {
                step.y = 1;
                //Aqui somamos 1, pois estamos calculando para a borda da Direita. ou seja, a borda do proximo
                // quadrado
                sideDistance.y = (mapPosition.y + 1 - playerPos.y) * deltaDist.y;
            }

            //Com tudo pronto, o DDA pode começar
            boolean hit = false;
            int side = 0;
            while(!hit){
                if(sideDistance.x < sideDistance.y){
                    sideDistance.x += deltaDist.x;
                    mapPosition.x += step.x;
                    side = 0;
                }
                else{
                    sideDistance.y += deltaDist.y;
                    mapPosition.y += step.y;
                    side = 1;
                }
                if(map[mapPosition.intX()][mapPosition.intY()] != 0) hit = true;
            }

            double perpendicularWallDistance;
            if (side == 0) perpendicularWallDistance = (mapPosition.x - playerPos.x + (1-step.x)/2)/rayDirection.x;
            else perpendicularWallDistance = (mapPosition.y - playerPos.y + (1-step.y)/2)/rayDirection.y;

            int wallHeight = (int) (screenSize.y/perpendicularWallDistance);

            int startPixel = (int) (screenSize.y/2 - wallHeight/2);
            int loopStart = (startPixel < 0) ? 0: startPixel;

            int endPixel =(int) (screenSize.y/2 + wallHeight/2);
            int loopEnd = (endPixel > screenSize.y) ? screenSize.intY(): endPixel;


            double wallX; //where exactly the wall was hit
            if (side == 0) wallX = playerPos.y + perpendicularWallDistance * rayDirection.y;
            else           wallX = playerPos.x + perpendicularWallDistance * rayDirection.x;
            wallX -= Math.floor((wallX));

            int xCoord = (int)(wallX*128);
            int lastCoord = -1;
            int color = 0;
            for(int pixelY = loopStart; pixelY <  loopEnd; pixelY ++){
                int yCoord = Math.abs((pixelY-startPixel)*128/wallHeight);
                if(yCoord == lastCoord){
                    frame.setRGB(xPx, pixelY, color);
                    continue;
                }
                //g.setColor(wall[xCoord][yCoord]);
                //g.drawLine(xPx, pixelY, xPx, pixelY);
                color = wall[xCoord][yCoord];
                frame.setRGB(xPx, pixelY, color);
                lastCoord = yCoord;
            }
            /*if(side==0) g.setColor(Color.CYAN);
            else g.setColor(Color.RED);
            g.drawLine(xPx, startPixel, xPx, endPixel);*/
        }
        
        g.drawImage(frame, 0, 0, screenSize.intX(), screenSize.intY(), null);
        clearFrame();
        currentFrameTime = System.currentTimeMillis();
        double delta = (currentFrameTime-lastFrameTime)/1000.0;
        lastFrameTime = System.currentTimeMillis();
        
        if(delta !=0){
            System.out.printf("FPS: %d fps\n",(int)(1/delta));
        }
        
    }

    private Color mixColor(Color a, Color o, double k){
        double nK = 1.0 - k;
        int r = (int)(a.getRed() * nK + o.getRed()*k);
        int g = (int)(a.getGreen() * nK + o.getGreen()*k);
        int b =(int) (a.getBlue() * nK + o.getBlue()*k);
        return new Color(r,g,b);
    }
}
