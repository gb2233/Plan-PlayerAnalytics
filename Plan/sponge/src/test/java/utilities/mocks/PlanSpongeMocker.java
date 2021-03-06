/*
 * License is provided in the jar as LICENSE also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/LICENSE
 */
package utilities.mocks;

import com.djrapitops.plan.PlanSponge;
import com.djrapitops.plugin.benchmarking.Timings;
import com.djrapitops.plugin.command.ColorScheme;
import com.djrapitops.plugin.logging.console.PluginLogger;
import com.djrapitops.plugin.logging.console.TestPluginLogger;
import com.djrapitops.plugin.logging.debug.CombineDebugLogger;
import com.djrapitops.plugin.logging.debug.DebugLogger;
import com.djrapitops.plugin.logging.debug.MemoryDebugLogger;
import com.djrapitops.plugin.logging.error.ConsoleErrorLogger;
import com.djrapitops.plugin.logging.error.ErrorHandler;
import com.djrapitops.plugin.task.thread.ThreadRunnableFactory;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Mocking Utility for Sponge version of Plan.
 *
 * @author Rsl1122
 */
public class PlanSpongeMocker extends Mocker {

    private PlanSponge planMock;

    private PlanSpongeMocker() {
    }

    public static PlanSpongeMocker setUp() {
        return new PlanSpongeMocker().mockPlugin();
    }

    private PlanSpongeMocker mockPlugin() {
        planMock = Mockito.mock(PlanSponge.class);
        super.planMock = planMock;

        doReturn(new ColorScheme("§1", "§2", "§3")).when(planMock).getColorScheme();
        doReturn("1.0.0").when(planMock).getVersion();

        Logger logger = Mockito.mock(Logger.class);
        ThreadRunnableFactory runnableFactory = new ThreadRunnableFactory();
        PluginLogger testPluginLogger = new TestPluginLogger();
        DebugLogger debugLogger = new CombineDebugLogger(new MemoryDebugLogger());
        ErrorHandler consoleErrorLogger = new ConsoleErrorLogger(testPluginLogger);
        Timings timings = new Timings(debugLogger);

        doReturn(logger).when(planMock).getLogger();
        doReturn(runnableFactory).when(planMock).getRunnableFactory();
        doReturn(testPluginLogger).when(planMock).getPluginLogger();
        doReturn(debugLogger).when(planMock).getDebugLogger();
        doReturn(consoleErrorLogger).when(planMock).getErrorHandler();
        doReturn(timings).when(planMock).getTimings();

        return this;
    }

    public PlanSpongeMocker withDataFolder(File tempFolder) {
        when(planMock.getDataFolder()).thenReturn(tempFolder);
        return this;
    }

    public PlanSpongeMocker withGame() {
        Game game = Mockito.mock(Game.class);

        Platform platform = mockPlatform();
        Server server = mockServer();
        doReturn(platform).when(game).getPlatform();
        doReturn(server).when(game).getServer();

        doReturn(game).when(planMock).getGame();
        return this;
    }

    private Platform mockPlatform() {
        Platform platform = Mockito.mock(Platform.class);

        MinecraftVersion version = Mockito.mock(MinecraftVersion.class);
        doReturn("1.12").when(version).getName();
        doReturn(version).when(platform).getMinecraftVersion();

        return platform;
    }

    private Server mockServer() {
        Server server = Mockito.mock(Server.class);

        Text motd = Mockito.mock(Text.class);
        doReturn("Motd").when(motd).toPlain();
        Optional<InetSocketAddress> ip = Optional.of(new InetSocketAddress(25565));
        int maxPlayers = 20;
        List<Player> online = new ArrayList<>();

        doReturn(motd).when(server).getMotd();
        doReturn(ip).when(server).getBoundAddress();
        doReturn(maxPlayers).when(server).getMaxPlayers();
        doReturn(online).when(server).getOnlinePlayers();

        return server;
    }

    public PlanSpongeMocker withResourceFetchingFromJar() throws Exception {
        withPluginFiles();
        return this;
    }

    public PlanSponge getPlanMock() {
        return planMock;
    }
}
