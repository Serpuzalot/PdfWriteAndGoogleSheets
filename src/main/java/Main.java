import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "MY GOOGLE SHEETS Project";
    private static String SPREADSHEET_ID = "1p3gh90EYeDzQNe4rjf8JmMN5sbOEsAWj6c7a1UjnKR8";
    static List<List<Object>> values ;

    private static Credential authorize() throws IOException,GeneralSecurityException{

        InputStream in = Main.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets  = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),new InputStreamReader(in)
        );

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),JacksonFactory.getDefaultInstance(),
                clientSecrets,scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow,new LocalServerReceiver())
                .authorize("user");

        return credential;

    }

    public  static Sheets getSheetsService() throws IOException,GeneralSecurityException{
        Credential credential =authorize();
        return  new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static synchronized  List<List<Object>> getDataFromSheets() throws IOException {
        try {
            sheetsService = getSheetsService();
        } catch (IOException e) {
            System.out.println(e);
        } catch (GeneralSecurityException e) {
            System.out.println(e);
        }
        String range = "mainList!A2:F";

        ValueRange response= sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID,range)
                .execute();
        List<List<Object>> values  = response.getValues();
        for (List<Object> el : values){
            for(Object a : el){
                if(a.toString() == null){
                    values.remove(el);
                    break;
                }
            }
        }
        return values;
    }

    public static synchronized  List<List<Object>> getNeededEltoCreatePDF( List<List<Object>> data) throws FileNotFoundException {
        ArrayList<List<Object>> neededData = new ArrayList<List<Object>>();
        File file = new File("lastStudentCode.txt");
        //FileReader reader = new FileReader(file);
        //String str = reader.;
        String lastCode = PdfCreator.getLastStudentCode();
        if(lastCode == null || lastCode.isEmpty()){
            neededData.addAll(data);
        }else{
            long lastCodeLong = Long.parseLong(lastCode);
            for (List<Object> el : data){
                String codeEl =  el.get(2).toString();
                long codeElLong = Long.parseLong(codeEl);
                if(lastCodeLong < codeElLong){
                    neededData.add(el);
                }
            }
        }
        return  neededData;
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException, InterruptedException {
        Object lock = new Object();

        Runnable task = () -> {
                try {
                    values = getDataFromSheets();
                    List<List<Object>> nonCertificated = getNeededEltoCreatePDF(values);
                    if(!nonCertificated.isEmpty()){
                        PdfCreator creator = new PdfCreator(nonCertificated);
                        creator.start();
                    }
                } catch (IOException e) {

                }
        };
        boolean stopButton = false;
        while (!stopButton){
            Thread t = new Thread(task);
            t.start();
            //System.out.println("Chose operation:");



            TimeUnit.SECONDS.sleep(60);
        }


    }

}

