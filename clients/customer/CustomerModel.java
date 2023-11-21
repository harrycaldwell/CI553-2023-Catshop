package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReadWriter;
import middle.StockReader;

import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * Implements the Model of the customer client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class CustomerModel extends Observable
{
  private Product     theProduct = null;          // Current product
  private Basket      theBasket  = null;          // Bought items

  private String      pn = "";                    // Product being processed

  private StockReader     theStock     = null;
  private StockReadWriter resStock		= null;
  private OrderProcessing theOrder     = null;
  private ImageIcon       thePic       = null;

  /*
   * Construct the model of the Customer
   * @param mf The factory to create the connection objects
   */
  public CustomerModel(MiddleFactory mf)
  {
    try                                          // 
    {  
      theStock = mf.makeStockReader();           // Database access
    } catch ( Exception e )
    {
      DEBUG.error("CustomerModel.constructor\n" +
                  "Database not created?\n%s\n", e.getMessage() );
    }
    theBasket = makeBasket();                    // Initial Basket
  }
  
  /**
   * return the Basket of products
   * @return the basket of products
   */
  public Basket getBasket()
  {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String productNum )
  {
    theBasket.clear();                          // Clear s. list
    String theAction = "";
    String pn  = productNum.trim();                    // Product no.
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( theStock.exists( pn ) )              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetails( pn ); //  Product
        if ( pr.getQuantity() >= amount )       //  In stock?
        { 
          theAction =                           //   Display 
            String.format( "%s : %7.2f (%2d) ", //
              pr.getDescription(),              //    description
              pr.getPrice(),                    //    price
              pr.getQuantity() );               //    quantity
          pr.setQuantity( amount );             //   Require 1
          theBasket.add( pr );                  //   Add to basket
          thePic = theStock.getImage( pn );     //    product
        } else {                                //  F
          theAction =                           //   Inform
            pr.getDescription() +               //    product not
            " not in stock" ;                   //    in stock
        }
      } else {                                  // F
        theAction =                             //  Inform Unknown
          "Unknown product number " + pn;       //  product number
      }
    } catch( StockException e )
    {
      DEBUG.error("CustomerClient.doCheck()\n%s",
      e.getMessage() );
    }
    setChanged(); notifyObservers(theAction);
  }
  
  /**
   * Check if any products that relate to the name are in stock or exist
   * @param productName the products name
   */
  public void doCheck2(String productName)
  {
	  /* Checks if what is entered contains a 0. If it does, calls on the original doCheck() method to
	  	 search via productNumber instead. */
	  if(productName.contains("0")) {
		  doCheck(productName);
		  return;
	  }
    theBasket.clear();   
    String theAction = "";
    //Translates the product name to pn, gets rid of any blank space
    pn  = productName.trim();    
    int    amount  = 1;               
    try
    {      
      //Creates an array list of any products that match the criteria
      ArrayList<Product> products = theStock.getDetailsName( pn );
       for (Product pr : products) {
        if ( theStock.existsName(pr.getDescription()) ){  
        	if ( pr.getQuantity() >= amount )
        	{ 
        		theAction =
        				String.format( "%s : %7.2f (%2d) ",
        						pr.getProductNum(),  
        						pr.getPrice(),   
        						pr.getQuantity() );
        		pr.setQuantity( amount );      
        		theBasket.add( pr );               
        		thePic = theStock.getImage( pr.getProductNum() ); 
        	} else {              
        		theAction =            
        				pr.getDescription() +         
        				" this item does not exist." ; 
        	}
        }
        else {       
            theAction =                             //  Inform Unknown
              "Unknown Product Query with the name:  " + pn;       //  product number
          }
        }
       
    } catch( StockException e )
    {
      DEBUG.error("CustomerClient.doCheck()\n%s",
      e.getMessage() );
    }
    setChanged(); notifyObservers(theAction);
  }
  /**
   * Clear the products from the basket
   */
  public void doClear()
  {
    String theAction = "";
    theBasket.clear();                        // Clear s. list
    theAction = "Enter Product Number";       // Set display
    thePic = null;                            // No picture
    setChanged(); notifyObservers(theAction);
  }
  
  /**
   * Return a picture of the product
   * @return An instance of an ImageIcon
   */ 
  public ImageIcon getPicture()
  {
    return thePic;
  }
  
  /**
   * ask for update of view callled at start
   */
  private void askForUpdate()
  {
    setChanged(); notifyObservers("START only"); // Notify
  }

  /**
   * Make a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }

// ---- NEW FOR CUSTOMER CLIENT RESERVATION ----
  private enum State { process, checked }
  private State       theState   = State.process;   // Current state

  /**
   * make a Basket when required
   */
  private void makeBasketIfReq()
  {
    if ( theBasket == null )
    {
      try
      {
        int uon   = theOrder.uniqueNumber();     // Unique order num.
        theBasket = makeBasket();                //  basket list
        theBasket.setOrderNum( uon );            // Add an order number
      } catch ( OrderException e )
      {
        DEBUG.error( "Comms failure\n" +
                     "CashierModel.makeBasket()\n%s", e.getMessage() );
      }
    }
  }
  
  
