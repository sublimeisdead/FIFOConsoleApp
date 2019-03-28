package ru.sublimeisdead.FIFOConsoleApp;

import ru.sublimeisdead.FIFOConsoleApp.Model.ResultCode;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBHandler {

    public void initDB(){

        String createPurchaseTableStatement="CREATE TABLE IF NOT EXISTS purchase (  id INT AUTO_INCREMENT NOT NULL,"+
               " id_product INT NOT NULL,"+
        "price DECIMAL(10,2) NOT NULL,"+
                "amount INT NOT NULL,"+
        "purchase_date DATE, PRIMARY KEY (id), FOREIGN KEY (id_product) REFERENCES products (id));";

        String createDemandTableStatement="CREATE TABLE IF NOT EXISTS demand (  id INT AUTO_INCREMENT NOT NULL,"+
                " id_product INT NOT NULL,"+
                "price DECIMAL(10,2) NOT NULL,"+
                "amount INT NOT NULL,"+
                "demand_date DATE, PRIMARY KEY (id), FOREIGN KEY (id_product) REFERENCES products (id));";

        String createProductsTableStatement="CREATE TABLE IF NOT EXISTS products (  id  INT AUTO_INCREMENT NOT NULL,"+
                " product_name VARCHAR(50) NOT NULL,"+
                "PRIMARY KEY (id), CONSTRAINT UC_product_name UNIQUE (product_name));";



        try(Connection connection=DatabaseConnection.getInstance().getConnection();
            Statement statement=connection.createStatement()){

            statement.execute(createProductsTableStatement);
            statement.execute(createPurchaseTableStatement);
            statement.execute(createDemandTableStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean productExists( String productName){
        String checkProductStatement="SELECT * FROM products WHERE product_name = ?";
        try(Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement(checkProductStatement)){
            statement.setString(1,productName);
            ResultSet resultSet=statement.executeQuery();

            if(!resultSet.next()){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public ResultCode newProductCommand(String newProduct){

        String insertNewProductStatement="INSERT INTO products (product_name) VALUES (?)";
        if(!productExists(newProduct)){
            try(Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement(insertNewProductStatement)){
                statement.setString(1,newProduct);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println(ResultCode.OK);
            return ResultCode.OK;
        }else
            System.out.println(ResultCode.ERROR);
            return ResultCode.ERROR;
    }

    public ResultCode purchaseCommand(String productName, Integer amount, BigDecimal price, Date purchaseDate){


            String purchaseStatement = "INSERT INTO purchase (id_product, AMOUNT, PRICE, purchase_date) VALUES (?,?,?,?)";


        try(Connection connection=DriverManager.getConnection("jdbc:h2:file:~/MyWarehouse","sa","");
                 PreparedStatement statement = connection.prepareStatement(purchaseStatement)) {
                statement.setInt(1, getIdProduct(productName));
                statement.setInt(2, amount);
                statement.setBigDecimal(3, price);
                statement.setDate(4, purchaseDate);
                statement.executeUpdate();

                System.out.println(ResultCode.OK);
                return ResultCode.OK;
            } catch (Exception e) {
               System.out.println(ResultCode.ERROR);
                return ResultCode.ERROR;

            }
    }

    public ResultCode demandCommand(String productName, Integer amount, BigDecimal price, java.sql.Date demandDate)  {

        int result= 0;
        try {
            result = checkProductsLeftByDate(productName,demandDate,amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(result==-1){
            return ResultCode.ERROR;
        }

        String purchaseStatement = "INSERT INTO demand (id_product, amount, price, demand_date) VALUES (?,?,?,?)";


        try(Connection connection=DriverManager.getConnection("jdbc:h2:file:~/MyWarehouse","sa","");
                 PreparedStatement statement = connection.prepareStatement(purchaseStatement)) {
                statement.setInt(1, getIdProduct(productName));
                statement.setBigDecimal(3, price);
                statement.setInt(2, amount);
                statement.setDate(4, demandDate);
                statement.executeUpdate();
                System.out.println(ResultCode.OK);
                return ResultCode.OK;
            } catch (SQLException e) {
                System.out.println(ResultCode.ERROR);
                e.printStackTrace();
                return ResultCode.ERROR;

            }

    }

    public BigDecimal salesreportCommand(String productName, java.sql.Date demandDate){
        List<Integer> purchaseAmounts=new ArrayList<>();
        List<BigDecimal> purchasePrices=new ArrayList<>();
        List<Integer> demandAmounts=new ArrayList<>();
        List<BigDecimal> demandPrices=new ArrayList<>();

        String purchaseHistory="SELECT AMOUNT,PRICE, SUM(AMOUNT*PRICE) FROM PURCHASE LEFT JOIN PRODUCTS P ON PURCHASE.ID_PRODUCT = P.ID  WHERE "+
        "p.PRODUCT_NAME=? and PURCHASE_DATE<=? GROUP BY AMOUNT,PRICE,PURCHASE_DATE ORDER BY PURCHASE_DATE";

        String demandHistory="SELECT AMOUNT,PRICE, SUM(AMOUNT*PRICE) FROM DEMAND LEFT JOIN PRODUCTS P ON DEMAND.ID_PRODUCT = P.ID  WHERE " +
                " p.PRODUCT_NAME=? and DEMAND_DATE<=? GROUP BY AMOUNT,PRICE,DEMAND_DATE ORDER BY DEMAND_DATE";


        BigDecimal summaryDemandPrice=new BigDecimal(0);
        int summaryDemandAmount=0;



        try(Connection connection=DriverManager.getConnection("jdbc:h2:file:~/MyWarehouse","sa","")){
            PreparedStatement statement = connection.prepareStatement(purchaseHistory);
            statement.setString(1, productName);
            statement.setDate(2, demandDate);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                purchaseAmounts.add(rs.getInt(1));
                purchasePrices.add(rs.getBigDecimal(2));
            }

            statement = connection.prepareStatement(demandHistory);
            statement.setString(1, productName);
            statement.setDate(2, demandDate);
            rs = statement.executeQuery();
            while(rs.next()){
                demandAmounts.add(rs.getInt(1));
                demandPrices.add(rs.getBigDecimal(2));
                summaryDemandAmount=summaryDemandAmount+rs.getInt(1);
                summaryDemandPrice=summaryDemandPrice.add(rs.getBigDecimal(3));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try{
        int i;
        int summaryPurchaseAmount=0;
        BigDecimal selfCost=new BigDecimal(0);
        for(i=0;;){
            if(summaryPurchaseAmount<summaryDemandAmount){
                summaryPurchaseAmount=summaryPurchaseAmount+purchaseAmounts.get(i);
                selfCost=selfCost.add(BigDecimal.valueOf(purchaseAmounts.get(i)).multiply(purchasePrices.get(i)));
                i++;
            }else {
                selfCost=selfCost.subtract(BigDecimal.valueOf(summaryPurchaseAmount-summaryDemandAmount)
                        .multiply(purchasePrices.get(i-1)));
                break;
            }
        }

        return summaryDemandPrice.subtract(selfCost);
        }catch (Exception e){
            System.out.println(ResultCode.ERROR);
            return null;
        }

    }

    public Integer getIdProduct(String productName){
        String getIdProductStatement="SELECT id FROM products WHERE product_name = ?";


    Integer idProduct=null;
        try(Connection connection=DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(getIdProductStatement)) {
            statement.setString(1, productName);
            ResultSet rs=statement.executeQuery();
            while (rs.next()){
                idProduct=rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(ResultCode.ERROR);
            e.printStackTrace();
        }
        return idProduct;
    }

    public Integer checkProductsLeftByDate(String productName,Date demandDate, Integer demandAmount) throws SQLException {
        String productsLeftQuantityQuery="SELECT SUM(AMOUNT) FROM PURCHASE LEFT JOIN PRODUCTS P ON PURCHASE.ID_PRODUCT = P.ID WHERE PRODUCT_NAME=? AND PURCHASE_DATE<=?";
        String demandProductsQuantityQuery="SELECT SUM(AMOUNT) FROM DEMAND LEFT JOIN PRODUCTS P ON DEMAND.ID_PRODUCT = P.ID WHERE PRODUCT_NAME=? AND DEMAND_DATE<=?";
        Integer productsLeftQuantity = null;
        Integer desiredDemandQuantity = null;
        try(Connection connection=DatabaseConnection.getInstance().getConnection()){

            PreparedStatement stmt=connection.prepareStatement(productsLeftQuantityQuery);
            stmt.setString(1,productName);
            stmt.setDate(2,demandDate);
            ResultSet purchaseResSet=stmt.executeQuery();
            while (purchaseResSet.next()){
                productsLeftQuantity= purchaseResSet.getInt(1);
            }

            stmt=connection.prepareStatement(demandProductsQuantityQuery);
            stmt.setString(1,productName);
            stmt.setDate(2,demandDate);

            ResultSet demandResSet=stmt.executeQuery();
            while (demandResSet.next()){
                desiredDemandQuantity=demandResSet.getInt(1)+demandAmount;
            }
            purchaseResSet.close();
            demandResSet.close();
            stmt.close();
            if(desiredDemandQuantity>productsLeftQuantity){
                System.out.println(ResultCode.ERROR);
                return -1;
            }else return 1;
        }
    }



}
