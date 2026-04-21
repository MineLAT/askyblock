/*******************************************************************************
 * This file is part of ASkyBlock.
 *
 *     ASkyBlock is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ASkyBlock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ASkyBlock.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package com.wasteofplastic.askyblock.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.schematics.Schematic;
import com.wasteofplastic.askyblock.util.Util;
import com.wasteofplastic.askyblock.util.VaultHelper;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

public class SchematicsPanel implements Listener {
    private ASkyBlock plugin;

    /**
     * @param plugin - ASkyBlock plugin object
     */
    public SchematicsPanel(ASkyBlock plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns a customized panel of available Schematics for the player
     * 
     * @param player
     * @return custom Inventory object or null if there are no valid schematics for this player
     */
    public Inventory getPanel(Player player) {
        // Go through the available schematics for this player
        int slot = 0;
        List<SPItem> items = new ArrayList<SPItem>();
        List<Schematic> availableSchems = plugin.getIslandCmd().getSchematics(player, false);
        // Add an info icon
        //items.add(new SPItem(Material.MAP,"Choose your island", "Pick from the selection...",slot++));
        // Generate additional available schematics
        for (Schematic schematic : availableSchems) {
            if (schematic.isVisible()) {
                items.add(new SPItem(schematic, slot++));
            }
        }
        //plugin.getLogger().info("DEBUG: there are " + items.size() + " in the panel");
        // Now create the inventory panel
        if (items.size() > 0) {
            // Make sure size is a multiple of 9
            int size = PanelHolder.INNER_SLOTS[items.size()] + 9 + 8;
            size -= (size % 9);
            Gui newPanel = new Gui(size, plugin.myLocale(player.getUniqueId()).schematicsTitle);
            // Fill the inventory and return
            for (SPItem i : items) {
                newPanel.setItem(PanelHolder.INNER_SLOTS[i.getSlot()], i);
            }
            return newPanel.getInventory();
        } else {
            Util.sendMessage(player, ChatColor.RED + plugin.myLocale().errorCommandNotReady);
        }
        return null;
    }

    /**
     * Handles when the schematics panel is actually clicked
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that
        // clicked the item
        Inventory inventory = event.getInventory(); // The inventory that was clicked in
        // Check this is the right panel
        if (!(inventory.getHolder() instanceof Gui gui)) {
            return;
        }
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot == -999) {
            player.closeInventory();
            inventory.clear();
            return;
        }
        if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
            player.closeInventory();
            inventory.clear();
            player.updateInventory();
            return;
        }
        // Get the item clicked
        SPItem item = gui.getItem(slot);
        if (item == null) {
            player.closeInventory();
            inventory.clear();
            return;
        }

        // plugin.getLogger().info("DEBUG: slot is " + slot);
        player.closeInventory(); // Closes the inventory
        inventory.clear();
        // Check cost
        if (item.getCost() > 0) {
            if (Settings.useEconomy && VaultHelper.setupEconomy() && !VaultHelper.econ.has(player, item.getCost())) {
                // Too expensive
                Util.sendMessage(player, ChatColor.RED + plugin.myLocale(player.getUniqueId()).minishopYouCannotAfford.replace("[description]", item.getName()));
            } else {
                // Do something
                if (Settings.useEconomy && VaultHelper.setupEconomy()) {
                    VaultHelper.econ.withdrawPlayer(player, item.getCost());
                }
                Util.runCommand(player, Settings.ISLANDCOMMAND + " make " + item.getHeading());
            }
        } else {
            Util.runCommand(player, Settings.ISLANDCOMMAND + " make " + item.getHeading());
        }
    }

    private static class Gui implements InventoryHolder {

        private final Inventory inventory;
        private final Map<Integer, SPItem> items = new HashMap<>();

        public Gui(int size, String title) {
            this.inventory = Bukkit.createInventory(this, size, title);
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        @Nullable
        public SPItem getItem(int slot) {
            return items.get(slot);
        }

        public void setItem(int slot, SPItem item) {
            items.put(slot, item);
            inventory.setItem(slot, item.getItem());
        }
    }
}