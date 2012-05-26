/**
 * 
 */
package org.powertac.tourney.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.powertac.tourney.beans.Game;
import org.powertac.tourney.services.Database;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author constantine
 * 
 */

@Component("actionIndex")
@Scope("request")
public class ActionIndex
{

  private String sortColumn = null;
  private boolean sortAscending = true;
  private int rowCount = 5;

  public List<Game> getGameList()
  {
    List<Game> games = new ArrayList<Game>();
    
    Database db = new Database();
    try{
      db.startTrans();
      games = db.getGames();
      db.commitTrans();
    }catch (SQLException e){
      db.abortTrans();
      e.printStackTrace();
    }
    
    return games;

  }

  public String getSortColumn ()
  {
    return sortColumn;
  }

  public void setSortColumn (String sortColumn)
  {
    this.sortColumn = sortColumn;
  }

  public boolean isSortAscending ()
  {
    return sortAscending;
  }

  public void setSortAscending (boolean sortAscending)
  {
    this.sortAscending = sortAscending;
  }

  public int getRowCount ()
  {
    return rowCount;
  }

  public void setRowCount (int rowCount)
  {
    this.rowCount = rowCount;
  }

}
