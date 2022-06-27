package net.ragnaroknetwork.rewardsgui.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerInventory {
    private final Database database;
    private final UUID uuid;
    private Map<String, Integer> inventory;

    public PlayerInventory(Database database, UUID uuid, Map<String, Integer> inventory) {
        this.database = database;
        this.uuid = uuid;
        if (inventory == null) loadInventory();
        else this.inventory = inventory;
    }

    public void loadInventory() {
        this.inventory = new HashMap<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT inventory FROM rewards WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            try (final ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String playerInventory = resultSet.getString("inventory");
                    if (playerInventory.equals("-"))
                        return;

                    String[] inventories = playerInventory.split(";");

                    for (String inventory : inventories) {
                        String[] split = inventory.split("-");
                        int amount = Integer.parseInt(split[1]);
                        this.inventory.put(split[0], amount);
                    }
                    return;
                }
            }

            try (
                    Connection conn = database.getConnection();
                    PreparedStatement insertStatement = conn
                            .prepareStatement("INSERT INTO rewards(uuid) VALUES(?)")
            ) {
                insertStatement.setString(1, uuid.toString());
                insertStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRewards(String id, int amount) {
        inventory.put(id, inventory.getOrDefault(id, 0) + amount);
        writeInventory();
    }

    public void removeReward(String id) {
        addRewards(id, -1);
    }

    public List<String> getInventoryRewards() {
        return new ArrayList<>(inventory.keySet());
    }

    public int getRewards(String id) {
        return inventory.getOrDefault(id, 0);
    }

    public void writeInventory() {
        try (
                Connection connection = database.getConnection();
                final PreparedStatement preparedStatement = connection
                        .prepareStatement("UPDATE rewards SET inventory = ? WHERE guild_id = ?")
        ) {
            String value;
            if (inventory.isEmpty()) value = "-";
            else {
                value = inventory.entrySet().stream()
                        .map(it -> it.getKey() + "-" + it.getValue())
                        .collect(Collectors.joining(";"));
            }

            preparedStatement.setString(1, value);
            preparedStatement.setString(2, uuid.toString());

            preparedStatement.executeUpdate();

            database.updateCache(uuid, inventory);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
