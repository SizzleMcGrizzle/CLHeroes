package me.sizzlemcgrizzle.clheroes.runnables;

import com.earth2me.essentials.IEssentials;
import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaltopCalculateRunnable extends HeroRunnable {
	private IEssentials ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
	private File folder;
	private LinkedHashMap<String, Double> top3 = new LinkedHashMap<>();
	private Map<String, Double> balances = new HashMap<>();
	private String first = null, second = null, third = null;

	@Override
	public void doFunction() {
		folder = new File(ess.getDataFolder(), "userdata");

		if (!doesFolderExist(folder) || folder.listFiles() == null)
			return;

		if (ess.getSettings().isEcoDisabled()) {
			if (ess.getSettings().isDebug()) {
				ess.getLogger().info("Internal economy functions disabled, aborting baltop.");
			}
			return;
		}

		createBalances();

		getTop3();

		top3.put(first, balances.get(first));
		top3.put(second, balances.get(second));
		top3.put(third, balances.get(third));

		if (Settings.DEBUG_MESSAGES) {
			top3.forEach((a, v) -> Common.log("UUID " + a + " has a balance of " + v));
		}

		new BaltopApplyRunnable(top3).runTask(SimplePlugin.getInstance());
	}

	@Override
	public void run() {
		doFunction();
	}

	private void getTop3() {
		for (Map.Entry<String, Double> entry : balances.entrySet()) {
			Double balance = entry.getValue();
			String uuid = entry.getKey();
			if (first == null || balance > balances.get(first)) {
				third = second;
				second = first;
				first = uuid;
				continue;
			}
			if (second == null || balance > balances.get(second)) {
				third = second;
				second = uuid;
				continue;
			}
			if (third == null || balance > balances.get(third)) {
				third = uuid;
			}
		}
	}

	private void createBalances() {
		for (File file : folder.listFiles()) {
			YamlConfiguration reader = YamlConfiguration.loadConfiguration(file);
			double balance;
			try {
				reader.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			if (!reader.getKeys(false).contains("money"))
				balance = 0.0;
			else if (reader.getString("money") == null)
				continue;
			else
				balance = Double.parseDouble(reader.getString("money"));
			String name = file.getName().replace(".yml", "");
			if (Settings.DEBUG_MESSAGES) {
				Common.log(name + " has a balance of " + balance);
			}
			balances.put(name, balance);
		}
	}

	private boolean doesFolderExist(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
