package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.menus.ChunkManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Manage extends SubCommand {

    @Override
    public String getName() {
        return "manage";
    }

    @Override
    public String getDescription() {
        return "Opens your trusted and chunk manager menu.";
    }

    @Override
    public String getSyntax() {
        return "/chunk manage";
    }

    @Override
    public String getPermission() {
        return "chunk.command.manage";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        new ChunkManager(plugin.getData().getPlayerMU(player.getUniqueId())).open();
        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