/**
 * Buy the product
 */
public void doBuy()
{
  String theAction = "";
  int    amount  = 1;                         //  & quantity
  try
  {
    if ( theState != State.checked )          // Not checked
    {                                         //  with customer
      theAction = "Check if OK with customer first";
    } else {
      boolean stockBought =                   // Buy
        resStock.buyStock(                    //  however
          theProduct.getProductNum(),         //  may fail              
          theProduct.getQuantity() );         //
      if ( stockBought )                      // Stock bought
      {
      	boolean inBasket = false;
        makeBasketIfReq(); 
        for (int i =0; i < theBasket.size(); i++) {
	    		if (theBasket.get(i).getProductNum().equals(theProduct.getProductNum())) {
	    			theBasket.get(i).setQuantity(theBasket.get(i).getQuantity()+ 1);
	    			inBasket = true;
	    		}
	    	}//  new Basket ?
        if (!inBasket) {theBasket.add( theProduct );   }       //  Add to bought
        theAction = "Purchased " +            //    details
                theProduct.getDescription();  //
      } else {                                // F
        theAction = "!!! Not in stock";       //  Now no stock
      }
    }
  } catch( StockException e )
  {
    DEBUG.error( "%s\n%s", 
          "CashierModel.doBuy", e.getMessage() );
    theAction = e.getMessage();
  }
  theState = State.process;                   // All Done
  setChanged(); notifyObservers(theAction);
}

/**
 * Cashier Removes item from Order
 */
public void doRemove() {
	  DEBUG.trace("Remove accessed");
	    String theAction = "";
	    try {
	    if (theBasket.size() > 0 && theBasket != null && theState == State.checked) {
	    	for (int i =0; i < theBasket.size(); i++) {
	    		if (theBasket.get(i).getProductNum().equals(theProduct.getProductNum())) {
	    			if (theBasket.get(i).getQuantity() > 1) {
	    				theBasket.get(i).setQuantity(theBasket.get(i).getQuantity() - 1);
	    			}
	    			else {
	    				theBasket.remove(i);
	    				resStock.addStock(theProduct.getProductNum(),theProduct.getQuantity());
	    			}
	    		}
	    	}
		    theAction = "Removed " + theProduct.getDescription() + " from the basket";
		    theState = State.process;
		} 
	    else {
		    theAction = "No item to remove or item cannot be removed";
		}
	    }
	    catch( StockException e )
	    {
	      DEBUG.error( "%s\n%s", 
	            "CashierModel.doBuy", e.getMessage() );
	      theAction = e.getMessage();
	    }
	    setChanged(); notifyObservers(theAction);
}

/**
 * Customer pays for the contents of the basket
 */
public void doReserve()
{
  String theAction = "";
  int    amount  = 1;                       //  & quantity
  try
  {
    if ( theBasket != null &&
         theBasket.size() >= 1 )            // items > 1
    {                                       // T
      theOrder.newOrder( theBasket );       //  Process order
      theBasket = null;                     //  reset
    }                                       //
    theAction = "Next customer";            // New Customer
    theState = State.process;               // All Done
    theBasket = null;
  } catch( OrderException e )
  {
    DEBUG.error( "%s\n%s", 
          "CashierModel.doCancel", e.getMessage() );
    theAction = e.getMessage();
  }
  theBasket = null;
  setChanged(); notifyObservers(theAction); // Notify
}

/**
 * Check if the product is in Stock
 * @param productNum The product number
 */
public void doCheck3(String productNum )
{
  String theAction = "";
  theState  = State.process;                  // State process
  pn  = productNum.trim();                    // Product no.
  int    amount  = 1;                         //  & quantity
  try
  {
    if ( theStock.exists( pn ) )              // Stock Exists?
    {                                         // T
      Product pr = theStock.getDetails(pn);   //  Get details
      if ( pr.getQuantity() >= amount )       //  In stock?
      {                                       //  T
        theAction =                           //   Display 
          String.format( "%s : %7.2f (%2d) ", //
            pr.getDescription(),              //    description
            pr.getPrice(),                    //    price
            pr.getQuantity() );               //    quantity     
        theProduct = pr;                      //   Remember prod.
        theProduct.setQuantity( amount );     //    & quantity
        theState = State.checked;             //   OK await BUY 
      } else {                                //  F
        theAction =                           //   Not in Stock
          pr.getDescription() +" not in stock";
      }
    } else {                                  // F Stock exists
      theAction =                             //  Unknown
        "Unknown product number " + pn;       //  product no.
    }
  } catch( StockException e )
  {
    DEBUG.error( "%s\n%s", 
          "CashierModel.doCheck", e.getMessage() );
    theAction = e.getMessage();
  }
  setChanged(); notifyObservers(theAction);
}
}

