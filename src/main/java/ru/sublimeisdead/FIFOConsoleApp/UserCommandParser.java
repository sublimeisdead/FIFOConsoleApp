package ru.sublimeisdead.FIFOConsoleApp;

import ru.sublimeisdead.FIFOConsoleApp.Model.UserCommand;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.*;
import java.util.Locale;


public interface UserCommandParser {

     String[] parse(String[] userInput) throws Exception;

     default boolean isUserCommand(String userInput) {
         for(UserCommand userCommand:UserCommand.values()){
             if(userCommand.name().equals(userInput.trim())){
                 return true;
             }
         }
         return false;
     }

     default Integer parseInteger(String integer) {
         try{
             int i=Integer.parseInt(integer.trim());
             if(i<0){
                 return null;
             }
             return i;
         }catch (NumberFormatException e){
             return null;
         }

     }

     default BigDecimal parseBigDecimal(String bigDecimalString){
         Locale ru_RU=new Locale("ru","RU");
         DecimalFormat decimalFormat=(DecimalFormat) NumberFormat.getInstance(ru_RU);
         decimalFormat.setParseBigDecimal(true);

         BigDecimal bigDecimal=(BigDecimal) decimalFormat.parse(bigDecimalString.trim(), new ParsePosition(0));

         if(bigDecimal!=null && bigDecimal.compareTo(BigDecimal.ZERO)>=0){
             return bigDecimal;
         }else return null;
     }

     default Date parseDate(String date){
         SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy");
         sdf.setLenient(false);
         java.util.Date retDate;

         try{
             retDate=sdf.parse(date.trim());
         }catch (ParseException e){
             return null;
         }
         return new Date(retDate.getTime());

     }
}
