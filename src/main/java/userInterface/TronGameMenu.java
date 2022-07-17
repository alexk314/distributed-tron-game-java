package userInterface;

import org.apache.log4j.Logger;
import support.MyLog;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

/**
 * @author Philip Borchert
 */
public class TronGameMenu extends JFrame {

    private static final Logger LOGGER = MyLog.createLogger("Log TronGameMenu");
    private final TronGame gameViewGui;
    private ApplicationStubs applicationStubs;
    private boolean hasServer = false;

    public TronGameMenu(ApplicationStubs applicationStubs, TronGame tronGameViewGui) {
        this.gameViewGui = tronGameViewGui;
        initComponents();
        list_games.setModel(new DefaultListModel<>());
        this.applicationStubs = applicationStubs;
        this.applicationStubs.setTronGameMenu(this);
    }

    private String getLocalIp() {
        InetAddress addr = null;
        String localIp = "192.168.178."; // default if address lookup fails
        try {
            addr = InetAddress.getLocalHost();
            localIp = addr.getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("Couldn't resolve local IP in TronGameMenu");
            e.printStackTrace();
        }
        return localIp;
    }

    private String getRandomPort() {
        Random randGen = new Random();

        int startPort = 50000;
        int randomInt = randGen.nextInt(1000);
        int randomPort = startPort + randomInt; // Create a random Port between 50.000-50.999:
        return String.valueOf(randomPort);
    }

    public void setHasServer(boolean hasServer) {
        this.hasServer = hasServer;
    }

    /**
     * @param hostConnection
     */
    public void addGameToGamelist(Connection hostConnection, String receivedGameName) {
        String item = createItemForGameList(hostConnection, receivedGameName);
        addItem(item);
        LOGGER.info(String.format("addGameToGamelist: %s", item));
    }

    private String createItemForGameList(Connection hostConnection, String gameName) {
        StringBuilder hostGameItem = new StringBuilder();
        hostGameItem.append(gameName);
        hostGameItem.append(" with IP:");
        hostGameItem.append(hostConnection.getIp());
        hostGameItem.append(":");
        hostGameItem.append(hostConnection.getPort());
        return hostGameItem.toString();
    }

    /**
     * Fügt ein ListItem als String in die Liste ein.
     *
     * @param itemName
     */
    private void addItem(String itemName) {

        // ListModel aus der Liste holen
        DefaultListModel model = (DefaultListModel) list_games.getModel();

        // String in die Liste eintragen
        model.addElement(itemName);

        // ListModel in die Liste eintragen
        list_games.setModel(model);
    }

    /**
     * Ersetzt die aktuelle Liste, durch eine neue Liste.
     */
    private void clearGameList() {

        // Neues ListModel in die Liste eintragen.
        list_games.setModel(new DefaultListModel<>());
    }

    /**
     * Liefert das Selektierte item als String zurück. Sonst null.
     *
     * @return
     */
    private String getSelectedItem() {
        String itemValue = (String) list_games.getSelectedValue();
        LOGGER.info("Item from Game List selected: " + itemValue);
        return itemValue;
    }

    /**
     * Prüfung auf korrekte Selektierung.
     * 
     * @return true, wenn korrekt. Sonst false.
     */
    private boolean itemIsValid() {
        if (list_games.getSelectedValue() != null) {
            return true;
        }
        return false;
    }

    /**
     * Deklariert das eigene Spiel als Host
     * 
     * @param e Event ist drücken des "Host" Buttons
     */
    private void btn_hostActionPerformed(ActionEvent e) {

        // Server erstellen
        createServer();
        boolean localGame = cb_localGame.isSelected(); // starts a local session if selected

        LOGGER.info("Player Selected Local Game: " + localGame);
        applicationStubs.hostGame(txt_gameName.getText(), localGame); // Start a game

    }

    /**
     * Erzeugt eine Liste von Spielen, die von einem Host geleitet werden.
     * 
     * @param e Event ist drücken des "Update" Buttons
     */
    private void btn_updateActionPerformed(ActionEvent e) {

        // Abbruch, wenn Port leer oder nur Leerzeichen vorhanden oder Server vorhanden.
        if (txt_Port.getText().isBlank())
            return;

        // Server erstellen
        createServer();

        // Liste leeren
        clearGameList();

        // Broadcast anfrage senden
        this.applicationStubs.requestGameList();
    }

