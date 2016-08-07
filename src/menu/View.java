package menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
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
import util.Settings;
import util.Util;

public class View extends JFrame {
  private Model model;

  private JPanel masterPanel;
  
  public JCheckBox chHardcore = new JCheckBox();
  public JComboBox coLang = new JComboBox();
  public JCheckBox chCheats = new JCheckBox();
  public JTextField tfSqSize = new JTextField();
  public JTextField tfSqGrow = new JTextField();
  public JTextField tfSqNewAmount = new JTextField();
  public JTextField tfSqPoints = new JTextField();
  public JTextField tfLvlStart = new JTextField();
  public JTextField tfLvlMax = new JTextField();
  public JTextField tfLvlNext = new JTextField();
  public JTextField tfLvlFac = new JTextField();
  public JCheckBox chFullscreen = new JCheckBox();
  public JCheckBox chVsync = new JCheckBox();
  public JComboBox coResolution = new JComboBox();
  public JComboBox coFramerate = new JComboBox();
          
  public View(Model model) {
    this("Squarez", 350, 450, model);
  }

  protected View(String title, int width, int height, Model model) {
    super(title);
    
    this.model = model;    
    this.initLookAndFeel();
    if(model.error == null) {
      this.initPanels();
      this.getContentPane().add(masterPanel);
      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      this.setSize(width, height);
      this.setLocationRelativeTo(null);
      this.setResizable(false);
      this.setIconImage(new ImageIcon(Util.classLoader.getResource("res/img/window/icon256.png")).getImage());
    }else{
      errorMessage(model.error);
    }

    this.setVisible(true);
  }

