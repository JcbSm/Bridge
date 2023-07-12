package com.github.jcbsm.bridge;

import com.github.jcbsm.bridge.exceptions.DatabaseNoResultException;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
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

    private Connection connect() throws SQLException{
        String connString = String.format("jdbc:sqlite:%s\\database.db", plugin.getDataFolder().getPath());
        return DriverManager.getConnection(connString);
    }


    private void createTables(){
        String membersTable = """
                CREATE TABLE IF NOT EXISTS "members" (
                \t"member_id"\tINTEGER NOT NULL UNIQUE,
                \t"member_name"\tTEXT NOT NULL,
                \t"member_linked"\tINTEGER NOT NULL DEFAULT 0,
                \tPRIMARY KEY("member_id")
                )""";
        String minecraftTable = """
                CREATE TABLE IF NOT EXISTS "minecraft" (
                \t"minecraft_uuid"\tTEXT NOT NULL UNIQUE,
                \t"minecraft_name"\tTEXT NOT NULL UNIQUE,
                \t"member_id"\tINTEGER NOT NULL,
                \tPRIMARY KEY("minecraft_uuid"),
                \tFOREIGN KEY("member_id") REFERENCES "members"("member_id") ON UPDATE CASCADE ON DELETE CASCADE
                )""";

        try (Connection conn = this.connect()){
            Statement stmt = conn.createStatement();
            stmt.execute(membersTable);
            stmt.execute(minecraftTable);
        }
        catch (SQLException e){
            logger.warn("error");
        }
    }


    /**
     * Add corresponding member to the guild's database.
     * To be used with GuildMemberJoinEvent event
     * @param user JDA user object
     * @throws SQLException where connection/writing to the database results in some error.
     */
    public void addMember(User user) throws SQLException{
        String sql = "INSERT INTO members (`member_id`, `member_name`) VALUES (?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, user.getIdLong());
            stmt.setString(2, user.getEffectiveName());

            stmt.executeUpdate();
        }
        catch (SQLException e){
            this.logger.warn("Error: " + e.getMessage());
            throw e;
        }
    }


    public void linkMember(User user, String uuid) throws SQLException{
        String sql = "INSERT INTO minecraft (`minecraft_uuid`, `minecraft_name`, `member_id`) VALUES (?, ?, ?);" +
                     "UPDATE members SET member_linked = 1 WHERE member_id = ?;";

        // Add user to database if not already.
        if (!(this.memberExists(user.getIdLong()))){ this.addMember(user); }

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, uuid);
            stmt.setInt(2, 1);  // register as linked
            stmt.setLong(3, user.getIdLong());
            stmt.setLong(4, user.getIdLong());

            stmt.executeUpdate();
        }

        catch (SQLException e){
            this.logger.warn("Error: " + e.getMessage());
            throw e;
        }
    }


    public boolean memberExists(long userID) throws SQLException{
        String sql = "SELECT (count(*) > 0) FROM members WHERE member_id = ?";
        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, userID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getBoolean(1);
            }else{
                return false;
            }
        }
        catch (SQLException e){
            this.logger.warn("Error: " + e.getMessage());
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
