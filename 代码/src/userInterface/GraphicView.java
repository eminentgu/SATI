package userInterface;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class GraphicView extends JFrame {  // 顶层容器类，继承自JFrame
	
	JPanel carrierPanel;  // 载体面板，需要把startPanel和functionPanel面板添加到它上面，Layout设置为CardLayout
	PreparedPanel startPanel;  // 引导界面（初始界面）面板
	MainPanel functionPanel;  // 主要功能面板
	
	GraphicView() {
		carrierPanel = new JPanel();
		startPanel = new PreparedPanel();
		functionPanel = new MainPanel();
		init();
	}
	
	void init() {
		startPanel.init(this);
		CardLayout card = new CardLayout();
		carrierPanel.setLayout(card);
		carrierPanel.add(startPanel, "paneS");  // 将引导界面命名为"paneS"，放置在carrierPanel上
		carrierPanel.add(functionPanel, "paneF");  // 将主要功能界面命名为"paneF"，放置在carrierPanel上
		card.show(carrierPanel, "paneS");  // 初始时显示的是引导界面
		add(carrierPanel);
		setVisible(true);  // 设置底层窗口可见
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}

class PreparedPanel extends JPanel {  // 引导界面类，继承自JPanel，布局为BorderLayout
	
	JLabel hint1, hint2;
	JButton openFileButton, startButton;
	FileChooseController fileChooseController;
	Switch2FunctionController switch2FunctionController;
	Box tipBox;  // tipBox会被添加到tipPanel，作为引导界面的提示面板
	JPanel tipPanel, fileSelectedPanel;  // tipPanel添加在引导界面的WEST，fileSelectedPanel添加在引导界面的CENTER
	List<File> files;
	
	PreparedPanel() {  
		hint1 = new JLabel("文献题录分析系统");
		hint1.setFont(new Font("楷体", Font.BOLD, 50));
		hint1.setForeground(new Color(220, 20, 60));
		hint2 = new JLabel("请选择需要导入的文献题录文件");
		hint2.setFont(new Font("华文中宋", Font.BOLD, 24));
		hint2.setForeground(new Color(255, 99, 71));
		openFileButton = new JButton("浏览...");
		openFileButton.setFont(new Font("楷体", Font.BOLD, 24));
		openFileButton.setForeground(new Color(255, 99, 71));
		startButton = new JButton("开始题录分析");
		startButton.setFont(new Font("楷体", Font.BOLD, 30));
		startButton.setForeground(new Color(255, 99, 71));
		// 创建一系列引导界面上的小组件并设置字体、颜色等
		files = new LinkedList<>();
		fileSelectedPanel = new JPanel();
		tipPanel = new JPanel(new BorderLayout());
		// 创建用于放置小组件的中间容器
		fileChooseController = new FileChooseController();
		switch2FunctionController = new Switch2FunctionController();
		openFileButton.addActionListener(fileChooseController);
		startButton.addActionListener(switch2FunctionController);
		// 创建监视器并为按钮组件注册监视器
		setLayout(new BorderLayout());
	}
	
	void init(GraphicView win) {	
		fileChooseController.setStartPanel(this);
		fileChooseController.setFileSelectedPanel(fileSelectedPanel);	
		switch2FunctionController.setWin(win);
		switch2FunctionController.setFileSelectedPanel(fileSelectedPanel);
		// 设置监视器的成员，让监视器能够使用窗体中的组件
		tipBox = Box.createVerticalBox();
		hint2.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将hint2居中显示
		openFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将浏览打开文件按钮居中显示
		tipBox.add(Box.createVerticalStrut(250)); tipBox.add(hint2); tipBox.add(Box.createVerticalStrut(20)); tipBox.add(openFileButton);
		tipPanel.add(tipBox, BorderLayout.CENTER);
		tipPanel.setBackground(new Color(240, 248, 255));
		add(tipPanel, BorderLayout.WEST);
		// 组装tipBox并将它添加到tipPanel，最后tipPanel添加到WEST位置
		hint1.setHorizontalAlignment(SwingConstants.CENTER);
		add(hint1, BorderLayout.NORTH);
		fileSelectedPanel.setBackground(new Color(240, 248, 255));
		add(fileSelectedPanel, BorderLayout.CENTER);
		add(startButton, BorderLayout.SOUTH);
		setBackground(new Color(240, 248, 255));
	}
	
}

class MainPanel extends JSplitPane {  // 功能实现主界面，继承自JSplitPane，是一个水平的拆分窗格
	
	JLabel hint1, hint2, hint3;
	JComboBox<String> functions;
	JButton reSelectButton, export2DatabaseButton;
	Box menuBox;  // 菜单组件，会被添加到menuPanel中
	JPanel menuPanel, displayPanel, initialPanel, statisticalPanel, analyticalPanel;
	/*
	 * menuPanel是主界面的左侧组件，其中展示了菜单信息；
	* displayPanel是主界面的右侧组件，其中展示了功能实现的展示内容，布局为CardLayout。其中添加了三个面板，它们分别为：initialPanel、statisticalPanel和analyticalPanel。
	* initialPanel是从引导界面切换到主界面时的初始展示面板；statisticalPanel是统计功能的展示面板；analyticalPanel是分析功能的展示面板
	* statisticalPanel和analyticalPanel的布局都是BorderLayout
	*/
	StaGuidePanel staGuidePanel;  // 这是统计功能面板statisticalPanel的NORTH面板，引导用户使用统计功能 
	StaShowPanel staShowPanel;  // 这是统计功能面板statisticalPanel的CENTER面板，展示用户使用统计功能的结果 
	AnaGuidePanel anaGuidePanel;  // 这是分析功能面板analyticalPanel的NORTH面板，引导用户使用分析功能 
	AnaShowPanel anaShowPanel;  // 这是分析功能面板analyticalPanel的CENTER面板，展示用户使用分析功能的结果 
	PanelDisplaycontroller panelDisplaycontroller;
	Switch2StartController switch2StartController;
	ExportController exportController;
	List<String> fileNames;  // 导入的题录文件名列表
	
	MainPanel() {
		Font font1 = new Font("黑体", Font.BOLD, 18);
		Font font2 = new Font("楷体", Font.BOLD, 22);
		hint1 = new JLabel("  已选文件：");
		hint1.setFont(font1);
		hint2 = new JLabel("请选择要使用的功能面板：");
		hint2.setFont(font1);
		hint3 = new JLabel("功能区（请在左侧下拉列表中选择一个功能）");
		hint3.setFont(new Font("华文新魏", Font.BOLD, 40));
		hint3.setForeground(new Color(255, 0, 0));
		functions = new JComboBox<>();
		functions.addItem("统计");
		functions.addItem("分析");
		functions.setSelectedIndex(-1);
	    reSelectButton = new JButton("重新选择文件");
	    reSelectButton.setFont(font2);
	    reSelectButton.setForeground(new Color(255, 69, 0));
		export2DatabaseButton = new JButton("导出数据表");
		export2DatabaseButton.setFont(font2);
		export2DatabaseButton.setForeground(new Color(255, 69, 0));		
		// 创建小组件
		menuBox = Box.createVerticalBox();
		menuPanel = new JPanel();
		initialPanel = new JPanel(new BorderLayout());
		statisticalPanel = new JPanel(new BorderLayout());
		analyticalPanel = new JPanel(new BorderLayout());
		staGuidePanel = new StaGuidePanel(this);
		staShowPanel = new StaShowPanel();
		anaGuidePanel = new AnaGuidePanel(this);
		anaShowPanel = new AnaShowPanel();
		displayPanel = new JPanel();
		// 创建一系列窗体作为中间容器容纳小组件
		switch2StartController = new Switch2StartController();
		panelDisplaycontroller = new PanelDisplaycontroller();
		exportController = new ExportController();
		reSelectButton.addActionListener(switch2StartController);
		functions.addItemListener(panelDisplaycontroller);
		export2DatabaseButton.addActionListener(exportController);		
		// 创建监视器并注册在按钮和下拉列表上
	}
	
	void setFileNames(List<String> fileNames) {  // 设置用户已选的题录文件，用于在menuPanel展示
		Collections.sort(fileNames);
		this.fileNames = fileNames;
	}

	void init(GraphicView win) {
		Font font = new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16);
		functions.setSelectedIndex(-1);
		List<JLabel> files = new LinkedList<>();
		for (String fileName: fileNames) {
			JLabel file = new JLabel(fileName);
			file.setFont(font);
			files.add(file);
		}
		panelDisplaycontroller.setFunctionPanel(this);
		switch2StartController.setWin(win);
		// 为监视器设置成员使其能够操作窗体的组件
		hint1.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将hint1居中显示
		menuBox.add(Box.createVerticalStrut(30)); menuBox.add(hint1); menuBox.add(Box.createVerticalStrut(15));
		for (JLabel file: files) {
			file.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将文件名JLabel居中显示
			menuBox.add(file); menuBox.add(Box.createVerticalStrut(10));
		}
		hint2.setAlignmentX(Component.CENTER_ALIGNMENT);
		menuBox.add(Box.createVerticalStrut(50)); menuBox.add(hint2);
		menuBox.add(Box.createVerticalStrut(10)); menuBox.add(functions);
		reSelectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		menuBox.add(Box.createVerticalStrut(30)); menuBox.add(reSelectButton);
		export2DatabaseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		menuBox.add(Box.createVerticalStrut(10)); menuBox.add(export2DatabaseButton);	
		menuPanel.add(menuBox);
		menuPanel.setBackground(new Color(240, 255, 240));
		// 组装menuBox，并把它添加到menuPanel
		hint3.setHorizontalAlignment(SwingConstants.CENTER);
		initialPanel.add(hint3, BorderLayout.CENTER);
		initialPanel.setBackground(new Color(176, 196, 222));
		// initialPanel上只有一个组件hint3，用于提示用户
		statisticalPanel.add(staGuidePanel, BorderLayout.NORTH);
		statisticalPanel.add(staShowPanel, BorderLayout.CENTER);
		analyticalPanel.add(anaGuidePanel, BorderLayout.NORTH);
		analyticalPanel.add(anaShowPanel, BorderLayout.CENTER);
		CardLayout card = new CardLayout();
		displayPanel.setLayout(card);
		displayPanel.add(initialPanel, "INITIAL");
		displayPanel.add(statisticalPanel, "STA");
		displayPanel.add(analyticalPanel, "ANA");	
		card.show(displayPanel, "INITIAL");
		// displayPanel布局设置为CarLayout，把三个窗体添加到它上面，并在最开始展示名为"INITIAL"的窗体即initialPanel
		setLeftComponent(menuPanel);
		setRightComponent(displayPanel);
		// 主要功能界面functionPanel继承自JSplitPane，为它设置左右窗格，左侧为menuPanel，右侧为displayPanel
	}
	
}

