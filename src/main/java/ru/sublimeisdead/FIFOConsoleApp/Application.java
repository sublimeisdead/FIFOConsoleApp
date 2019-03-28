package ru.sublimeisdead.FIFOConsoleApp;




import ru.sublimeisdead.FIFOConsoleApp.Model.ResultCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;


public class Application {
    public static void main(String[] args){


        DBHandler handler=new DBHandler();
        handler.initDB();
        BufferedReader br= new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Enter command:");
            String inputString = null;
            try {
                inputString = br.readLine();
            } catch (IOException e) {
                System.out.println(ResultCode.ERROR);

            }
            assert inputString != null;
            if (inputString.equalsIgnoreCase("exit")) {
                System.out.println("User entered 'exit' ");
                break;
            }
            String[] splited = inputString.split(" ");
            Arrays.stream(splited).map(String::trim).toArray(u -> splited);

            switch (splited[0]) {
                case "NEWPRODUCT":
                    NewProductParser newProductParser = new NewProductParser();
                    String[] parsedNewProduct = newProductParser.parse(splited);
                    if(parsedNewProduct!=null) {
                        handler.newProductCommand(parsedNewProduct[1]);
                    }
                    break;
                case "PURCHASE":
                    PurchaseAndDemandParser purchaseParser = new PurchaseAndDemandParser();
                    String[] parsedPurchase = purchaseParser.parse(splited);
                    if(parsedPurchase!=null) {
                        handler.purchaseCommand(parsedPurchase[1],
                                purchaseParser.parseInteger(parsedPurchase[2]),
                                purchaseParser.parseBigDecimal(parsedPurchase[3]),
                                purchaseParser.parseDate(parsedPurchase[4]));
                    }
                    break;
                case "DEMAND":
                    PurchaseAndDemandParser demandParser = new PurchaseAndDemandParser();
                    String[] parsedDemand = demandParser.parse(splited);

                    if(parsedDemand!=null) {
                        handler.demandCommand(parsedDemand[1],
                                demandParser.parseInteger(parsedDemand[2]),
                                demandParser.parseBigDecimal(parsedDemand[3]),
                                demandParser.parseDate(parsedDemand[4]));
                    }
                    break;

                case "SALESREPORT":
                    SalesreportParser salesreportParser = new SalesreportParser();
                    String[] parsedReport = salesreportParser.parse(splited);
                    if(parsedReport!=null) {
                        BigDecimal bd = handler.salesreportCommand(parsedReport[1], salesreportParser.parseDate(parsedReport[2]));

                        if(bd!=null){
                            System.out.println(bd);
                        }
                    }
                    break;

                default:
                    System.out.println(ResultCode.ERROR);


            }
        }


    }

}


