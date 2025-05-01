import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import burp.api.montoya.ui.menu.MenuItem;


public class Extension implements BurpExtension {
    private static final String EXTENSION_NAME = "Bulama";
    @Override
    public void initialize(MontoyaApi montoyaApi) {
        montoyaApi.extension().setName(EXTENSION_NAME);
        montoyaApi.extension().setName("Bulama");
        montoyaApi.logging().logToOutput("Bulama extension loaded");

        BasicMenuItem alertEventItem = BasicMenuItem.basicMenuItem("Raise critical alert").withAction(() -> montoyaApi.logging().raiseCriticalEvent("Alert from extension"));
        BasicMenuItem basicMenuItem = MenuItem.basicMenuItem("Unload extension");
        MenuItem unloadExtensionItem = basicMenuItem.withAction(() -> montoyaApi.extension().unload());

        Menu menu = Menu.menu(EXTENSION_NAME).withMenuItems(alertEventItem, unloadExtensionItem);

        montoyaApi.userInterface().menuBar().registerMenu(menu);

        montoyaApi.extension().registerUnloadingHandler(new MyExtensionUnloadingHandler(montoyaApi));

        UserInterface ui = new UserInterface();

        montoyaApi.userInterface().registerSuiteTab(EXTENSION_NAME, ui.getUI());
        


        // TODO Add your code here
    }
}