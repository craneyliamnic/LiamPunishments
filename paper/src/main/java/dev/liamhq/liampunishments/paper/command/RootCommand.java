/*
 * LiamPunishments - a modern punishment plugin for Paper, Folia and Velocity.
 * Copyright (C) 2026 Liam Nic Craney (compileOnlyLiam) <https://liamhq.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.liamhq.liampunishments.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/** The {@code /liampunishments} administration command, also available as {@code /punish}. */
public final class RootCommand {

    public static final String LABEL = "liampunishments";
    public static final String DESCRIPTION = "LiamPunishments administration";
    public static final List<String> ALIASES = List.of("punish");
    public static final String PERMISSION_INFO = "liampunishments.command.info";

    private final PluginMeta pluginMeta;

    public RootCommand(PluginMeta pluginMeta) {
        this.pluginMeta = pluginMeta;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal(LABEL)
                .requires(source -> source.getSender().hasPermission(PERMISSION_INFO))
                .executes(context -> {
                    context.getSource().getSender().sendMessage(info());
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    private Component info() {
        return Component.text()
                .append(Component.text(pluginMeta.getName(), NamedTextColor.GOLD))
                .append(Component.text(" v" + pluginMeta.getVersion(), NamedTextColor.GRAY))
                .build();
    }
}