class StaGuidePanel extends JPanel{  // 统计功能面板的指引面板，继承自JPanel
	
	JLabel hint4, hint5, hint6;
	JComboBox<String> infoNames = new JComboBox<>();
	JTextField text1, text2;
	JButton oneButton, nButton;
	StaInfoSelectController staInfoSelectController;
	StaInquiryController staInquiryController;
	Box guideBox, box1, box2, box3;  // box1、box2和box3都是guideBox的组件，guideBox最后再添加到指引面板上
	
	StaGuidePanel(MainPanel functionPanel){
		Font font1 = new Font("黑体", Font.BOLD, 16);
		Font font2 = new Font("楷体", Font.BOLD, 16);
		hint4 = new JLabel("统计信息名：");
		hint4.setFont(font1);
		hint5 = new JLabel("单个查询：");
		hint5.setFont(font1);
		hint6 = new JLabel("频次TOP N查询：");
		hint6.setFont(font1);
		infoNames = new JComboBox<>();
		infoNames.addItem("关键词"); infoNames.addItem("作者"); infoNames.addItem("机构"); 
		infoNames.setSelectedIndex(-1);
        text1 = new JTextField(20);
        text2 = new JTextField(20);
		oneButton = new JButton("开始查询");
		oneButton.setFont(font2);
		oneButton.setForeground(new Color(255, 0, 0));
		nButton = new JButton("开始查询");
		nButton.setFont(font2);
		nButton.setForeground(new Color(255, 0, 0));
		// 创建统计功能引导窗口上的所有小组件
		staInfoSelectController = new StaInfoSelectController();
		staInquiryController = new StaInquiryController();
		infoNames.addItemListener(staInfoSelectController);
		oneButton.addActionListener(staInquiryController);
	    nButton.addActionListener(staInquiryController);
	    // 为按钮注册相应的监视器
	    setBackground(new Color(250, 250, 210));
		init(functionPanel);
	}
	
