package ru.sublimeisdead.FIFOConsoleApp;

import ru.sublimeisdead.FIFOConsoleApp.Model.ResultCode;

public class NewProductParser implements UserCommandParser {

    @Override
    public String[] parse(String[] splitedUserInput) {

        if(splitedUserInput.length!=2 || splitedUserInput==null){
            System.out.println(ResultCode.ERROR);
            return null;
        }else return splitedUserInput;
    }
}
