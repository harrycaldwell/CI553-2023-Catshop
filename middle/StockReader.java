package middle;

import catalogue.Product;

import java.util.ArrayList;

import javax.swing.*;

/**
  * Interface for read access to the stock list.
  * @author  Mike Smith University of Brighton
  * @version 2.0
  */

public interface StockReader
{

 /**
   * Checks if the product exits in the stock list
   * @param pNum Product nymber
   * @return true if exists otherwise false
   * @throws StockException if issue
   */
  boolean exists(String pNum) throws StockException;
  
  /**
   * Checks if the product exits in the stock list
   * @param pNum Product name
   * @return true if exists otherwise false
   * @throws StockException if issue
   */
  boolean existsName(String pNum) throws StockException;
  /**
   * Returns details about the product in the stock list
   * @param pNum Product nymber
   * @return StockNumber, Description, Price, Quantity
   * @throws StockException if issue
   */
  
  Product getDetails(String pNum) throws StockException;
  
  
  /**
   * Returns details about the products in the stock list within the array
   * @param desc
   * @return ArrayList with products that match search criteria
   * @throws StockException if issue
   */
  ArrayList<Product> getDetailsName(String desc) throws StockException;
  
  
  /**
   * Returns an image of the product in the stock list
   * @param pNum Product nymber
   * @return Image
   * @throws StockException if issue
   */
  
  ImageIcon getImage(String pNum) throws StockException;
}
