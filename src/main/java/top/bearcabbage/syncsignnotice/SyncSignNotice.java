package top.bearcabbage.syncsignnotice;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.SignText;
/**
 * 同步标牌通知模组主类
 */
public class SyncSignNotice implements ModInitializer {
	public static final String MOD_ID = "sync-sign-notice";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final SConfig config = new SConfig(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve("config.json"));
	private static final SConfig noticeCache = new SConfig(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve("notice_cache.json"));

	public static int maxNewsCount = 5;
	public static int refreshInterval = 300*20;

	public static int clientTick = 0;
	public static final List<SignText> newsList = new ArrayList<>();
	
	// 用于线程安全的锁对象
	public static final Object newsListLock = new Object();
	
	// 数据库配置
	private static DatabaseConfig dbConfig;

	@Override
	public void onInitialize() {

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			LOGGER.info("[SyncSignNotice] Client started, loading configuration and pulling news...");
			loadConfig();
			pullNews();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (++clientTick % refreshInterval == 0) {
				LOGGER.info("[SyncSignNotice] Auto-refreshing news...");
				pullNews();
			}
		});
	}

	/**
	 * 加载配置
	 */
	public static void loadConfig() {
		LOGGER.info("[SyncSignNotice] Mod loading...");
		
		// 加载数据库配置
		dbConfig = loadDatabaseConfig();
		
		// 加载其他配置
		loadGeneralConfig();
		
		LOGGER.info("[SyncSignNotice] Database config loaded: {}:{}", dbConfig.host, dbConfig.port);
	}

	/**
	 * 拉取新闻（异步执行）
	 */
	public static void pullNews() {
		LOGGER.info("[SyncSignNotice] Starting async news pull...");
		NewsService.pullNews(dbConfig, noticeCache, maxNewsCount)
			.thenRun(() -> {
				LOGGER.info("[SyncSignNotice] News pull completed successfully");
			})
			.exceptionally(throwable -> {
				LOGGER.error("[SyncSignNotice] News pull failed: {}", throwable.getMessage(), throwable);
				return null;
			});
	}
	
	/**
	 * 加载数据库配置
	 */
	private static DatabaseConfig loadDatabaseConfig() {
		String host = config.getOrDefault("db_host", "example.com");
		String user = config.getOrDefault("db_user", "ExampleUser");
		String password = config.getOrDefault("db_password", "ExamplePassword");
		String database = config.getOrDefault("db_database", "example_db");
		int port = config.getOrDefault("db_port", 3306);
		
		return new DatabaseConfig(host, user, password, database, port);
	}
	
	/**
	 * 加载通用配置
	 */
	private static void loadGeneralConfig() {
		maxNewsCount = config.getOrDefault("maxNewsCount", 5);
		refreshInterval = 20*config.getOrDefault("autoRefreshInterval", 300);
	}

	public static int getNewsSignNumber(SignText signText) {
		if (signText == null || signText.equals(new SignText())) {
			return 0;
		}

		Text[] messages = signText.getMessages(false);
		if (messages.length < 4 ||
				messages[0].getLiteralString() == null ||
				!messages[0].getLiteralString().equals("[Notice]") ||
				messages[3].getLiteralString() == null ||
				!messages[3].getLiteralString().equals("SyncSign") ||
				messages[1].getLiteralString() == null) {
			return 0;
		}

		try {
			return Integer.parseInt(messages[1].getLiteralString());
		} catch (NumberFormatException e) {
			LOGGER.warn("[SyncSignNotice] Failed to parse sign number: {}", e.getMessage());
			return 0;
		}
	}
}