	void init(MainPanel functionPanel){	
		staInfoSelectController.setStaGuidePanel(this);
	    staInquiryController.setFunctionPanel(functionPanel);
	    // 设置监视器的成员，用户触发相应事件后监视器便可以操作成员内的小组件
		guideBox = Box.createVerticalBox();
		box1 = Box.createHorizontalBox();
		box2 = Box.createHorizontalBox();
		box3 = Box.createHorizontalBox();
		text1.setEditable(false);
		text2.setEditable(false);
		box1.add(hint4); box1.add(Box.createHorizontalStrut(10)); box1.add(infoNames); box1.add(Box.createHorizontalGlue());
		box2.add(hint5); box2.add(Box.createHorizontalStrut(20)); box2.add(text1); box2.add(Box.createHorizontalStrut(40)); box2.add(oneButton);
		box3.add(hint6); box3.add(Box.createHorizontalStrut(12)); box3.add(text2); box3.add(Box.createHorizontalStrut(40)); box3.add(nButton);
		guideBox.add(box1); guideBox.add(Box.createVerticalStrut(20)); guideBox.add(box2); guideBox.add(Box.createVerticalStrut(10)); guideBox.add(box3);
		add(guideBox);	
		// 将所有小组件组装到大容器guideBox中，最后把它添加到staGuidePanel作为统计功能的引导界面
	}
	
}

