import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Image;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.*;
import java.util.List;

public class PdfCreator {
    private List<List<Object>> data;
    private static String lastStudentCode;
    private  static  String certificateBgPath = "C:\\Users\\Serpuzalot\\Desktop\\JavaStart\\JavaProject\\src\\main\\resources\\certificatePhoto.jpg";

    PdfCreator(List<List<Object>> data){
        this.data = data;
    }

    public static String getLastStudentCode() {
        return lastStudentCode;
    }

    public static String getSertificateBgImage() {
        return certificateBgPath;
    }

    public static void setSertificateBgImage(String sertificateBgImage) {
        PdfCreator.certificateBgPath = sertificateBgImage;
    }

    public void  start(){
        for (List<Object> student : data){
            createPdf(student);
        }

    }

    private void createPdf(List<Object> student)  {
        try{
            String resultPath = ".\\Results\\Cetrificate For " + student.get(0) + student.get(1) +".pdf";
            File result = new File(resultPath);
            PdfDocument pdf = new PdfDocument((new PdfWriter(resultPath)));
            pdf.addNewPage();
            Document document = new Document(pdf);
            String line = student.get(0).toString() + " " + student.get(1).toString() ;
            ImageData imageData = ImageDataFactory.create(certificateBgPath);
            Image img = new Image(imageData);
            document.add(img.setFixedPosition(0,0).setWidth(595.276f).setHeight(841.89f));
            document.add(new Paragraph(line).setFixedPosition(230,490,1000).setFontSize(24));
            document.add(new Paragraph("Subject:").setFixedPosition(235,460,1000).setFontSize(24));
            line = student.get(4).toString();
            document.add(new Paragraph(line).setFixedPosition(230,430,1000).setFontSize(24));
            line = "Signature:  ______";
            document.add(new Paragraph(line).setFixedPosition(300,150,400).setFontSize(16));
            line = "Odessa National Polytechnic University";
            document.add(new Paragraph(line).setFixedPosition(200,120,400).setFontSize(14));
            document.close();
            lastStudentCode =  student.get(2).toString();
            saveLastStudentCode(lastStudentCode);
        }catch (IOException e){

        }
    }
    private void saveLastStudentCode (String code) throws IOException {
        File file = new File("lastStudentCode.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();

    }


 //TODO: fix position + add to googleTable last cod student add work gdrive cloud start programm + doc
}
