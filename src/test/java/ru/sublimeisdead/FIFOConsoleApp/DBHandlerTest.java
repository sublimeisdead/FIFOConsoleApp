package ru.sublimeisdead.FIFOConsoleApp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.CryptoPro.ssl.D;
import ru.sublimeisdead.FIFOConsoleApp.Model.ResultCode;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DBHandlerTest {
    DBHandler handler=new DBHandler();
    @BeforeEach
    void setUp() {
    clearPurchase();
    clearDemand();
    }

    @AfterEach
    void tearDown() {
       String deleteTestProductStatement="DELETE FROM PRODUCTS WHERE PRODUCT_NAME=?";
        try(Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement(deleteTestProductStatement)){
            statement.setString(1,"testproduct");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    void newProductCommand() {
        assertEquals(ResultCode.OK,handler.newProductCommand("testproduct"));
    }

    @Test
    void productExists() {
        handler.newProductCommand("testproduct");
        assertEquals(ResultCode.ERROR,handler.newProductCommand("testproduct"));

    }


    @Test
    void purchaseCommand() {
      handler.newProductCommand("testproduct");
      Date date=Date.valueOf("2019-01-01");
      assertEquals(ResultCode.OK,handler.purchaseCommand("testproduct",1, new BigDecimal("1000"), date));
      String deleteTestPurchaseProduct="DELETE FROM PURCHASE WHERE PURCHASE.ID_PRODUCT IN (SELECT PRODUCTS.ID FROM PRODUCTS WHERE PRODUCTS.PRODUCT_NAME=?)";
        try(Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement(deleteTestPurchaseProduct)){
            statement.setString(1,"testproduct");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void demandCommand() {
        handler.newProductCommand("testproduct");
        Date date=Date.valueOf("2019-01-01");
        handler.purchaseCommand("testproduct",1,new BigDecimal("1000"),date);
        assertEquals(ResultCode.OK,handler.demandCommand("testproduct",1, new BigDecimal("1000"), date));
        String deleteTestDemandProduct="DELETE FROM DEMAND WHERE DEMAND.ID_PRODUCT IN (SELECT PRODUCTS.ID FROM PRODUCTS WHERE PRODUCTS.PRODUCT_NAME=?)";
        try(Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement(deleteTestDemandProduct)){
            statement.setString(1,"testproduct");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void salesreportCommand() {
        handler.newProductCommand("testproduct");
        Date purchaseDate1=Date.valueOf("2019-01-01");
        Date purchaseDate2=Date.valueOf("2019-02-01");

        handler.purchaseCommand("testproduct",1, new BigDecimal("1000"), purchaseDate1);
        handler.purchaseCommand("testproduct",2, new BigDecimal("2000"), purchaseDate2);
        Date demandDate1=Date.valueOf("2019-03-01");

        handler.demandCommand("testproduct",2,new BigDecimal("5000"),demandDate1);

        assertEquals(new BigDecimal("7000").stripTrailingZeros(),handler.salesreportCommand("testproduct",demandDate1).stripTrailingZeros());
        clearPurchase();
        clearDemand();
    }

    @Test
    void getIdProduct() {
        handler.newProductCommand("testproduct");
        Integer id=handler.getIdProduct("testproduct");
        assertEquals(id,handler.getIdProduct("testproduct"));
    }

    @Test
    void checkProductsLeftByDate() {
        handler.newProductCommand("testproduct");
        Date purchaseDate=Date.valueOf("2019-01-01");
        Date demandDate=Date.valueOf("2019-02-02");
        int demandAmount=2;
        handler.purchaseCommand("testproduct",1,new BigDecimal("100"),purchaseDate);
        handler.demandCommand("testproduct",demandAmount,new BigDecimal("150"),demandDate);
        try {
            assertEquals(new Integer(-1),handler.checkProductsLeftByDate("testproduct",demandDate,demandAmount));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        clearPurchase();
        clearDemand();

    }

    void clearPurchase(){
        String deleteTestPurchaseProduct="DELETE FROM PURCHASE WHERE PURCHASE.ID_PRODUCT IN (SELECT PRODUCTS.ID FROM PRODUCTS WHERE PRODUCTS.PRODUCT_NAME=?)";
        try(Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement(deleteTestPurchaseProduct)){
            statement.setString(1,"testproduct");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void clearDemand(){
        String deleteTestDemandProduct="DELETE FROM DEMAND WHERE DEMAND.ID_PRODUCT IN (SELECT PRODUCTS.ID FROM PRODUCTS WHERE PRODUCTS.PRODUCT_NAME=?)";
        try(Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement(deleteTestDemandProduct)){
            statement.setString(1,"testproduct");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}