class StaShowPanel extends JPanel {  // 统计功能面板的展示面板，继承自JPanel，布局为BorderLayout
	
	JLabel hint7, hint8;
	JTable table;  // 统计结果的表格组件
	JButton delButton, combineButton, delsButton;
	DelController delController;
	DelsController delsController;
	CombineController combineController;
	Box tableHintBox;  // 这是展示统计结果表格的容器，被添加到展示面板的CENTER
	JPanel modificationPanel;  // 这是实现修正功能的面板，如果需要的话，将被添加到展示面板的SOUTH
	
	StaShowPanel(){
		Font font = new Font("楷体", Font.BOLD, 16);
	    hint8 = new JLabel("结果修正：");
	    hint8.setFont(new Font("黑体", Font.BOLD, 16));
	    delButton = new JButton("删除");
	    delButton.setFont(font);
	    delButton.setForeground(new Color(255, 69, 0));
	    delsButton = new JButton("删除所选行");
	    delsButton.setFont(font);
	    delsButton.setForeground(new Color(255, 69, 0));
	    combineButton = new JButton("合并");
	    combineButton.setFont(font);
	    combineButton.setForeground(new Color(255, 69, 0));
	    // 创建窗体上的所有小组件
	    delController = new DelController();
	    combineController = new CombineController();
	    delsController = new DelsController();
	    delButton.addActionListener(delController);
	    delsButton.addActionListener(delsController);
	    combineButton.addActionListener(combineController);
	    // 创建监视器并注册
	    setBackground(new Color(173, 216, 230));
	    setLayout(new BorderLayout());
	}
	
