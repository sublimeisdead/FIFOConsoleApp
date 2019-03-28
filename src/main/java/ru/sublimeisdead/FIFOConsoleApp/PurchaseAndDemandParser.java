package ru.sublimeisdead.FIFOConsoleApp;


import ru.sublimeisdead.FIFOConsoleApp.Model.ResultCode;

public class PurchaseAndDemandParser implements UserCommandParser {
    @Override
    public String[] parse(String[] splitedUserInput) {

        if(isUserCommand(splitedUserInput[0]) && splitedUserInput.length==5){

            try {
                if (parseInteger(splitedUserInput[2]) != null &&
                        parseBigDecimal(splitedUserInput[3]) != null &&
                        parseDate(splitedUserInput[4]) != null) {
                    return splitedUserInput;
                } else {
                    System.out.println(ResultCode.ERROR);
                    return null;
                }
            }catch (Exception e){
                System.out.println(ResultCode.ERROR);
                return null;
            }

        }else{
            System.out.println(ResultCode.ERROR);
            return null;

        }

    }


}
