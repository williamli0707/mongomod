package com.github.williamli0707.mod;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ConnectionString;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.ReplaceOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.bson.Document;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

@Mod(MongoMod.MODID)
public class MongoMod {
    public static final String MODID = "mongomod";

	private static final Logger LOGGER = LogUtils.getLogger();
	// Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	// Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	private final FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().upsert(true);
	private final MongoCollection joinLogs, startLogs, onlineLogs;


	public MongoMod() throws IOException {
//		Scanner in = new Scanner(new File("Config.txt"));
		MongoClient mongoClient = MongoClients.create("mongodb+srv://mongomod:<>@cluster0.hix0z.mongodb.net/?retryWrites=true&w=majority");
		MongoDatabase db = mongoClient.getDatabase("lermit4");
		joinLogs = db.getCollection("joinlogs");
		startLogs = db.getCollection("startlogs");
		onlineLogs = db.getCollection("onlinelogs");

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		//Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		// Register the Deferred Register to the mod event bus so blocks get registered
		BLOCKS.register(modEventBus);
		// Register the Deferred Register to the mod event bus so items get registered
		ITEMS.register(modEventBus);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		//getting the most recent player log:
		//Document myDoc = collection.find().sort(new Document("_id", -1)).first();
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Some common setup code
		LOGGER.info("HELLO FROM COMMON SETUP");
		LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event){
		if(ServerLifecycleHooks.getCurrentServer().getTickCount() % 200 == 0){
			onlineLogs.findOneAndReplace(eq("_id", 1), new PlayerLog(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()).toDocument(), options);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		LOGGER.info("PlayerLoggedInEvent " + event.getEntity().getName());
		if(!(event.getEntity() instanceof Player)) return;
		joinLogs.insertOne(new PlayerEvent("join", ((Player) event.getEntity()).getGameProfile().getName()).toDocument());
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event){
		LOGGER.info("PlayerLoggedOutEvent " + event.getEntity().getName());
		if(!(event.getEntity() instanceof Player)) return;
		joinLogs.insertOne(new PlayerEvent("leave", ((Player) event.getEntity()).getGameProfile().getName()).toDocument());
	}

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event){
//		LOGGER.info("EntityJoinWorldEvent " + event.getEntity().getName());
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		// Do something when the server starts
		LOGGER.info("HELLO from server starting");
		startLogs.insertOne(new ServerEvent("start").toDocument());

	}

	@SubscribeEvent
	public void onServerStopping(ServerStoppingEvent event) {
		// Do something when the server stops
		LOGGER.info("HELLO from server stopping");
		startLogs.insertOne(new ServerEvent("stop").toDocument());
	}
}