	void init(String chiInfoName, String word) {  // 监视器的actionPerformed方法创建完用户所请求的表格后会调用init方法，将表格及相关组件展示出来
		hint7 = new JLabel(chiInfoName + "为"  + word.trim() + "的频次查询结果");  // 设置标题名称
	    hint7.setFont(new Font("楷体", Font.BOLD, 18));
	    hint7.setForeground(new Color(128, 0, 0));
	    table.setBackground(new Color(224, 255, 255));
	    table.setRowHeight(table.getRowHeight() + 10);
	    table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
	    table.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
	    hint7.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将表格标题hint7居中显示
	    tableHintBox = Box.createVerticalBox();    
	    tableHintBox.add(Box.createVerticalStrut(10)); tableHintBox.add(hint7); tableHintBox.add(Box.createVerticalStrut(10));
	    tableHintBox.add(new JScrollPane(table));  // 表格table被放入滚动窗格后再添加到tableHintBox，这样可以使表格获得滚动条
	    add(tableHintBox, BorderLayout.CENTER);
	    // tableHintBox是表格和表格标题的组合，会被添加到staShowPanel的CENTER位置		
		delController.setInfoName(chiInfoName);
		delController.setTable(table);
		delsController.setTable(table);
		combineController.setInfoName(chiInfoName);
		combineController.setTable(table);
		// 为各组件设置成员来操纵表格的构造
		modificationPanel = new JPanel();
	    modificationPanel.add(hint8); modificationPanel.add(delButton); 
	    modificationPanel.add(delsButton);  modificationPanel.add(combineButton);
	    modificationPanel.setBackground(new Color(173, 216, 230));	    
		if (chiInfoName.equals("作者")) {}
		else {  // 如果用户查询频次的字段为关键词或机构，就会将modificationPanel添加到SOUTH位置，实现修正功能
			add(modificationPanel, BorderLayout.SOUTH);
		}
		revalidate();
		repaint();
	}
	
	void init(String chiInfoName, int n) {  // 这是init方法的重载，用以展示Top N频次查询结果的表格
		hint7 = new JLabel(chiInfoName + "的频次TOP " + n + "查询结果");  // 设置标题名称
	    hint7.setFont(new Font("楷体", Font.BOLD, 18));
	    hint7.setForeground(new Color(128, 0, 0));
	    table.setBackground(new Color(224, 255, 255));
	    table.setRowHeight(table.getRowHeight() + 10);
	    table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
	    table.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
	    tableHintBox = Box.createVerticalBox();
	    modificationPanel = new JPanel();
	    tableHintBox.add(Box.createVerticalStrut(10)); tableHintBox.add(hint7); tableHintBox.add(Box.createVerticalStrut(10)); 
	    tableHintBox.add(new JScrollPane(table));  // 表格table被放入滚动窗格后再添加到tableHintBox
		hint7.setAlignmentX(Component.CENTER_ALIGNMENT);	  // 将表格标题hint7居中显示
		delController.setInfoName(chiInfoName);
		delController.setTable(table);
		delsController.setTable(table);
		combineController.setInfoName(chiInfoName);
		combineController.setTable(table);
	    modificationPanel.add(hint8); modificationPanel.add(delButton); 
	    modificationPanel.add(delsButton);  modificationPanel.add(combineButton);
	    modificationPanel.setBackground(new Color(173, 216, 230));
		add(tableHintBox, BorderLayout.CENTER);
		if (chiInfoName.equals("作者")) {}
		else {
			add(modificationPanel, BorderLayout.SOUTH);
		}
		revalidate();
		repaint();
	}
	
}

class AnaGuidePanel extends JPanel {  // 分析功能面板的指引面板，继承自JPanel
	
	JLabel hint4, hint5, hint6, hint7, hint8, hint9, hint10;
	JComboBox<String> infoNames1, infoNames2;
	JTextField text1, text2, text3, text4;
	JButton oneButton, nButton, distributionButton;
	Box guideBox, boxV, boxH1, boxH2, boxH3, boxH4, boxH5;  // boxH1、boxH2和boxH3被添加到boxV中，然后boxV、boxH4和boxH5被添加到box，box最终添加到指引面板anaGuidePanel上
	AnaInfoSelectController anaInfoSelectController;
	AnaInquiryController anaInquiryController;
	
