package Project.Common;

public class User {
    private long clientId = Constants.DEFAULT_CLIENT_ID;
    private String clientName;
    private boolean isReady = false;
    private boolean tookTurn = false;
    private int points=0;
    private boolean isDrawer=false;

    /**
     * @return the clientId
     */
    public long getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the username
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * @param username the username to set
     */
    public void setClientName(String username) {
        this.clientName = username;
    }

    public String getDisplayName() {
        return String.format("%s#%s", this.clientName, this.clientId);
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public void reset() {
        this.clientId = Constants.DEFAULT_CLIENT_ID;
        this.clientName = null;
        this.isReady = false;
        this.tookTurn = false;
    }

    /**
     * @return the tookTurn
     */
    public boolean didTakeTurn() {
        return tookTurn;
    }

    /**
     * @param tookTurn the tookTurn to set
     */
    public void setTookTurn(boolean tookTurn) {
        this.tookTurn = tookTurn;
    }

    /**
     * @return the points
     */
    public int getClientPoints(){
        return points;
    }
    /**
     * @param addedPoints the points to add
     */
     public void setClientPoints(int addedPoints){
        this.points+=addedPoints;
    }

    //sets drawer status and checks if drawer of that round
    /**
     * @return drawer status
     */
    public boolean isDrawer(){
        return this.isDrawer;
    }

    /**
     * @param roundDrawer true if user is selected as drawer in round otherwise false 
     */
    public void setDrawer(boolean roundDrawer){ 
        this.isDrawer = roundDrawer;
    }



    /**
     * Resets the session state for the user.
     */
    public void resetSession() {
        this.isReady = false;
        this.isDrawer = false;
        // TODO: add reset points here
    }
}