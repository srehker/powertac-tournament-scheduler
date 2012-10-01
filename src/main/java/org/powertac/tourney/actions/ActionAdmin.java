package org.powertac.tourney.actions;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.powertac.tourney.beans.*;
import org.powertac.tourney.services.*;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.util.*;

@ManagedBean
@RequestScoped
public class ActionAdmin
{
  private static Logger log = Logger.getLogger("TMLogger");

  private String sortColumnPom = null;
  private boolean sortAscendingPom = true;
  private String sortColumnMachine = null;
  private boolean sortAscendingMachine = true;
  private String sortColumnUsers = null;
  private boolean sortAscendingUsers = true;

  private String locationName = "";
  private int locationTimezone = 0;
  private Date locationStartTime = null;
  private Date locationEndTime = null;

  private int machineId = -1;
  private String machineName = "";
  private String machineUrl = "";
  private String machineViz = "";

  private Upload upload = new Upload();
  private UploadedFile uploadedPom;
  private String pomName;

  private Integer selectedTournament;

  private TournamentProperties properties = TournamentProperties.getProperties();

  public List<Tournament> availableTournaments = new ArrayList<Tournament>();

  public ActionAdmin ()
  {
    loadData();
  }

  @SuppressWarnings("unchecked")
  private void loadData ()
  {
    for (Tournament tournament: Tournament.getNotCompleteTournamentList()) {
      if (tournament.getStartTime().after(Utils.offsetDate())) {
        continue;
      }

      if (tournament.isMulti()) {
        availableTournaments.add(tournament);
      }
    }

    Collections.sort(availableTournaments, new Utils.AlphanumComparator());
  }

  public void restartWatchDog ()
  {
    log.info("Restarting WatchDog");
    Scheduler scheduler = Scheduler.getScheduler();
    scheduler.restartWatchDog();
  }

  public void loadTournament ()
  {
    log.info("Loading Tournament " + selectedTournament);

    Scheduler scheduler = Scheduler.getScheduler();
    scheduler.loadTournament(selectedTournament);
  }

  public void unloadTournament ()
  {
    log.info("Unloading Tournament");

    Scheduler scheduler = Scheduler.getScheduler();
    scheduler.unloadTournament();
  }

  public void destroyCache ()
  {
    new Cache();
  }

  public List<Tournament> getAvailableTournaments ()
  {
    return availableTournaments;
  }

  public List<String> getConfigErrors()
  {
    return properties.getErrorMessages();
  }

  public void removeMessage (String message)
  {
    properties.removeErrorMessage(message);
  }

  //<editor-fold desc="Location stuff">
  public List<Location> getLocationList ()
  {
    return Location.getLocationList();
  }

