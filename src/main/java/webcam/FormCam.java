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

