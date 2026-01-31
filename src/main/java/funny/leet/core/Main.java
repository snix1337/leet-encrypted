package funny.leet.core;

import funny.leet.api.file.DirectoryCreator;
import funny.leet.api.file.FileController;
import funny.leet.api.file.FileRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import obf.uscate.annotations.Compile;
import obf.uscate.annotations.CompileBytecode;
import obf.uscate.annotations.Initialization;
import funny.leet.api.file.exception.FileProcessingException;
import funny.leet.api.repository.box.BoxESPRepository;
import funny.leet.api.repository.rct.RCTRepository;
import funny.leet.api.repository.way.WayRepository;
import funny.leet.api.system.discord.DiscordManager;
import funny.leet.api.feature.draggable.DraggableRepository;
import funny.leet.api.repository.macro.MacroRepository;
import funny.leet.api.event.EventManager;
import funny.leet.api.feature.module.ModuleProvider;
import funny.leet.api.feature.module.ModuleRepository;
import funny.leet.api.feature.module.ModuleSwitcher;
import funny.leet.api.system.sound.SoundManager;
import funny.leet.common.util.logger.LoggerUtil;
import funny.leet.common.util.render.ScissorManager;
import funny.leet.core.client.ClientInfo;
import funny.leet.core.client.ClientInfoProvider;
import funny.leet.core.listener.ListenerRepository;
import funny.leet.implement.features.commands.CommandDispatcher;
import funny.leet.implement.features.commands.manager.CommandRepository;
import funny.leet.implement.features.modules.combat.killaura.attack.AttackPerpetrator;
import funny.leet.implement.screens.menu.MenuScreen;

import java.io.File;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Main implements ModInitializer {

    @Getter
    static Main instance;
    EventManager eventManager = new EventManager();
    ModuleRepository moduleRepository;
    ModuleSwitcher moduleSwitcher;
    CommandRepository commandRepository;
    CommandDispatcher commandDispatcher;
    BoxESPRepository boxESPRepository = new BoxESPRepository(eventManager);
    MacroRepository macroRepository = new MacroRepository(eventManager);
    WayRepository wayRepository = new WayRepository(eventManager);
    RCTRepository RCTRepository = new RCTRepository(eventManager);
    ModuleProvider moduleProvider;
    DraggableRepository draggableRepository;
    DiscordManager discordManager;
    FileRepository fileRepository;
    FileController fileController;
    ScissorManager scissorManager = new ScissorManager();
    ClientInfoProvider clientInfoProvider;
    ListenerRepository listenerRepository;
    AttackPerpetrator attackPerpetrator = new AttackPerpetrator();
    boolean initialized;

    @Override
    @CompileBytecode
    public void onInitialize() {
        instance = this;

        initClientInfoProvider();
        initModules();
        initDraggable();
        initFileManager();
        initCommands();
        initListeners();
        initDiscordRPC();
        SoundManager.init();
        MenuScreen menuScreen = new MenuScreen();
        menuScreen.initialize();

        initialized = true;
    }

    @Compile
    @Initialization
    private void initDraggable() {
        draggableRepository = new DraggableRepository();
        draggableRepository.setup();
    }

    @Compile
    @Initialization
    private void initModules() {
        moduleRepository = new ModuleRepository();
        moduleRepository.setup();
        moduleProvider = new ModuleProvider(moduleRepository.modules());
        moduleSwitcher = new ModuleSwitcher(moduleRepository.modules(), eventManager);
    }

    @Compile
    @Initialization
    private void initCommands() {
        commandRepository = new CommandRepository();
        commandDispatcher = new CommandDispatcher(eventManager);
    }


    private void initDiscordRPC() {
        discordManager = new DiscordManager();
        discordManager.init();
    }


    private void initClientInfoProvider() {
        File clientDirectory = new File(MinecraftClient.getInstance().runDirectory, "\\leet\\");
        File filesDirectory = new File(clientDirectory, "\\files\\");
        File moduleFilesDirectory = new File(filesDirectory, "\\configs\\");
        clientInfoProvider = new ClientInfo("leet: encrypted", "snix1337", "PREMIUM", clientDirectory, filesDirectory, moduleFilesDirectory);
    }

    //--SHOULD DO--//
    // Cape;
    // Discord RPC;
    // Make shader surround in World Renderer

    private void initFileManager() {
        DirectoryCreator directoryCreator = new DirectoryCreator();
        directoryCreator.createDirectories(clientInfoProvider.clientDir(), clientInfoProvider.filesDir(), clientInfoProvider.configsDir());
        fileRepository = new FileRepository();
        fileRepository.setup(this);
        fileController = new FileController(fileRepository.getClientFiles(), clientInfoProvider.filesDir(), clientInfoProvider.configsDir());
        try {
            fileController.loadFiles();
        } catch (FileProcessingException e) {
            LoggerUtil.error("Error occurred while loading files: " + e.getMessage() + " " + e.getCause());
        }
    }


    private void initListeners() {
        listenerRepository = new ListenerRepository();
        listenerRepository.setup();
    }
}