	AnaGuidePanel(MainPanel functionPanel){
		Font font1 = new Font("仿宋", Font.BOLD, 20);
		Font font2 = new Font("黑体", Font.BOLD, 16);
		Font font3 = new Font("楷体", Font.BOLD, 16);
		hint4 = new JLabel("共现查询：");
		hint4.setFont(font1);
		hint5 = new JLabel("选择待分析的字段：");
		hint5.setFont(font2);
		hint6 = new JLabel("单个查询：");
		hint6.setFont(font2);
		hint7 = new JLabel("共现频次TOP N查询：");
		hint7.setFont(font2);
		hint8 = new JLabel("分布查询：");
		hint8.setFont(font1);
		hint9 = new JLabel("选择需查询分布的字段：");
		hint9.setFont(font2);
		hint10 = new JLabel("输入该字段值：");
		hint10.setFont(font2);
		infoNames1 = new JComboBox<>();  // infoNames1是共现查询的下拉列表
		infoNames1.addItem("关键词"); infoNames1.addItem("作者"); infoNames1.addItem("机构");
		infoNames1.setSelectedIndex(-1);
		infoNames2 = new JComboBox<>();  // infoNames2是分布查询的下拉列表
		infoNames2.addItem("关键词"); infoNames2.addItem("机构");
		infoNames2.setSelectedIndex(-1);
		text1 = new JTextField(30);
		text2 = new JTextField(30);
		text3 = new JTextField(15);
		text4 = new JTextField(20);
		oneButton = new JButton("开始查询");
		oneButton.setFont(font3); oneButton.setForeground(new Color(255, 0, 0));
		nButton = new JButton("开始查询");
		nButton.setFont(font3); nButton.setForeground(new Color(255, 0, 0));
		distributionButton = new JButton("开始查询");
		distributionButton.setFont(font3); distributionButton.setForeground(new Color(255, 0, 0));
		// 创建分析功能引导界面的所有小组件
		anaInfoSelectController = new AnaInfoSelectController();
		anaInquiryController = new AnaInquiryController();
		infoNames1.addItemListener(anaInfoSelectController);
		infoNames2.addItemListener(anaInfoSelectController);
		oneButton.addActionListener(anaInquiryController);
		nButton.addActionListener(anaInquiryController);
		distributionButton.addActionListener(anaInquiryController);
		// 创建监视器并把它们注册到对应的按钮及下拉列表
		init(functionPanel);
	}
	
