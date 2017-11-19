package io.rixa.bot.guild.manager;

import gnu.trove.map.TByteByteMap;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.data.storage.enums.Statements;
import io.rixa.bot.guild.RixaGuild;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class GuildManager {

    private Map<String, RixaGuild> rixaGuildMap = new HashMap<>();

    private GuildManager() {
        instance = this;
    }
    private static GuildManager instance;

    public static GuildManager getInstance() {
        return (instance == null) ? new GuildManager() : instance;
    }

    public RixaGuild getGuild(Guild guild) {
        if (!hasGuild(guild.getId())) {
            return addGuild(guild);
        }
        return rixaGuildMap.get(guild.getId());
    }

    public RixaGuild getGuild(String id) {
        return rixaGuildMap.get(id);
    }

    public boolean hasGuild(String id) {
        return rixaGuildMap.containsKey(id);
    }

    public RixaGuild addGuild(Guild guild) {
        if (!(DatabaseAdapter.getInstance().exists("core", "guild_id", guild.getId()))) {
            insert(guild);
        }
        RixaGuild rixaGuild = new RixaGuild(guild);
        rixaGuildMap.put(guild.getId(), rixaGuild);
        DatabaseAdapter.getInstance().get().queryForObject(
                Statements.SELECT_CORE.getStatement(), new Object[] { guild.getId() }, new GuildMapper());
        return rixaGuild;
    }

    private void insert(Guild guild) {
        DatabaseAdapter.getInstance().get().update
                (Statements.INSERT_CORE.getStatement(),
                        guild.getId(), guild.getName(), "Description not set.", "No Keywords Found ");
    }

    public Map<String, RixaGuild> getGuilds() {
        return rixaGuildMap;
    }
}