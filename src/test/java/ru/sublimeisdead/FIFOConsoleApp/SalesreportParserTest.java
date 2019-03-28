package ru.sublimeisdead.FIFOConsoleApp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalesreportParserTest {
    String[] strings={"asdasd","asdasd","asdasd","dfgh"};
    SalesreportParser parser=new SalesreportParser();


    @Test
    void parseWrongLength() {

        assertNull(parser.parse(strings));

    }

    @Test
    void parseWrongTypeOfDate() {
        strings[3]=null;
        assertNull(parser.parse(strings));


    }

    @Test
    void parseWrongTypeOfDateDelimeter() {
        strings[3]=null;
        strings[2]="01/01/2001";
        assertNull(parser.parse(strings));

    }

    @Test
    void parseRightString() {
        strings[3]=null;
        strings[2]="01.01.2001";
        assertEquals(strings,parser.parse(strings));

    }




}