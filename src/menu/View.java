package menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import util.Log;
import util.Settings;
import util.Util;

public class View extends JFrame {
  private Model model;
  
  public JCheckBox chHardcore;
  public JComboBox coLang;
  public JComboBox coLog;
  public JCheckBox chShowHud;
  public JTextField tfSqSize;
  public JTextField tfSqGrow;
  public JTextField tfSqNewAmount;
  public JTextField tfSqPoints;
  public JTextField tfLvlStart;
  public JTextField tfLvlMax;
  public JTextField tfLvlNext;
  public JTextField tfLvlFac;
  public JCheckBox chFullscreen;
  public JCheckBox chVsync;
  public JComboBox coResolution;
  public JComboBox coFramerate;
          
  public View(Model model) {
    this(Settings.DISPLAY_TITLE+" v"+model.myVersion, 350, 460, model);
  }

  protected View(String title, int width, int height, Model model) {
    super(title);
    
    this.model = model;    
    if(model.error == null) {
      this.refreshContent();
      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      this.setSize(width, height);
      this.setLocationRelativeTo(null);
      this.setResizable(false);
      this.setIconImage(new ImageIcon(Util.classLoader.getResource("res/img/window/icon256.png")).getImage());
    }else{
      this.initLookAndFeel(); // yes it's duplicated, but needed
      errorMessage(model.error);
    }
    
    this.initLookAndFeel();
    this.setVisible(true);
  }
  
