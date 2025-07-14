package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import com.zenith.feature.player.World;
import com.zenith.feature.waypoints.Waypoint;
import com.zenith.mc.block.BlockPos;
import com.zenith.mc.dimension.DimensionData;

import java.util.ArrayList;

import static com.zenith.Globals.CONFIG;
import static com.zenith.command.brigadier.BlockPosArgument.blockPos;
import static com.zenith.command.brigadier.BlockPosArgument.getBlockPos;
import static com.zenith.command.brigadier.CustomStringArgumentType.getString;
import static com.zenith.command.brigadier.CustomStringArgumentType.wordWithChars;
import static com.zenith.command.brigadier.DimensionArgument.dimension;
import static com.zenith.command.brigadier.DimensionArgument.getDimension;

public class WaypointsCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("waypoints")
            .category(CommandCategory.INFO)
            .description("""
               Save and manage waypoints.
               
               Waypoints can be used as pathfinder goals:
               `b goto <waypointId>`
               `b click <left/right> <waypointId>`
               """)
            .usageLines(
                "add <id> <x> <y> <z>",
                "add <id> <dimension> <x> <y> <z>",
                "del <id>",
                "clear",
                "list"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("waypoints")
            .then(literal("add").then(argument("id", wordWithChars())
                .then(argument("dimension", dimension()).then(argument("pos", blockPos()).executes(c -> {
                    String id = getString(c, "id");
                    DimensionData dimension = getDimension(c, "dimension");
                    BlockPos pos = getBlockPos(c, "pos");
                    var waypoints = CONFIG.client.extra.waypoints.waypoints;
                    waypoints.removeIf(wp -> wp.id().equalsIgnoreCase(id));
                    waypoints.add(new Waypoint(id, dimension.name(), pos.x(), pos.y(), pos.z()));
                    waypoints.sort(Waypoint::compareTo);
                    c.getSource().getEmbed()
                        .title("Waypoint Added");
                })))
                .then(argument("pos", blockPos()).executes(c -> {
                    String id = getString(c, "id");
                    DimensionData dimension = World.getCurrentDimension();
                    BlockPos pos = getBlockPos(c, "pos");
                    var waypoints = CONFIG.client.extra.waypoints.waypoints;
                    waypoints.removeIf(wp -> wp.id().equalsIgnoreCase(id));
                    waypoints.add(new Waypoint(id, dimension.name(), pos.x(), pos.y(), pos.z()));
                    waypoints.sort(Waypoint::compareTo);
                    c.getSource().getEmbed()
                        .title("Waypoint Added");
                }))))
            .then(literal("del").then(argument("id", wordWithChars()).executes(c -> {
                String id = getString(c, "id");
                CONFIG.client.extra.waypoints.waypoints.removeIf(wp -> wp.id().equalsIgnoreCase(id));
                c.getSource().getEmbed()
                    .title("Waypoint Removed");
            })))
            .then(literal("clear").executes(c -> {
                CONFIG.client.extra.waypoints.waypoints.clear();
                c.getSource().getEmbed()
                    .title("Waypoints Cleared");
            }))
            .then(literal("list").executes(c -> {
                c.getSource().getEmbed()
                    .title("Waypoints List");
            }));
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .description(waypointList())
            .primaryColor();
    }

    private String waypointList() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Waypoint> waypoints = CONFIG.client.extra.waypoints.waypoints;
        if (waypoints.isEmpty()) {
            return "No waypoints.";
        }
        for (int i = 0; i < waypoints.size(); i++) {
            final Waypoint waypoint = waypoints.get(i);
            String wp;
            if (CONFIG.discord.reportCoords) {
                String wpWithCoords = "%s : %s : ||[%s, %s, %s]||\n";
                wp = String.format(wpWithCoords, waypoint.id(), waypoint.dimension(), waypoint.x(), waypoint.y(), waypoint.z());
            } else {
                String wpWithoutCoords = "%s : %s : ||[Coords disabled]||\n";
                wp = String.format(wpWithoutCoords, waypoint.id(), waypoint.dimension());
            }
            if (sb.length() + wp.length() > 4000) {
                sb.append("... and ").append(waypoints.size() - i).append("more");
                break;
            }
            sb.append(wp);
        }
        return sb.toString();
    }
}
