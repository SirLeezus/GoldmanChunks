package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Blocked extends SubCommand {

    @Override
    public String getName() {
        return "blocked";
    }

    @Override
    public String getDescription() {
        return "List of players blocked from entering or teleporting to your claimed chunks.";
    }

    @Override
    public String getSyntax() {
        return "/chunk blocked";
    }

    @Override
    public String getPermission() {
        return "chunk.command.blocked";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();


        int index;
        int maxDisplayed = 10;
        int page = 0;

        //page check
        if (args.length > 1) {
            if (BukkitUtils.containOnlyNumbers(args[1])) {
                page = Integer.parseInt(args[1]);
            } else {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_LIST_PAGE_NOT_NUMBER.getComponent(new String[]{ args[0] } )));
                return;
            }
        }

        if (page < 0) return;
        int position = page * maxDisplayed + 1;

        UUID uuid = player.getUniqueId();
        List<String> blocked = cacheManager.getBlockedNames(uuid);
        if (!blocked.isEmpty()) {
            List<Component> lines = new ArrayList<>();

            lines.add(Lang.COMMAND_BLOCKED_HEADER.getComponent(null));
            lines.add(Component.text(""));

            for (int i = 0; i < maxDisplayed; i++) {
                index = maxDisplayed * page + i;
                if (index >= blocked.size()) break;
                if (blocked.get(index) != null) {
                    String blockedName = blocked.get(index);
                    lines.add(Lang.COMMAND_BLOCKED_LINE.getComponent(new String[] { String.valueOf(position), blockedName }));
                    position++;
                }
            }

            if (lines.size() <= 2) return;

            lines.add(Component.text(""));
            Component next = Lang.NEXT_PAGE_TEXT.getComponent(null).hoverEvent(Lang.NEXT_PAGE_HOVER.getComponent(null)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk blocked " + (page + 1)));
            Component split = Lang.PAGE_SPACER.getComponent(null);
            Component prev = Lang.PREVIOUS_PAGE_TEXT.getComponent(null).hoverEvent(Lang.PREVIOUS_PAGE_HOVER.getComponent(null)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk blocked " + (page - 1)));
            lines.add(prev.append(split).append(next));
            for (Component message : lines) player.sendMessage(message);
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BLOCKED_NONE_BLOCKED.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
