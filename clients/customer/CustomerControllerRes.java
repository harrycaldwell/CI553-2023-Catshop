package clients.customer;

/**
 * The Customer Controller
 * @author M A Smith (c) June 2014
 */

public class CustomerControllerRes
{
  private CustomerModel model = null;
  private CustomerViewRes  view  = null;

  /**
   * Constructor
   * @param model The model 
   * @param view2  The view from which the interaction came
   */
  public CustomerControllerRes( CustomerModel model, CustomerViewRes view2 )
  {
    this.view  = view2;
    this.model = model;
  }

  /**
   * Check interaction from view
   * @param pn The product number to be checked
   */
  public void doCheck( String pn )
  {
    model.doCheck(pn);
  }
  
  /**
   * Checks interaction from view
   * @param pn in this case the product name to be checked
   */
  public void doCheck2( String pn )
  {
    model.doCheck2(pn);
  }

  /**
   * Clear interaction from view
   */
  public void doClear()
  {
    model.doClear();
  }
  
  
  
  /**
   * Buy interaction from view
   */
  public void doBuy()
  {
    model.doBuy();
  }
  
  /**
   * Remove interaction from view
   */
  public void doRemove() {
	  model.doRemove();
  }
  
  /**
   * Bought interaction from view
   */
  public void doReserve()
  {
    model.doReserve();
  }

  /**
   * Checks interaction from view
   * @param pn in this case the product name to be checked
   */
  public void doCheck3( String pn )
  {
    model.doCheck3(pn);
  }
}