    /**
     * Extrahiert aus einem Item aus der Liste die Verbindungsinformation zum Host
     * und verbindet sich.‚
     * 
     * @param e Event ist drücken des "Join" Buttons
     */
    private void btn_joinActionPerformed(ActionEvent e) {
        // Abbruch, wenn kein Item selektiert ist.
        if (!this.itemIsValid()) {
            return;
        }
        String item = getSelectedItem();
        String[] ipAndPort = item.split(":");
        LOGGER.info("Player wants to join Game: " + ipAndPort[0]);
        String hostGameIP = ipAndPort[1];
        String hostGamePort = ipAndPort[2];
        LOGGER.info(String.format("item: %s", item));
        LOGGER.info(String.format("ipAndPort: %s", Arrays.toString(ipAndPort)));
        LOGGER.info(String.format("hostIP: %s | hostPort: %s", hostGameIP, hostGamePort));
        applicationStubs.joinGame(hostGameIP, hostGamePort);
    }

    /**
     * Erstellt einen Server, wenn kein Server vorhanden.
     */
    private void createServer() {

        // Abbruch, wenn IP oder Port nicht vorhanden oder Server vorhanden.
        if (txt_IP.getText().isBlank() || txt_Port.getText().isBlank() || hasServer)
            return;

        // Logging
        LOGGER.info("Button Update: Server starten.");

        // Connection aus IP und Port erstellen (wird aus Textfeldern extrahiert)
        Connection connectionFromUserInput = new Connection(txt_IP.getText(), txt_Port.getText());

        try {
            // Server starten und Server-Flag setzten
            applicationStubs.startServer(connectionFromUserInput);
            setHasServer(true);
        } catch (IOException e1) {
            // Auto-generated catch block
            LOGGER.warn("Server konnte nicht nicht mit Port aus der GUI gestartet werden!" + e1.getMessage(), e1);
            e1.printStackTrace();
        }
    }

    private void initComponents() {
        lbl_gameName = new JLabel();
        txt_gameName = new JTextField();
        btn_host = new JButton();
        separator1 = new JSeparator();
        lbl_gameList = new JLabel();
        scrollPane1 = new JScrollPane();
        list_games = new JList();
        btn_update = new JButton();
        cb_localGame = new JCheckBox();
        lbl_Port = new JLabel();
        txt_Port = new JTextField();
        lbl_IP = new JLabel();
        txt_IP = new JTextField();
        btn_join = new JButton();

        // ======== this ========
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        Container contentPane = getContentPane();

        // ---- lbl_gameName ----
        lbl_gameName.setText("Game name:");

        // ---- txt_gameName ----
        txt_gameName.setText("Tron");

        // ---- btn_host ----
        btn_host.setText("Host game");
        btn_host.addActionListener(e -> btn_hostActionPerformed(e));

        // ---- lbl_gameList ----
        lbl_gameList.setText("Game list");

        // ======== scrollPane1 ========
        {
            scrollPane1.setViewportView(list_games);
        }

        // ---- btn_update ----
        btn_update.setText("Update game list");
        btn_update.addActionListener(e -> btn_updateActionPerformed(e));

        // ---- cb_localGame ----
        cb_localGame.setText("Local");

        // ---- lbl_Port ----
        lbl_Port.setText("Port:");

        // ---- txt_Port ----
        txt_Port.setText(getRandomPort());

        // ---- lbl_IP ----
        lbl_IP.setText("IP:");

        // ---- txt_IP ----
        txt_IP.setText(getLocalIp());

        // ---- btn_join ----
        btn_join.setText("Join game");
        btn_join.addActionListener(e -> btn_joinActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(btn_join, GroupLayout.PREFERRED_SIZE, 506,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout
                                                        .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(lbl_gameName, GroupLayout.Alignment.LEADING,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(lbl_Port, GroupLayout.Alignment.LEADING,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addComponent(txt_gameName)
                                                        .addComponent(txt_Port)))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(lbl_IP, GroupLayout.PREFERRED_SIZE, 75,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txt_IP)
                                                .addGap(18, 18, 18)
                                                .addComponent(cb_localGame, GroupLayout.PREFERRED_SIZE, 73,
                                                        GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btn_host, GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(separator1, GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1)
                                        .addComponent(lbl_gameList, GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btn_update, GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap()));
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lbl_IP)
                                        .addComponent(cb_localGame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(txt_IP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lbl_Port)
                                        .addComponent(txt_Port, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(lbl_gameName)
                                        .addComponent(txt_gameName, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_host, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(separator1, GroupLayout.PREFERRED_SIZE, 3, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lbl_gameList)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_update, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_join, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JLabel lbl_gameName;
    private JTextField txt_gameName;
    private JButton btn_host;
    private JSeparator separator1;
    private JLabel lbl_gameList;
    private JScrollPane scrollPane1;
    private JList list_games;
    private JButton btn_update;
    private JCheckBox cb_localGame;
    private JLabel lbl_Port;
    private JTextField txt_Port;
    private JLabel lbl_IP;
    private JTextField txt_IP;
    private JButton btn_join;
}
