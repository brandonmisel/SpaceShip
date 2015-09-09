
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    int rocketXPos;
    int rocketYPos;
    
    int rocketXSpeed;
    int rocketYSpeed;
    
    boolean rocketRight;
    boolean shoot;

    int numStars = 7;
    int starXPos[];
    int starYPos[];
    
    int currentMissile;    
    int numMissiles = 15;
    int missileXPos[];
    int missileYPos[];
    boolean missileActive[] = new boolean[numMissiles];
    boolean missileRight[] = new boolean[numMissiles];
   
    Missile missiles[] = new Missile[Missile.numMissiles] ;
   
    

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
                    shoot = true;
                    missileXPos[Missile.currentMissile] = rocketXPos;
                    missileYPos[Missile.currentMissile] = rocketYPos;
                    missileActive[Missile.currentMissile] = true;
//Move on to the next cannonball.                    
                    Missile.currentMissile++;
                    
// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    rocketYSpeed++;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rocketYSpeed--;
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    rocketXSpeed--;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    rocketXSpeed++;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);

        g.setColor(Color.yellow);
        for (int i=0;i<starXPos.length;i++)
        {
            drawCircle(getX(starXPos[i]),getYNormal(starYPos[i]),
                0.0,1.0,1.0 );
        }
        if (rocketRight)
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        else
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
     
     g.setColor(Color.red);
     for (int i=0;i<starXPos.length;i++)
     {
        if(shoot)
        {
            drawMissile(getX(missileXPos[i]),getYNormal(missileYPos[i]),
                0.0,1/2.0,1/4.0 );
        }
        
        
        
        
     }
        gOld.drawImage(image, 0, 0, null);
    }
    ////////////////////////////////////////////////////////////////////////////
    public void drawPulse(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    ////////////////////////////////////////////////////////////////////////////
    public void drawMissile(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        
        g.fillRect(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        shoot = false;
        
     
       starXPos = new int[numStars] ;
       starYPos = new int[numStars] ;
        
       missileXPos = new int[numMissiles] ;
       missileYPos = new int[numMissiles] ;
        
     for (int i=0;i<missiles.length;i++)
     {
        missiles[i] = new Missile();
        
     } 
        rocketXSpeed = 0;
        rocketYSpeed = 0;
        rocketRight = true;
        for (int i=0;i<starXPos.length;i++)
        {
            starXPos[i] = (int)(Math.random()*getWidth2());
            starYPos[i] = (int)(Math.random()*getHeight2());
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            reset();
            bgSound = new sound("./starwars.wav");             
        }
        if (bgSound.donePlaying)
        {
            bgSound = new sound("./starwars.wav"); 
        }
        for (int i=0;i<starXPos.length;i++)
        {
            starXPos[i] -= rocketXSpeed;
            if (starXPos[i] < 0)
            {
                starYPos[i] = (int)(Math.random()*getHeight2());
                starXPos[i] = getWidth2();
            }
            else if (starXPos[i] > getWidth2())
            {
                starYPos[i] = (int)(Math.random()*getHeight2());
                starXPos[i] = 0;
                
            }
        }
        if (rocketXSpeed > 0)
        {
            rocketRight = true;
        }
        else if (rocketXSpeed < 0)
        {
            rocketRight = false;
        }
        rocketYPos += rocketYSpeed;
        if (rocketYPos < 0)
        {
            rocketYSpeed = 0;
            rocketYPos = 0;
        }
        else if (rocketYPos > getHeight2())
        {
            rocketYSpeed = 0;
            rocketYPos = getHeight2();
        }
        if(rocketXSpeed > 30)
        {
            rocketXSpeed = 30;
        }
        if(rocketXSpeed < -30)
        {
            rocketXSpeed = -30;
        }
        
     for (int i=0;i<starXPos.length;i++)
     {
        if( shoot && rocketRight)
        { 
            missileXPos[i]+=5;
//             missileActive = false;
        }
        else if(shoot && !rocketRight)
        {
            missileXPos[i]-=5;
//            missileActive = false;
        }
     }
   for (int i=0;i<starXPos.length;i++)
   {  
     if(missileXPos[i] > getWidth2())
        {
            shoot = false;
            missileXPos[i] = rocketXPos;
            missileYPos[i] = rocketYPos;
        }
   }
   
}

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                if (newLine.startsWith("numMissiles"))
                {
                    String numStarsString = newLine.substring(12);
                    numMissiles = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}

class Missile
{
    public static int currentMissile = 0;
    public final static int numMissiles = 10;
    
    public int missileXPos;
    public int missileYPos;
    public boolean missileActive;
    public boolean missileRight;
    Missile()
    {
        missileActive = false;
       
    }
    
}