  public void addLocation ()
  {
    if (locationName.isEmpty() || (locationStartTime == null) || (locationEndTime == null)) {
      return;
    }

    Location location = new Location();
    location.setLocation(locationName);
    location.setFromDate(locationStartTime);
    location.setToDate(locationEndTime);

    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    try {
      locationName = "";
      locationTimezone = 0;
      locationStartTime = null;
      locationEndTime = null;

      session.save(location);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
  }

  public void deleteLocation (Location location)
  {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.delete(location);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
  }
  //</editor-fold>

  //<editor-fold desc="Pom stuff">
  public List<Pom> getPomList ()
  {
    return Pom.getPomList();
  }

  public void submitPom ()
  {
    if (pomName.isEmpty()) {
      // Show succes message.
      String msg = "Error: You need to fill in the pom name";
      FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
      FacesContext.getCurrentInstance().addMessage("pomUploadForm", fm);
      return;
    }

    if (uploadedPom == null) {
      String msg = "Error: You need to choose a pom file";
      FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
      FacesContext.getCurrentInstance().addMessage("pomUploadForm", fm);
      return;
    }

    User currentUser = User.getCurrentUser();
    Pom pom = new Pom();
    pom.setPomName(getPomName());
    pom.setUser(currentUser);

    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();
    try {
      session.save(pom);
    }
    catch (ConstraintViolationException e) {
      session.getTransaction().rollback();
      String msg = "Error: This name is already used";
      FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
      FacesContext.getCurrentInstance().addMessage("pomUploadForm", fm);
      return;
    }

    upload.setUploadedFile(uploadedPom);
    upload.setUploadLocation(properties.getProperty("pomLocation"));
    boolean pomStored = upload.submit("pom." + pom.getPomId() + ".xml");

    if (pomStored) {
      session.getTransaction().commit();
    }
    else {
      session.getTransaction().rollback();
    }
    session.close();
  }
  //</editor-fold>

  //<editor-fold desc="Machine stuff">
  public List<Machine> getMachineList ()
  {
    return Machine.getMachineList();
  }

  public void toggleAvailable (Machine machine)
  {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    try {
      machine.setAvailable(!machine.isAvailable());
      session.update(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
  }

  public void toggleStatus (Machine machine)
  {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    try {
      if (machine.isInProgress()) {
        machine.setStatus(Machine.STATE.idle.toString());
      } else {
        machine.setStatus(Machine.STATE.running.toString());
      }
      session.update(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
  }
  
  public void editMachine (Machine m)
  {
    machineId = m.getMachineId();
    machineName = m.getMachineName();
    machineUrl = m.getMachineUrl();
    machineViz = m.getVizUrl();
  }
  
  public void saveMachine()
  {
    machineUrl = machineUrl.replace("https://", "").replace("http://", "");
    machineViz = machineViz.replace("https://", "").replace("http://", "");

    if (machineName.isEmpty() || machineUrl.isEmpty() || machineViz.isEmpty()) {
      String msg = "Error: machine not saved, some fields were empty!";
      FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
      FacesContext.getCurrentInstance().addMessage("saveMachine", fm);
  	  return;
  	}

    // Make sure we get a new list of IPs
    Cache.machineIPs = null;
    Cache.vizIPs = null;

    // It's a new machine
    if (machineId == -1) {
      addMachine();
    } else {
      editMachine();
    }
  }

  public void addMachine ()
  {
    Machine machine = new Machine();
    machine.setMachineName(machineName);
    machine.setMachineUrl(machineUrl);
    machine.setVizUrl(machineViz);
    machine.setStatus(Machine.STATE.idle.toString());
    machine.setAvailable(false);

    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.save(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();

      String msg = "Error : machine not added " + e.getMessage();
      FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
      FacesContext.getCurrentInstance().addMessage("saveMachine", fm);
    }
    if (transaction.wasCommitted()) {
      resetMachineData();
      log.info("Added new machine " + machine.getMachineId());
    }

    session.close();
  }

  public void editMachine ()
  {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    try {
      Machine machine = (Machine) session.get(Machine.class, machineId);
      machine.setMachineName(machineName);
      machine.setMachineUrl(machineUrl);
      machine.setVizUrl(machineViz);

      resetMachineData();
      log.info("Edited machine " + machine.getMachineId());
      session.update(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();

      String msg = "Error : machine not edited " + e.getMessage();
      FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
      FacesContext.getCurrentInstance().addMessage("saveMachine", fm);
    }
    session.close();
  }

  public void deleteMachine (Machine machine)
  {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    try {
      log.info("Deleting machine " + machine.getMachineId());
      session.delete(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();

      String msg = "Error : machine not deleted " + e.getMessage();
      FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
      FacesContext.getCurrentInstance().addMessage("saveMachine", fm);
    }
    session.close();
    resetMachineData();
  }

  private void resetMachineData() {
    machineId = -1;
    machineViz = "";
    machineName = "";
    machineUrl = "";
  }

  public String getLogins (String machineName)
  {
    try {
      long login =
          (System.currentTimeMillis() - Cache.vizLogins.get(machineName))/1000;
      if (login > 900) {
        Cache.vizLogins.remove(machineName);
      } else {
        return String.valueOf(login);
      }
    }
    catch (Exception ignored) {}

    return "";
  }
  //</editor-fold>

  public List<User> getUserList()
  {
    return User.getUserList();
  }

  public void refresh ()
  {
  }

  //<editor-fold desc="Setters and Getters">
  public String getLocationName()
  {
    return locationName;
  }
  public void setLocationName(String locationName)
  {
    this.locationName = locationName;
  }

  public int getLocationTimezone() {
    return locationTimezone;
  }
  public void setLocationTimezone(int locationTimezone) {
    this.locationTimezone = locationTimezone;
  }

  public Date getLocationStartTime ()
  {
    return locationStartTime;
  }
  public void setLocationStartTime (Date locationStartTime)
  {
    this.locationStartTime = locationStartTime;
  }

  public Date getLocationEndTime ()
  {
    return locationEndTime;
  }
  public void setLocationEndTime (Date locationEndTime)
  {
    this.locationEndTime = locationEndTime;
  }

  public String getPomName ()
  {
    return pomName;
  }
  public void setPomName (String pomName)
  {
    this.pomName = pomName.trim();
  }

  public UploadedFile getUploadedPom ()
  {
    return uploadedPom;
  }
  public void setUploadedPom (UploadedFile uploadedPom)
  {
    this.uploadedPom = uploadedPom;
  }

  public int getMachineId ()
  {
    return machineId;
  }
  public void setMachineId(int machineId) {
    this.machineId = machineId;
  }

  public String getMachineName ()
  {
    return machineName;
  }
  public void setMachineName (String machineName)
  {
    this.machineName = machineName;
  }

  public String getMachineUrl ()
  {
    return machineUrl;
  }
  public void setMachineUrl (String machineUrl)
  {
    this.machineUrl = machineUrl;
  }

  public String getMachineViz ()
  {
    return machineViz;
  }
  public void setMachineViz (String machineViz)
  {
    this.machineViz = machineViz;
  }
  //</editor-fold>

  //<editor-fold desc="Sorting setters and getters">
  public boolean isSortAscendingPom ()
  {
    return sortAscendingPom;
  }
  public void setSortAscendingPom (boolean sortAscendingPom)
  {
    this.sortAscendingPom = sortAscendingPom;
  }

  public String getSortColumnPom()
  {
    return sortColumnPom;
  }
  public void setSortColumnPom(String sortColumnPom)
  {
    this.sortColumnPom = sortColumnPom;
  }

  public String getSortColumnMachine ()
  {
    return sortColumnMachine;
  }
  public void setSortColumnMachine (String sortColumnMachine)
  {
    this.sortColumnMachine = sortColumnMachine;
  }

  public boolean isSortAscendingMachine ()
  {
    return sortAscendingMachine;
  }
  public void setSortAscendingMachine (boolean sortAscendingMachine)
  {
    this.sortAscendingMachine = sortAscendingMachine;
  }

  public String getSortColumnUsers ()
  {
    return sortColumnUsers;
  }
  public void setSortColumnUsers (String sortColumnUsers)
  {
    this.sortColumnUsers = sortColumnUsers;
  }

  public boolean isSortAscendingUsers ()
  {
    return sortAscendingUsers;
  }
  public void setSortAscendingUsers (boolean sortAscendingUsers)
  {
    this.sortAscendingUsers = sortAscendingUsers;
  }

  public Integer getSelectedTournament() {
    return selectedTournament;
  }
  public void setSelectedTournament(Integer selectedTournament) {
    this.selectedTournament = selectedTournament;
  }
  //</editor-fold>
}
