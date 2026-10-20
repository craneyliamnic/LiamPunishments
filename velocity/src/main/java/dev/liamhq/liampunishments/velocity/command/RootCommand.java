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
package dev.liamhq.liampunishments.velocity.command;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The proxy-side administration command. It uses its own label so it does not shadow the backend
 * {@code /liampunishments} command when both plugins are installed.
 */
public final class RootCommand {

    public static final String LABEL = "liampunishmentsproxy";
    public static final String ALIAS = "punishproxy";
    public static final String PERMISSION_INFO = "liampunishments.command.info";

    private final PluginContainer container;

    public RootCommand(PluginContainer container) {
        this.container = container;
    }

    public void register(CommandManager commandManager, Object plugin) {
        CommandMeta meta =
                commandManager.metaBuilder(LABEL).aliases(ALIAS).plugin(plugin).build();
        commandManager.register(meta, build());
    }

    private BrigadierCommand build() {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder(LABEL)
                .requires(source -> source.hasPermission(PERMISSION_INFO))
                .executes(context -> {
                    context.getSource().sendMessage(info());
                    return Command.SINGLE_SUCCESS;
                })
                .build());
    }

    private Component info() {
        PluginDescription description = container.getDescription();
        String name = description
                .getName()
                .orElseThrow(() -> new IllegalStateException("velocity-plugin.json is missing 'name'"));
        String version = description
                .getVersion()
                .orElseThrow(() -> new IllegalStateException("velocity-plugin.json is missing 'version'"));
        return Component.text()
                .append(Component.text(name, NamedTextColor.GOLD))
                .append(Component.text(" v" + version, NamedTextColor.GRAY))
                .build();
    }
}