	void init(MainPanel functionPanel) {
		text1.setEditable(false); text2.setEditable(false);
		text3.setEditable(false); text4.setEditable(false);		
		anaInfoSelectController.setAnaGuidePanel(this);		
		anaInquiryController.setFunctionPanel(functionPanel);
		// 为监视器设置成员操纵表格的生成
		guideBox = Box.createVerticalBox();
		boxV= Box.createVerticalBox();
		boxH1 = Box.createHorizontalBox();
		boxH2 = Box.createHorizontalBox();
		boxH3 = Box.createHorizontalBox();
		boxH4 = Box.createHorizontalBox();
		boxH5 = Box.createHorizontalBox();
		boxH1.add(Box.createHorizontalStrut(300)); boxH1.add(hint5); boxH1.add(Box.createHorizontalStrut(10));
		boxH1.add(infoNames1); boxH1.add(Box.createHorizontalStrut(300));
		boxH2.add(hint6); boxH2.add(Box.createHorizontalStrut(10)); boxH2.add(text1); boxH2.add(Box.createHorizontalStrut(15));
		boxH2.add(text2); boxH2.add(Box.createHorizontalStrut(20)); boxH2.add(oneButton);
		boxH3.add(hint7); boxH3.add(Box.createHorizontalStrut(10)); boxH3.add(text3); boxH3.add(Box.createHorizontalStrut(20)); boxH3.add(nButton);// nButton之前加了支撑长度
		boxV.add(boxH1); boxV.add(Box.createVerticalStrut(20)); boxV.add(boxH2); boxV.add(Box.createVerticalStrut(15));  boxV.add(boxH3);
		boxH4.add(Box.createHorizontalStrut(300)); boxH4.add(hint9);  boxH4.add(Box.createHorizontalStrut(10));
		boxH4.add(infoNames2); boxH4.add(Box.createHorizontalStrut(280));
		boxH5.add(hint10); boxH5.add(Box.createHorizontalStrut(10)); boxH5.add(text4);
		boxH5.add(Box.createHorizontalStrut(20)); boxH5.add(distributionButton);
		guideBox.add(hint4); guideBox.add(Box.createVerticalStrut(10)); guideBox.add(boxV);
		guideBox.add(Box.createVerticalStrut(20)); guideBox.add(hint8); guideBox.add(Box.createVerticalStrut(20)); 
		guideBox.add(boxH4); guideBox.add(Box.createVerticalStrut(10)); guideBox.add(boxH5); 
		add(guideBox);
		// 将分析功能引导界面的小组件全部组合到box中，然后把box添加到引导界面
		setBackground(new Color(250, 250, 210));
	}
	
}

class AnaShowPanel extends JPanel {  //分析功能面板的展示面板，继承自JPanel
	JLabel hint11;
	JPanel tablePanel;
	Box tableHintBox;  // 这是展示分析结果表格的容器，被添加到展示面板的CENTER
	JTable cooccurrenceTable, authorTable, authorAddressTable, keywordsTable, journalTable;  
	/* 
	 * 统计结果的一系列表格组件：cooccurrenceTable是共现查询的表格，authorTable是分布查询获得的作者分布表格，
	* authorAddressTable是分布查询获得的机构分布表格，keywordsTable是分布查询获得的作关键词分布表格，journalTable是分布查询获得的作期刊分布表格。
	*/
	
