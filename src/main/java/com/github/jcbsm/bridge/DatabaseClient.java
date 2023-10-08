package com.github.jcbsm.bridge;

import com.github.jcbsm.bridge.exceptions.DatabaseNoResultException;
import com.github.jcbsm.bridge.util.MojangRequest;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseClient {

    private static DatabaseClient client = new DatabaseClient();
    private final Logger logger = LoggerFactory.getLogger(DatabaseClient.class.getSimpleName());
    private final Bridge plugin;
    private DatabaseClient(){
        this.plugin = Bridge.getPlugin();

        try{
            Connection conn = this.connect();
            this.logger.info("Connected to the database.");
            this.createTables();

            conn.close();
        }
        catch (SQLException e){ this.logger.warn("SQL error: " + e.getMessage()); }
    }


    public static DatabaseClient getDatabase(){
        return client;
    }

    private Connection connect() throws SQLException {
        String connString = String.format("jdbc:sqlite:%s\\database.db", plugin.getDataFolder().getPath());
        return DriverManager.getConnection(connString);
    }


    private void createTables() {

        logger.info("Initialising DB...");

        String table = """
                CREATE TABLE IF NOT EXISTS `LinkedAccounts` (
                
                    `LinkID` INTEGER PRIMARY KEY AUTOINCREMENT,
                    `DiscordUserID` BIGINT NOT NULL,
                    `MinecraftUUID` CHAR(32) NOT NULL,
                
                    CONSTRAINT `UQ_MinecraftUUID` UNIQUE (`MinecraftUUID`)
                )""";

        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            stmt.execute(table);
            logger.info("Tables successfully initialised.");
        }
        catch (SQLException e) {
            e.printStackTrace();
            logger.warn("Unable to create table.");
        }
    }

    public void linkAccount(User user, String uuid) throws SQLException {

        String sql = "INSERT INTO `LinkedAccounts` (`DiscordUserID`, `MinecraftUUID`) VALUES (?, ?);";

        // open connection
        try (Connection conn = this.connect()) {

            // Prepare sql
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Set values
            stmt.setLong(1, user.getIdLong());
            stmt.setString(2, uuid);

            // Execute & close
            stmt.executeUpdate(); // Used when running non-returning statement
            stmt.close();

        } catch (SQLException e) {
            logger.warn(e.getMessage());
            throw e;
        }
    }

    public void unlinkAccount(String uuid) throws SQLException {

        String sql = "DELETE FROM LinkedAccounts WHERE MinecraftUUID = ?";

        try(Connection conn = this.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, uuid);

            stmt.executeUpdate();
        }
        catch (SQLException e){
            this.logger.warn("Error: " + e.getMessage());
            throw e;
        }
    }

    @Nullable
    public Long getLinked(String uuid) throws SQLException {
        String sql = "SELECT `DiscordUserID` FROM LinkedAccounts WHERE MinecraftUUID = ?;";

        try (Connection conn = this.connect()) {

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, uuid);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

            return null;

        } catch (SQLException e) {
            throw e;
        }
    }

    public int countLinkedAccounts(User user) throws SQLException {

        String sql = "SELECT COUNT(*) FROM LinkedAccounts WHERE `DiscordUserID` = ?;";

        try (Connection conn = this.connect()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(1, user.getIdLong());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }

        catch (SQLException e){
            throw e;
        }
    }

    /**
     * Get a map of all linked accounts for a specific discord user
     * @param user The user to search for
     * @return Map of UUID - Username for each account linked.
     * @throws SQLException
     * @throws IOException
     */
    public Map<String, String> getLinkedAccounts(User user) throws SQLException, IOException {

        Map<String, String> accounts = new HashMap<String, String>();

        String sql = "SELECT * FROM LinkedAccounts WHERE `DiscordUserID` = ?;";

        try (Connection conn = this.connect()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(1, user.getIdLong());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                String uuid = rs.getString("MinecraftUUID");

                String username = MojangRequest.uuidToUsername(uuid);

                if (username == null) continue;

                accounts.put(rs.getString("MinecraftUUID"), username);
            }

            return accounts;
        }

        catch (SQLException e){
            throw e;
        }

    }

    /**
     * Returns the Minecraft UUID currently associated with the provided Discord user ID.
     * @param user JDA user object - represents the user who invoked the command.
     * @return The minecraft UUID currently stored.
     */
    public UUID getCurrentUUID(User user) throws Exception {
        String sql = "SELECT minecraft_uuid FROM minecraft WHERE member_id = ?";
        UUID uuid;

        try (Connection conn = this.connect()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, user.getIdLong());

            if (stmt.execute()){
                ResultSet res = stmt.executeQuery(sql);
                uuid = UUID.fromString(res.getString("member_uuid"));
            }
            else{
                throw new DatabaseNoResultException("member_uuid", String.valueOf(user.getIdLong()));
            }
        }

        catch (SQLException e){
            logger.warn("Error");
            throw e;
        }

        return uuid;
    }


    // TODO: sanamorii - Literal duplicate code of getCurrentUUID, find a better way to do this.
    /**
     * Returns the Minecraft Username currently associate with the provided discord user ID
     * @param user Discord user Id to search for
     * @return The Minecraft Username stored.
     */
    public String getCurrentUsername(User user) throws Exception{
        String sql = "SELECT minecraft_name FROM minecraft WHERE member_id = ?";
        String username;

        try (Connection conn = this.connect()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, user.getIdLong());

            if (stmt.execute()){
                ResultSet res = stmt.executeQuery(sql);
                username = res.getString("member_name");
            }
            else{
                throw new DatabaseNoResultException("member_name", String.valueOf(user.getIdLong()));
            }
        }

        catch (SQLException e){
            logger.warn("Error");
            throw e;
        }

        return username;
    }

    /**
     * Sets the username for the Discord user.
     * @param user JDA user object - representative of the command invoker.
     * @throws Exception lol
     */
    public void setDiscordName(User user) throws Exception{
        String sql = "UPDATE members SET member_name = ? WHERE member_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getGlobalName());
            stmt.setLong(2, user.getIdLong());

            stmt.executeUpdate();
        }

        catch (SQLException e){
            this.logger.warn("Error: " + e.getMessage());
            throw e;
        }
    }

}
