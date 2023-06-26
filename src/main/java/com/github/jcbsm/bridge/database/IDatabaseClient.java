package com.github.jcbsm.bridge.database;

import java.util.UUID;

public interface IDatabaseClient {

    /**
     * Returns the Minecraft UUID currently associated with the provided Discord user ID.
     * @param userId Discord user ID to search for
     * @return The minecraft UUID currently stored.
     */
    UUID getCurrentUUID(String userId);

    /**
     * Returns the Minecraft Username currently associate with the provided discord user ID
     * @param userId Discord user Id to search for
     * @return The Minecraft Username stored.
     */
    String getCurrentUsername(String userId);

    /**
     * Sets the username for the Discord user.
     * @param userId Discord User's ID
     * @param username The UUID to set.
     */
    void setUsername(String userId, String username);
}