	AnaShowPanel(){
		setBackground(new Color(173, 216, 230));
		setLayout(new BorderLayout());
	}
	/*以下三个init重载方法与StaShowPanel类中的init方法类似，都是根据用户的查询需求定制不同的创建表格及其标题的方法：
	 * 把相应的table和hint放入tableHintBox之后，再把tableHintBox添加到AnaShowPanel上
	 */
	void init(String chiInfoName, String wordName, boolean[] options) {  
		tableHintBox = Box.createVerticalBox();
		hint11 = new JLabel(chiInfoName + "为" + wordName + "的分布信息");  // 设置标题名称
		hint11.setFont(new Font("楷体", Font.BOLD, 18));
		hint11.setForeground(new Color(128, 0, 0));
		int count = 0;  // 变量count存储显示的表格数量，以此来控制tablePanel的GridLayout布局的参数
		for (boolean option: options) {
			if (option) {
				count ++;
			}
		}
		tablePanel = new JPanel();  // 分布查询结果可能有多个表格，需要把它们添加到tablePanel中
		tablePanel.setLayout(new GridLayout(1, count));
		if (options[0] == true) {  // 索引0表示的是作者表格authorTable
			authorTable.setBackground(new Color(224, 255, 255));
			authorTable.setRowHeight(authorTable.getRowHeight() + 10);
			authorTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
			authorTable.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
			tablePanel.add(new JScrollPane(authorTable));  // 表格被放入滚动窗格后再添加到tablePanel
		}
		if (options[1] == true) {  // 索引1表示的是期刊表格journalTable
			journalTable.setBackground(new Color(224, 255, 255));
			journalTable.setRowHeight(journalTable.getRowHeight() + 10);
			journalTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
			journalTable.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
			tablePanel.add(new JScrollPane(journalTable));  // 表格被放入滚动窗格后再添加到tablePanel
		}
		if (options[2] == true) {  // 索引2表示的是关键词表格keywordsTable或者机构表格authorAddressTable，具体为哪一个表格取决于传入的参数chiInfoName
			if (chiInfoName.equals("关键词")) {
				authorAddressTable.setBackground(new Color(224, 255, 255));
				authorAddressTable.setRowHeight(authorAddressTable.getRowHeight() + 10);
				authorAddressTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
				authorAddressTable.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
				tablePanel.add(new JScrollPane(authorAddressTable));  // 表格被放入滚动窗格后再添加到tablePanel
			}
			else {
				keywordsTable.setBackground(new Color(224, 255, 255));
				keywordsTable.setRowHeight(keywordsTable.getRowHeight() + 10);
				keywordsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
				keywordsTable.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
				tablePanel.add(new JScrollPane(keywordsTable));  // 表格被放入滚动窗格后再添加到tablePanel
			}
		}
		tableHintBox.add(Box.createVerticalStrut(10)); tableHintBox.add(hint11); tableHintBox.add(Box.createVerticalStrut(10)); tableHintBox.add(tablePanel);
		hint11.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将表格标题hint11居中显示
		add(tableHintBox, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
	void init(String chiInfoName, String word1, String word2) {
		tableHintBox = Box.createVerticalBox();
		String verb;
		switch (chiInfoName) {  // 根据chiInfoName的不同，选择不同的动词verb搭配（"关键词"搭配"共现"，"作者"搭配"合著"，"机构"搭配"合作"）
			case "关键词":
				verb = "共现";
				break;
			case "作者":
				verb = "合著";
				break;
			default:
				verb = "合作";
		}
		hint11 = new JLabel(chiInfoName + "为" + word1.trim() +  "和" + word2.trim() + "的" + verb + "频次查询结果");  // 设置标题名称
		hint11.setFont(new Font("楷体", Font.BOLD, 18));
		hint11.setForeground(new Color(128, 0, 0));
		cooccurrenceTable.setBackground(new Color(224, 255, 255));
		cooccurrenceTable.setRowHeight(cooccurrenceTable.getRowHeight() + 10);
		cooccurrenceTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		cooccurrenceTable.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
		tableHintBox.add(Box.createVerticalStrut(10)); tableHintBox.add(hint11); tableHintBox.add(Box.createVerticalStrut(10)); 
		tableHintBox.add(new JScrollPane(cooccurrenceTable));  // 表格被放入滚动窗格后再添加到tableHintBox
		hint11.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将表格标题hint11居中显示
		add(tableHintBox, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
	void init(String chiInfoName, int n) {
		tableHintBox = Box.createVerticalBox();
		String verb;
		switch (chiInfoName) {  // 根据chiInfoName的不同，选择不同的动词verb搭配（"关键词"搭配"共现"，"作者"搭配"合著"，"机构"搭配"合作"）
			case "关键词":
				verb = "共现";
				break;
			case "作者":
				verb = "合著";
				break;
			default:
				verb = "合作";
		}
		hint11 = new JLabel(chiInfoName + verb + "的TOP " + n +  "查询结果");  // 设置标题名称
		hint11.setFont(new Font("楷体", Font.BOLD, 18));
		hint11.setForeground(new Color(128, 0, 0));
		cooccurrenceTable.setBackground(new Color(224, 255, 255));
		cooccurrenceTable.setRowHeight(cooccurrenceTable.getRowHeight() + 10);
		cooccurrenceTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		cooccurrenceTable.getTableHeader().setFont(new Font("楷体", Font.BOLD, 16));
		tableHintBox.add(Box.createVerticalStrut(10)); tableHintBox.add(hint11); tableHintBox.add(Box.createVerticalStrut(10));
		tableHintBox.add(new JScrollPane(cooccurrenceTable));  // 表格被放入滚动窗格后再添加到tableHintBox
		hint11.setAlignmentX(Component.CENTER_ALIGNMENT);  // 将表格标题hint11居中显示
		add(tableHintBox, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
}