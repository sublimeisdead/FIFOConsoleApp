package ru.sublimeisdead.FIFOConsoleApp;

import ru.sublimeisdead.FIFOConsoleApp.Model.ResultCode;

public class SalesreportParser implements UserCommandParser {

    @Override
    public String[] parse(String[] splitedUserInput) {

        try {
            if (splitedUserInput.length != 3 && parseDate(splitedUserInput[2]) == null) {
                return null;
            } else return splitedUserInput;
        }catch (Exception e){
            System.out.println(ResultCode.ERROR);
            return null;
        }
    }

}
