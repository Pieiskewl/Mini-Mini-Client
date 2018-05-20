package cc.hyperium.mixins.client.multiplayer;

import cc.hyperium.handlers.handlers.chat.GeneralChatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.List;

@Mixin(ServerList.class)
public class MixinServerList {
    @Shadow
    @Final
    private static Logger logger;
    @Shadow
    @Final
    private List<ServerData> servers;
    @Shadow
    @Final
    private Minecraft mc;

    @Overwrite
    public static void func_147414_b(ServerData p_147414_0_) {
        ServerList serverlist = new ServerList(Minecraft.getMinecraft());
        serverlist.loadServerList();

        for (int i = 0; i < serverlist.countServers(); ++i) {
            ServerData serverdata = serverlist.getServerData(i);

            if (serverdata.serverName.equals(p_147414_0_.serverName) && serverdata.serverIP.equals(p_147414_0_.serverIP)) {
                serverlist.func_147413_a(i, p_147414_0_);
                break;
            }
        }

        serverlist.saveServerList();
    }

    @Overwrite
    public void loadServerList() {
        try {
            this.servers.clear();
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));

            if (nbttagcompound == null) {
                return;
            }

            NBTTagList nbttaglist = nbttagcompound.getTagList("servers", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i)));
            }
        } catch (Exception exception) {
            logger.error((String) "Couldn\'t load server list", (Throwable) exception);
            GeneralChatHandler.instance().sendMessage("Load server list error");

        }
    }

    @Overwrite
    public void saveServerList() {
        try {
            NBTTagList nbttaglist = new NBTTagList();

            for (ServerData serverdata : this.servers) {
                nbttaglist.appendTag(serverdata.getNBTCompound());
            }

            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("servers", nbttaglist);
            CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.mcDataDir, "servers.dat"));
        } catch (Exception exception) {
            logger.error((String) "Couldn\'t save server list", (Throwable) exception);
            GeneralChatHandler.instance().sendMessage("Save server list error");
        }

    }

    @Overwrite
    public ServerData getServerData(int p_78850_1_) {
        try {
            return (ServerData) this.servers.get(p_78850_1_);
        } catch (Exception e) {
            GeneralChatHandler.instance().sendMessage("GetServer Data error 1");
            e.printStackTrace();
        }
        return null;
    }

    @Overwrite
    public void removeServerData(int p_78851_1_) {
        try {
            this.servers.remove(p_78851_1_);
        } catch (Exception e) {
            GeneralChatHandler.instance().sendMessage("Remove server data error");
        }
    }

    @Overwrite
    public void addServerData(ServerData p_78849_1_) {
        try {
            this.servers.add(p_78849_1_);
        } catch (Exception e) {
            GeneralChatHandler.instance().sendMessage("Add server data error");

        }
    }

    @Overwrite
    public int countServers() {
        return this.servers.size();
    }

    @Overwrite
    public void swapServers(int p_78857_1_, int p_78857_2_) {
        try {
            ServerData serverdata = this.getServerData(p_78857_1_);
            this.servers.set(p_78857_1_, this.getServerData(p_78857_2_));
            this.servers.set(p_78857_2_, serverdata);
            this.saveServerList();
        } catch (Exception e) {
            GeneralChatHandler.instance().sendMessage("Swap servers error");

        }
    }

    @Overwrite
    public void func_147413_a(int p_147413_1_, ServerData p_147413_2_) {
        try {
            this.servers.set(p_147413_1_, p_147413_2_);
        } catch (Exception e) {
            GeneralChatHandler.instance().sendMessage("func_147413_a server data error");
        }
    }
}