  private void refreshContent() {
    this.getContentPane().removeAll();
    
    // set up JObjects
    JPanel masterPanel = new JPanel();
    chHardcore = new JCheckBox();
    coLang = new JComboBox();
    coLog = new JComboBox();
    chShowHud = new JCheckBox();
    tfSqSize = new JTextField();
    tfSqGrow = new JTextField();
    tfSqNewAmount = new JTextField();
    tfSqPoints = new JTextField();
    tfLvlStart = new JTextField();
    tfLvlMax = new JTextField();
    tfLvlNext = new JTextField();
    tfLvlFac = new JTextField();
    chFullscreen = new JCheckBox();
    chVsync = new JCheckBox();
    coResolution = new JComboBox();
    coFramerate = new JComboBox();
    
    // refresh language
    Util.changeLanguage();
    
    // general
    JPanel pGeneral = new JPanel(new GridLayout(2,2));
    pGeneral.setPreferredSize(new Dimension(330,70));
    pGeneral.setBorder(BorderFactory.createTitledBorder(Util.getProperty("optionsTitleGeneral")));
    masterPanel.add(pGeneral);
    
    pGeneral.add(getCheckBox("Hardcore", chHardcore, Settings.getBoolean("hardcore"), true));
    pGeneral.add(getComboBox(Util.getProperty("optionsLanguage"), coLang, new String[]{"en","de"}, Settings.getString("language")));
    coLang.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Log.debug("View.coLang "+e.getActionCommand());
        saveSettings();
        refreshContent();
        revalidate();
      }
    });
    pGeneral.add(getCheckBox(Util.getProperty("optionsShowHud"), chShowHud, Settings.getBoolean("show_hud"), true));
    pGeneral.add(getComboBox(Util.getProperty("optionsLogLevel"), coLog, Log.logLevelsStrings, Settings.getString("log_level")));
    
    // squarez
    JPanel pSquarez = new JPanel(new GridLayout(2,2));
    pSquarez.setPreferredSize(new Dimension(330,70));
    pSquarez.setBorder(BorderFactory.createTitledBorder("Squarez"));
    masterPanel.add(pSquarez);
    
    pSquarez.add(getTextField("Size", tfSqSize, Settings.getString("sq_size")));
    pSquarez.add(getTextField("Grow", tfSqGrow, Settings.getString("sq_grow")));
    pSquarez.add(getTextField("New Amount", tfSqNewAmount, Settings.getString("sq_new_amount")));
    pSquarez.add(getTextField("Points", tfSqPoints, Settings.getString("sq_points")));
    
    // level
    JPanel pLevel = new JPanel(new GridLayout(2,2));
    pLevel.setPreferredSize(new Dimension(330,70));
    pLevel.setBorder(BorderFactory.createTitledBorder("Level"));
    masterPanel.add(pLevel);
    
    pLevel.add(getTextField("Start", tfLvlStart, Settings.getString("lvl_start")));
    pLevel.add(getTextField("Max", tfLvlMax, Settings.getString("lvl_max")));
    pLevel.add(getTextField("Next", tfLvlNext, Settings.getString("lvl_next")));
    pLevel.add(getTextField("Faculty", tfLvlFac, Settings.getString("lvl_fac")));
    
    // graphics
    JPanel pGraphics = new JPanel(new GridLayout(2,2));
    pGraphics.setPreferredSize(new Dimension(330,70));
    pGraphics.setBorder(BorderFactory.createTitledBorder(Util.getProperty("optionsTitleGraphics")));
    masterPanel.add(pGraphics);
    
    pGraphics.add(getCheckBox(Util.getProperty("optionsFullscreen"), chFullscreen, Settings.getBoolean("fullscreen"), true));
    chFullscreen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        coResolution.setEnabled(!chFullscreen.isSelected());
      }
    });
    pGraphics.add(getCheckBox("VSync", chVsync, Settings.getBoolean("vsync"), true));
    pGraphics.add(getComboBox(Util.getProperty("optionsResolution"), coResolution,
            Settings.getResolutions(), Settings.getInt("display_width")+"x"+Settings.getInt("display_height")));
    coResolution.setEnabled(!chFullscreen.isSelected());
    pGraphics.add(getComboBox(Util.getProperty("optionsRefreshRate"), coFramerate, new String[]{"30","60","120"},
            Settings.getInt("framerate")+""));
    
    // info
    JPanel pInfo = new JPanel(new BorderLayout());
    pInfo.setPreferredSize(new Dimension(330,60));
    pInfo.setBorder(BorderFactory.createTitledBorder(Util.getProperty("optionsInfo")));
    masterPanel.add(pInfo);
    
    JButton buOpen = new JButton(Util.getProperty("optionsOpenGithub"));
    buOpen.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        Util.openWebpage(Settings.GITHUB_URL);
      }
    });
    pInfo.add(buOpen, BorderLayout.WEST);
    
    JButton buHelp = new JButton(Util.getProperty("optionsHelp"));
    buHelp.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        infoMessage(Util.getProperty("optionsHelpDescription"));
      }
    });
    pInfo.add(buHelp, BorderLayout.CENTER);
    
    JButton buResetSettings = new JButton(Util.getProperty("optionsResetSettings"));
    buResetSettings.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        Log.debug("View.buResetSettings "+e.getActionCommand());
        Settings.resetSettings();
        refreshContent();
        revalidate();
      }
    });
    pInfo.add(buResetSettings, BorderLayout.EAST);
    
    String info;
    if(model.newVersionAvailable > 0){
      info = Util.getProperty("optionsVersionNew")+" (v"+model.latestVersion+")"; // new version available
    }else if(model.newVersionAvailable == 0){
      info = Util.getProperty("optionsVersionCurrent")+" (v"+model.myVersion+")"; // version up to date
    }else{
      info = Util.getProperty("optionsVersionNoConnection"); // could not check version
    }
    pInfo.add(new JLabel(" "+info), BorderLayout.SOUTH);
    
    // launch
    JButton buLaunch = new JButton(Util.getProperty("optionsLaunchGame"));
    buLaunch.setPreferredSize(new Dimension(300,50));
    buLaunch.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        close();
        saveSettings();
        new game.Main();
      }
    });
    masterPanel.add(buLaunch);
    this.getContentPane().add(masterPanel);
  }
  
  private void close() {
    this.setVisible(false); 
    this.dispose();
  }
  
  private void saveSettings() {
    Settings.setSettingValue("language", coLang.getSelectedItem());
    Settings.setSettingValue("log_level", coLog.getSelectedItem());
    Settings.setSettingValue("hardcore", chHardcore.isSelected());
    Settings.setSettingValue("show_hud", chShowHud.isSelected());
    Settings.parseSettingValue("sq_size", tfSqSize.getText());
    Settings.parseSettingValue("sq_grow", tfSqGrow.getText());
    Settings.parseSettingValue("sq_points", tfSqPoints.getText());
    Settings.parseSettingValue("sq_new_amount", tfSqNewAmount.getText());
    Settings.parseSettingValue("lvl_start", tfLvlStart.getText());
    Settings.parseSettingValue("lvl_max", tfLvlMax.getText());
    Settings.parseSettingValue("lvl_next", tfLvlNext.getText());
    Settings.parseSettingValue("lvl_fac", tfLvlFac.getText());
    Settings.setSettingValue("fullscreen", chFullscreen.isSelected());
    Settings.setSettingValue("vsync", chVsync.isSelected());
    Settings.parseSettingValue("display_width", ((String)coResolution.getSelectedItem()).split("x")[0]);
    Settings.parseSettingValue("display_height", ((String)coResolution.getSelectedItem()).split("x")[1]);
    Settings.parseSettingValue("framerate", coFramerate.getSelectedItem()+"");
    
    Settings.writeSettings();
  }

  private void initLookAndFeel() {
    try {
      //javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      javax.swing.SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  protected void errorMessage(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    Log.error(message);
    System.exit(1);
  }

  protected void infoMessage(String message) {
    JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
  }

  private JPanel getComboBox(String text, JComboBox comboBox, String[] values, String selected) {
    JPanel panel = new JPanel(new BorderLayout());
    comboBox.removeAllItems();
    
    // add items
    for(String value : values)
      comboBox.addItem(value);
    
    // get index of selected item
    int index = values.length-1;
    while(index>0 && !values[index].equals(selected)){
      index--;
    }
    
    comboBox.setSelectedIndex(index);
    panel.add(new JLabel(" "+text+" "), BorderLayout.WEST);
    panel.add(comboBox, BorderLayout.CENTER);
    return panel;
  }
  
  private JPanel getTextField(String text, JTextField textField, String value) {
    JPanel panel = new JPanel(new BorderLayout());
    textField.setText(value);
    panel.add(new JLabel(" "+text+" "), BorderLayout.WEST);
    panel.add(textField, BorderLayout.CENTER);
    return panel;
  }

  private JPanel getCheckBox(String text, JCheckBox checkBox, boolean value, boolean enabled) {
    JPanel panel = new JPanel(new BorderLayout());
    checkBox.setSelected(value);
    checkBox.setEnabled(enabled);
    panel.add(checkBox, BorderLayout.WEST);
    
    JLabel label = new JLabel(text);
    label.addMouseListener(new MouseAdapter() { // also make the label clickable
      public void mouseClicked(MouseEvent e) {
        checkBox.setSelected(!checkBox.isSelected());
      }  
    }); 
    panel.add(label, BorderLayout.CENTER);
    
    return panel;
  }
}
