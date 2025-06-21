package webcam;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

public class FormCAM extends javax.swing.JFrame {
    
    private DaemonThread myThread = null;
    
    VideoCapture video = null;
    
    MatOfByte frameB = new MatOfByte();
    
    MatOfRect faceDetections = new MatOfRect(); 
    
    String xmlPath = FormCAM.class.getResource("/haarcascade_frontalface_default.xml").getPath();
    
    CascadeClassifier faceDetector = new CascadeClassifier(xmlPath);
   
    class DaemonThread implements Runnable {
        
        volatile boolean runnable = false;
        @Override
               public void run (){
                    synchronized(this){
                        while(runnable){
                            if(video.grab()){
                                try{
                                    video.retrieve(frame);
                                    Graphics g = panelcam.getGraphics();
                                    faceDetector.detectMultiScale(frame, faceDetections, 1.3, 3, 0, new Size(30,30));
                                   
                                    for(Rect rect : faceDetections.toArray()){
                                        Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0,255,0), 3);
                                    }
                                    Imgcodecs.imencode(".bmp",frame, frameB);
                                    Image im = ImageIO.read(new ByteArrayInputStream(frameB.toArray()));
                                    BufferedImage buff = (BufferedImage) im;
                                    if (g.drawImage(buff, 0, 0, getWidth(), getHeight(), 0, 0, buff.getWidth(), buff.getHeight(),null)){
                                       if(runnable == false){
                                           System.out.println("Pausando...");
                                           this.wait();
                                       } 
                                    
                                    }
                        
                                }catch(IOException ex){
                            
                                    System.out.println("Erro!");
                        
                                }catch(InterruptedException ex){
                                    
                                    Logger.getLogger(FormCAM.class.getName()).log(Level.SEVERE, null, ex);
                                }  
                            }
                        }
                    }
                }
            }

    
 private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormCAM.class.getName());

    public FormCAM() {
        initComponents();
    }


                                                                                               @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        panelnav = new javax.swing.JPanel();
        btnplay = new javax.swing.JButton();
        btnstop = new javax.swing.JButton();
        panelcam = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        panelnav.setBackground(new java.awt.Color(255, 0, 0));
        panelnav.setPreferredSize(new java.awt.Dimension(221, 100));

        btnplay.setText("iniciar");
        btnplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnplayActionPerformed(evt);
            }
        });

        btnstop.setText("parar");

        javax.swing.GroupLayout panelnavLayout = new javax.swing.GroupLayout(panelnav);
        panelnav.setLayout(panelnavLayout);
        panelnavLayout.setHorizontalGroup(
            panelnavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelnavLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(btnplay)
                .addGap(39, 39, 39)
                .addComponent(btnstop)
                .addContainerGap(519, Short.MAX_VALUE))
        );
        panelnavLayout.setVerticalGroup(
            panelnavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelnavLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelnavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnstop)
                    .addComponent(btnplay))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelcamLayout = new javax.swing.GroupLayout(panelcam);
        panelcam.setLayout(panelcamLayout);
        panelcamLayout.setHorizontalGroup(
            panelcamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelcamLayout.setVerticalGroup(
            panelcamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 582, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelnav, javax.swing.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
            .addComponent(panelcam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelnav, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelcam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void btnplayActionPerformed(java.awt.event.ActionEvent evt) {                                        
        video = new VideoCapture(0);
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
        btnplay.setEnabled(false);
        btnstop.setEnabled(true);
          // TODO add your handling code here:
    }  


    private void btnstopActionPerformed(java.awt.event.ActionEvent evt) {                                        
        myThread.runnable = false;           
        
        btnplay.setEnabled(true);
        btnstop.setEnabled(false);
        
        video.release();
    }                                       
   public static void main(String args[]) throws Exception {