  private void initPanels() {
    masterPanel = new JPanel();
    
    // general
    JPanel pGeneral = new JPanel(new GridLayout(2,2));
    pGeneral.setPreferredSize(new Dimension(330,70));
    pGeneral.setBorder(BorderFactory.createTitledBorder("General"));
    masterPanel.add(pGeneral);
    
    pGeneral.add(getCheckBox("Hardcore", chHardcore, Settings.hardcore, true));
    pGeneral.add(getComboBox("Language", coLang, new String[]{"en","de"}, Settings.language));
    pGeneral.add(getCheckBox("Cheats", chCheats, Settings.cheats, false));
    chCheats.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("X");
      }
    });
    
    // squarez
    JPanel pSquarez = new JPanel(new GridLayout(2,2));
    pSquarez.setPreferredSize(new Dimension(330,70));
    pSquarez.setBorder(BorderFactory.createTitledBorder("Squarez"));
    masterPanel.add(pSquarez);
    
    pSquarez.add(getTextField("Size", tfSqSize, Settings.sq_size+""));
    pSquarez.add(getTextField("Grow", tfSqGrow, Settings.sq_grow+""));
    pSquarez.add(getTextField("New Amount", tfSqNewAmount, Settings.sq_new_amount+""));
    pSquarez.add(getTextField("Points", tfSqPoints, Settings.sq_points+""));
    
    // level
    JPanel pLevel = new JPanel(new GridLayout(2,2));
    pLevel.setPreferredSize(new Dimension(330,70));
    pLevel.setBorder(BorderFactory.createTitledBorder("Level"));
    masterPanel.add(pLevel);
    
    pLevel.add(getTextField("Start", tfLvlStart, Settings.lvl_start+""));
    pLevel.add(getTextField("Max", tfLvlMax, Settings.lvl_max+""));
    pLevel.add(getTextField("Next", tfLvlNext, Settings.lvl_next+""));
    pLevel.add(getTextField("Faculty", tfLvlFac, Settings.lvl_fac+""));
    
    // graphics
    JPanel pGraphics = new JPanel(new GridLayout(2,2));
    pGraphics.setPreferredSize(new Dimension(330,70));
    pGraphics.setBorder(BorderFactory.createTitledBorder("Graphics"));
    masterPanel.add(pGraphics);
    
    pGraphics.add(getCheckBox("Fullscreen", chFullscreen, Settings.fullscreen, true));
    chFullscreen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        coResolution.setEnabled(!chFullscreen.isSelected());
      }
    });
    pGraphics.add(getCheckBox("VSync", chVsync, Settings.vsync, true));
    pGraphics.add(getComboBox("Resolution", coResolution,
            new String[]{"1024x768","1280x720","1280x768","1280x800","1280x960",
              "1280x1024","1360x768","1366x768","1440x900","1536x864","1600x900",
              "1600x1200","1680x1050","1920x1080","1920x1200","2560x1080","2560x1440"},
            Settings.display_width+"x"+Settings.display_height));
    coResolution.setEnabled(!chFullscreen.isSelected());
    pGraphics.add(getComboBox("Refresh Rate", coFramerate, new String[]{"30","60","120"},
            Settings.framerate+""));
    
    // info
    JPanel pInfo = new JPanel(new BorderLayout());
    pInfo.setPreferredSize(new Dimension(330,50));
    pInfo.setBorder(BorderFactory.createTitledBorder("Info"));
    masterPanel.add(pInfo);
    
    JButton buOpen = new JButton("Open Github");
    buOpen.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        Util.openWebpage(Settings.GITHUB_URL);
      }
    });
    pInfo.add(buOpen, BorderLayout.WEST);
    String info;
    if(model.newVersionAvailable > 0){
      // new version available
      info = "A new version of this game is available! (v"+model.latestVersion+")";
    }else{
      if(model.newVersionAvailable == 0){
        // version up to date
        info = "Your game is up to date (v"+model.myVersion+")";
      }else{
        // could not check version
        info = "Could not check online for latest version!";
      }
    }
    pInfo.add(new JLabel(" "+info), BorderLayout.CENTER);
    
    // launch
    JButton buLaunch = new JButton("Launch Game");
    buLaunch.setPreferredSize(new Dimension(300,50));
    buLaunch.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        close();
        saveSettings();
        new game.Main();
      }
    });
    masterPanel.add(buLaunch);
  }
  
  private void close() {
    this.setVisible(false); 
    this.dispose();
  }
  
  private void saveSettings() {
    HashMap<String, Object> settings = new HashMap<>();
    
    System.out.println(tfLvlFac.getText());
    System.out.println(coLang.getSelectedItem());
    
    settings.put("language", coLang.getSelectedItem());
    settings.put("hardcore", chHardcore.isSelected());
    settings.put("cheats", chCheats.isSelected());
    settings.put("sq_size", tfSqSize.getText());
    settings.put("sq_grow", tfSqGrow.getText());
    settings.put("sq_points", tfSqPoints.getText());
    settings.put("sq_new_amount", tfSqNewAmount.getText());
    settings.put("lvl_start", tfLvlStart.getText());
    settings.put("lvl_max", tfLvlMax.getText());
    settings.put("lvl_next", tfLvlNext.getText());
    settings.put("lvl_fac", tfLvlFac.getText());
    settings.put("fullscreen", chFullscreen.isSelected());
    settings.put("vsync", chVsync.isSelected());
    settings.put("display_width", ((String)coResolution.getSelectedItem()).split("x")[0]);
    settings.put("display_height", ((String)coResolution.getSelectedItem()).split("x")[1]);
    settings.put("framerate", coFramerate.getSelectedItem());
    
    Settings.loadSettings(settings);
    Settings.saveSettingsFile(settings);
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
    System.err.println(message);
    System.exit(1);
  }

  protected void infoMessage(String message) {
    JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
  }

  private JPanel getComboBox(String text, JComboBox comboBox, String[] values, String selected) {
    JPanel panel = new JPanel(new BorderLayout());
    
    // add items
    for(String value : values)
      comboBox.addItem(value);
    
    // get index of selected item
    int index = 0;
    while(!values[index].equals(selected))
      index++;
    if(index >= values.length)
      index = 0;
    
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
    panel.add(new JLabel(text), BorderLayout.CENTER);
    return panel;
  }
}
