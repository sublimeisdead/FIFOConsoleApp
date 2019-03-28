package ru.sublimeisdead.FIFOConsoleApp;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class NewProductParserTest {

    @Test
    void parseLength() {
        NewProductParser parser= new NewProductParser();
        String[] badStrings1={"asd","asdd","asd"};
        String[] badStrings2={"asd"};
        assertNull(parser.parse(badStrings1));
        assertNull(parser.parse(badStrings2));
        String[] goodStrings={"asd","asdd"};
        assertEquals(goodStrings,parser.parse(goodStrings));
    }
}