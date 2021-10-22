import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Image;
import com.sun.org.apache.bcel.internal.generic.INEG;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import  org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import sun.jvm.hotspot.memory.FileMapInfo;

import javax.imageio.stream.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class PdfCreator {
    private List<List<Object>> data;

    PdfCreator(List<List<Object>> data){
        this.data = data;
    }

    public void  Start(){
        printStudents();
        int studentIndex = choiceStudent();
        createPdf(studentIndex);
    }

    private  int choiceStudent(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Choice a student who need certificate: ");
        String choiceStr = scan.nextLine();
        boolean checkError = tryParseInt(choiceStr) ;
        int chocie =  checkError ? Integer.parseInt(choiceStr) - 1 : 0 ;
        while (!checkError || chocie < 0 || chocie > data.size() ){
            System.out.println("Input error,try again");
            System.out.println("Choice a student who need certificate: ");
            choiceStr = scan.nextLine();
            checkError = tryParseInt(choiceStr);
            chocie =  checkError ? Integer.parseInt(choiceStr) - 1 : 0 ;
        }
        return chocie;
    }

    private boolean tryParseInt(String str){
        try {
            Integer.parseInt(str);
            return true;
        }catch (NumberFormatException e){
            return  false;
        }
    }

    private void printStudents(){
        if(data.isEmpty() || data == null) {
            System.out.println("No data found!");
            return;
        }
        for (List row : data ) {
            System.out.print(data.indexOf(row)+1);
            System.out.printf(":  %s %s code: %s phone: %s\n",row.get(0),row.get(1),row.get(2),row.get(3));
        }
    }

    private void createPdf(int studentIndex)  {
        String certificateBgPath = getBackgroundFilePath();

        try{
            String resultPath = "/Result";
            PdfDocument pdf = new PdfDocument(new PdfWriter("Cetrificate for " + data.get(studentIndex).get(0).toString()));
            Document document = new Document(pdf);
            String line = data.get(studentIndex).get(0).toString() + " " +data.get(studentIndex).get(1).toString();
            ImageData data = ImageDataFactory.create(certificateBgPath);
            Image img = new Image(data);
            document.add(img);
            document.add(new Paragraph(line).setFixedPosition(240,590,1000).setFontSize(24));
            document.close();
        }catch (IOException e){

        }
    }

    private String getBackgroundFilePath(){
        System.out.println("Input file path to background : ");
        Scanner scan = new Scanner(System.in);
        String path = scan.nextLine();
        File fileInfo = new File(path);
        while (!fileInfo.exists()){
            System.out.println("Input file path error,try again");
            path = scan.nextLine();
            fileInfo = new File(path);
        }
        return  path;
    }

}
