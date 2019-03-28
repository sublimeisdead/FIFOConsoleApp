package ru.sublimeisdead.FIFOConsoleApp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseAndDemandParserTest {

PurchaseAndDemandParser parser=new PurchaseAndDemandParser();
    @Test
    void parseNotUserCommand() {
        String[] strings={"sdfsdf","iphone","1","2000","01.01.2011"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parsePurchaseCommandWrongCase() {
        String[] strings={"Purchase","iphone","1","2000","01.01.2011"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parseWrongLength() {
        String[] strings={"PURCHASE","iphone","1","2000","01.01.2011","dfsdf"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parseWrongAmountType() {
        String[] strings={"PURCHASE","iphone","g","2000","01.01.2011"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parseNegativeAmount() {
        String[] strings={"PURCHASE","iphone","-1","2000","01.01.2011"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parseWrongPriceType() {
        String[] strings={"PURCHASE","iphone","1","g","01.01.2011"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parseWrongDateType() {
        String[] strings={"PURCHASE","iphone","1","2000.00","hj"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parseWrongDateDelimeter() {
        String[] strings={"PURCHASE","iphone","1","2000.00","01/01/2011"};
        assertNull(parser.parse(strings));
    }

    @Test
    void parseOkPurchaseString() {
        String[] strings={"PURCHASE","iphone","1","2000.00","01.01.2011"};
        assertEquals(strings,parser.parse(strings));
    }

    @Test
    void parseOkDemandString() {
        String[] strings={"DEMAND","iphone","1","2000.00","01.01.2011"};
        assertEquals(strings,parser.parse(strings));
    }




}