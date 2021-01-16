package io.github.metr05.economicsbase;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;


@Plugin(
        id = "economicsbase",
        name = "EconomicsBase",
        version = "1.0-SNAPSHOT",
        description = "A plugin is designed to attach sponge server,whose function is aim on system building,and give an open environment for next stage development.",
        url = "https://hemu.blog",
        authors = {
                "Metr05"
        }
)
public class EconomicsBase {
    PluginManager pluginManager = Sponge.getPluginManager();
    PluginContainer plugin = pluginManager.getPlugin("economicsbase").orElse(null);
    Task.Builder taskBuilder = Task.builder();
    File file = new File("economicsbase.conf");
    Path potentialFile = file.toPath();
    ConfigurationLoader<CommentedConfigurationNode> loader =
            HoconConfigurationLoader.builder().setPath(potentialFile).build();
    ConfigurationNode rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
    @Inject
    private Logger logger;

    @Listener
    public void onServerLoading(GamePostInitializationEvent event){
        try {
            rootNode = loader.load();
        } catch(IOException e) {
            // handle error
        }
    }

    @Listener
    //服务端加载完毕Event
    public void onServerStart(GameStartedServerEvent event) {
        //加载提醒信息
        logger.info("§aAnarchismEconomicsBase §aversion1.0.0");//**Anarchism**Economics(red)Base(blue) version1.0.0(gold)
        String socketserver = rootNode.getNode("root","parent","socketserver","host").getString();
        logger.info("Middleware开启"+socketserver+"监听");
        int contract_port = rootNode.getNode("root","parent","socketserver","contract","port").getInt();
        int economy_port = rootNode.getNode("root","parent","socketserver","economy","port").getInt();
        logger.info("§4契约系统端口: "+contract_port+" "+"§e经济系统端口: "+economy_port);
        taskBuilder.execute(()->{
            try {
                run(contract_port);
            } catch (Exception e) {
                logger.info(String.valueOf(e));
            }}
        ).async().name("Contract_Thread").submit(plugin);
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event){
        try {
            loader.save(rootNode);
        } catch(IOException e) {
            // handle error
        }
    }
    private AdminMessageChannel adminChannel = new AdminMessageChannel();

    @Listener
    public void onClientConnectionJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if(player.hasPermission("economicsbase.admin")) {
            MessageChannel originalChannel = event.getOriginalChannel();
            MessageChannel newChannel = MessageChannel.combined(originalChannel,
                    adminChannel);
            player.setMessageChannel(newChannel);
        }
        logger.info(player.getName());
    }
    public void run(int port) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 监听指定的端口
        ServerSocket server = new ServerSocket(port);
        logger.info("监听中...");
        while(true) {
            Socket socket = server.accept();
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                sb.append(new String(bytes, 0, len, "UTF-8"));
            }
            //logger.info("§4[Client]§l" + sb);
            inputStream.close();
            socket.close();
            if(sb.toString().equals("f24f62eeb789199b9b2e467df3b1876b")){//"exit"的md5
                logger.warn("监听已停止");
                break;
            }
            MessageChannel PublicChannel = MessageChannel.TO_PLAYERS;
            PublicChannel.send(Text.builder(sb.toString()).color(TextColors.GOLD).build());
            sb = new StringBuilder();
        }
        server.close();
    }
}


