package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.menus.PlayerChunks;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkList extends SubCommand {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Opens your chunks claimed menu.";
    }

    @Override
    public String getSyntax() {
        return "/chunk list";
    }

    @Override
    public String getPermission() {
        return "chunk.command.list";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        new PlayerChunks(plugin.getData().getPlayerMU(player.getUniqueId())).open();
        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
