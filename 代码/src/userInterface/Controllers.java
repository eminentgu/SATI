package userInterface;

import importPaper.*;
import genericOperation.*;
import analyticalFunction.*;
import statisticalFunction.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;

class Switch2FunctionController implements ActionListener {  // 监视器控制引导界面切换到功能界面，注册在startPanel的成员startButton上
	
	private GraphicView win;
	private JPanel fileSelectedPanel;
	
	void setWin(GraphicView win) {
		this.win = win;
	}
	
	void setFileSelectedPanel(JPanel fileSelectedPanel) {
		this.fileSelectedPanel = fileSelectedPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		Component[] components = fileSelectedPanel.getComponents();  // components数组中都是startPanel上的文件复选框
		List<String> chosenFileNames = new LinkedList<>();  // chosenFileNames列表中存放用户选择的题录文件的文件名
		List<File> chosenFiles = win.startPanel.files;  // chosenFiles存放用户选择的题录文件的文件
		boolean hasFile = false, isValid = true;  // hasFile记录用户是否选择了题录文件，isValid检查用户选择的文件是否为题录文件（后缀名为.net）
		for (int i = components.length - 1; i >= 0; i --) {
			JCheckBox chosenFile = (JCheckBox) components[i];
			String fileName = chosenFile.getText();
			if (chosenFile.isSelected()) {
				if (fileName.endsWith(".net")) {
					hasFile = true;
					chosenFileNames.add(fileName);
				}
				else {
					isValid = false;
					break;
				}
			}
			else {
				chosenFiles.remove(i);
			}
		}
		CardLayout card = (CardLayout) win.carrierPanel.getLayout();
		win.carrierPanel.remove(win.startPanel);
		win.startPanel = new PreparedPanel();
		win.startPanel.init(win);
		win.carrierPanel.add(win.startPanel, "paneS");
		card.show(win.carrierPanel, "paneS");
		// 一旦用户点击了"开始题录分析"按钮，就会重新实例化startPanel
		if (hasFile && isValid) {  // 如果用户选择了题录文件且所选文件都是有效的（后缀名为.net）就会开始题录分析进入主界面functionPanel
			DeleteAllTables.delete();  // 开始进行题录分析前必须要把之前的数据表全部删除
			String tableName = "Paper Information";
			String[] infoNames = {"Reference Type", "Title", "Author", "Author Address", "Journal", "Year", "Volume", "Issue", "Pages", "Keywords", "Abstract", "ISBN/ISSN", "Notes", "URL", "DOI", "Database Provider"};
			GenericCreateTable table = new GenericCreateTable(tableName, infoNames);
			table.initialTable();
			CreatePaperInfo.importPaperInfo(table, chosenFiles);  // 使用importPaper包CreatePaperInfo类的静态方法importPaperInfo导入用户选择的题录信息
			win.functionPanel.setFileNames(chosenFileNames);  // 将用户选择的题录文件名传给功能实现主界面functionPanel
			win.functionPanel.init(win);
			card.show(win.carrierPanel, "paneF");
			// 调用init方法初始化主界面functionPanel并将carrierPanel切换显示为主界面
		}
		if (! isValid) {
			JOptionPane.showMessageDialog(null, "选择的文件不是文献题录，文献题录文件扩展名应为.net，请重新选择！", "提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (! hasFile) {
			JOptionPane.showMessageDialog(null, "未选择题录文件！", "提示", JOptionPane.WARNING_MESSAGE);
		}
		// 如果文件无效或者用户未选择文件，就会弹出对话框提示用户，然后返回startPanel重新选择文件
	}
	
}

class FileChooseController implements ActionListener {  // 控制文件选择监视器，注册在openFileButton上

	private JPanel fileSelectedPanel;
	private PreparedPanel startPanel;
	private int fileNum = 0;  // 记录用户已经选择的文件数量
	
	void setStartPanel(PreparedPanel startPanel) {
		this.startPanel = startPanel;
	}
	
	void setFileSelectedPanel(JPanel fileSelectedPanel) {
		this.fileSelectedPanel = fileSelectedPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();        
        FileFilter txtFilter = new TxtFileFilter();  // 创建自定义的文件类型筛选器
        fileChooser.setFileFilter(txtFilter);  // 设置默认的文件筛选器
        fileChooser.setMultiSelectionEnabled(true);  //文件可以多选       
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {  // 如果用户在文件选择对话框上点击了"确定"按钮，就会对用户选择的文件进行处理
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
            	startPanel.files.add(file);
                addCheckBox(file);  // 调用私有方法添加文件复选框
            }
        }
	}
	
	private void addCheckBox(File file) {
		JCheckBox chosenFile = new JCheckBox(file.getName());
		chosenFile.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 22));
		chosenFile.setForeground(new Color(205, 92, 92));
		chosenFile.setSelected(true);
		fileNum += 1;
		if (fileNum % 15 == 0) {  // 根据已选文件的数量要调整fileSelectedPanel的布局
			fileSelectedPanel.setLayout(new GridLayout(15, fileNum /15));
			fileSelectedPanel.revalidate();
			fileSelectedPanel.repaint();
		}
		else {
			if (fileNum < 16) {
				fileSelectedPanel.setLayout(new GridLayout(fileNum, 1));
				fileSelectedPanel.revalidate();
				fileSelectedPanel.repaint();
			}
			else {
				fileSelectedPanel.setLayout(new GridLayout(15, fileNum / 15 + 1));
				fileSelectedPanel.revalidate();
				fileSelectedPanel.repaint();
			}
		}
		fileSelectedPanel.add(chosenFile);  // 设置完布局后，最后将复选框chosenFile添加到fileSelectedPanel
	}

}

class TxtFileFilter extends FileFilter {  // 自定义文件过滤器，继承自FileFilter，与前一个监视器FileChooseController配合使用
	
    public String getDescription() {
        return "文本文件 (*.net)";  // 文献题录文件后缀名为.net
    }

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            String fileName = file.getName();
            return fileName.endsWith(".net");
        }
    }
    
}

class Switch2StartController implements ActionListener {  // 监视器控制功能主界面切换到引导界面， 注册在reSelectButton上
	
	private GraphicView win;
	
	void setWin(GraphicView win) {
		this.win = win;
	}
	
	public void actionPerformed(ActionEvent e) {  //要做3件事：1、重新实例化functionPanel；2、提示用户是否保存获得的access数据库文件；3、切换到引导界面
		int ans = JOptionPane.showConfirmDialog(win.functionPanel, "是否要导出此次分析过程中得到的数据表？", "导出数据表", JOptionPane.YES_NO_CANCEL_OPTION);
		// 当用户点击"重新选择文件"按钮要返回开始界面时，弹出对话框询问用户是否要保存处理过程中得到的数据表
		if (ans == JOptionPane.CANCEL_OPTION) {}  // 用户点击"取消"或关闭对话框则无事发生
		else {
			if (ans == JOptionPane.YES_OPTION) {
				Path sourcePath = Path.of("D:/Temp/Database.accdb");
				Path destinationPath = ExportDatabase.selectPath().resolve(sourcePath.getFileName());  // 由
				if (destinationPath != null) {
					ExportDatabase.export(sourcePath, destinationPath);
				}
			}
			CardLayout card = (CardLayout) win.carrierPanel.getLayout();
			win.carrierPanel.remove(win.functionPanel);
			win.functionPanel = new MainPanel();
			win.carrierPanel.add(win.functionPanel, "paneF");
			card.show(win.carrierPanel, "paneS");
			// 无论用户选择的是"是"还是"否"，都会回到最开始的引导界面，表示用户本次文献分析结束；另外还要重新实例化功能主面板
		}
	}

}

class ExportController implements ActionListener{  // 监视器控制数据表保存，注册在export2DatabaseButton上
	
	public void actionPerformed(ActionEvent e) {
		Path sourcePath = Path.of("D:/Temp/Database.accdb");
		Path destinationPath = ExportDatabase.selectPath();
		if (destinationPath != null) {  // 如果用户选择了目标目录，则会将数据库文件复制到目标目录
			destinationPath = destinationPath.resolve(sourcePath.getFileName());
			ExportDatabase.export(sourcePath, destinationPath);
		}
	}

}

class ExportDatabase {  // 该类用于导出数据表的操作

    static void export(Path sourcePath, Path destinationPath) {  // 静态方法，调用即可实现文件从sourcePath复制到destinationPath
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(null, "导出成功！", "提示", JOptionPane.PLAIN_MESSAGE);
        } 
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, "导出失败，请检查文件访问权限！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    static Path selectPath() {  // 与引导界面的文件选择一致，调用这个静态方法会弹出文件选择对话框，但是这里是选择数据库文件的保存目录
    	JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int option = dirChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File directory = dirChooser.getSelectedFile();
            Path directoryPath = directory.toPath();
            return directoryPath;
        }
        else {
        	return null;  // 如果用户点击了"取消"或者关闭了对话框，都会返回null
        }
    }
    
}

class PanelDisplaycontroller implements ItemListener {  // 切换使用功能的监视器，注册在functions下拉列表上
	
	private MainPanel functionPanel;
	
	void setFunctionPanel(MainPanel functionPanel) {
		this.functionPanel = functionPanel;
	}
	
	public void itemStateChanged(ItemEvent e) {
		String functionName = functionPanel.functions.getSelectedItem().toString();  // 获取functions选中的内容（"分析"或"统计"）
		CardLayout card = (CardLayout) functionPanel.displayPanel.getLayout();
		if (functionName.equals("统计")) {
			card.show(functionPanel.displayPanel, "STA");  // 如果选择了"统计"就会显示statisticalPanel
		}
		else {
			card.show(functionPanel.displayPanel, "ANA");  // 如果选择了"分析"就会显示analyticalPanel
		}
	}
	
}

class StaEditController implements FocusListener {  // 该监视器注册在文本框上，可以实现在文本框上的形如"输入待查询的关键词名"的提示性文本，用户点击文本框，文本会自动消失
	
	private String defaultText1, defaultText2, infoName;
	private StaGuidePanel staGuidePanel;
	
	void setInfoName(String infoName) {
		this.infoName = infoName;
		
	}
	
	void setStaGuidePanel(StaGuidePanel staGuidePanel) {
		this.staGuidePanel = staGuidePanel;
		defaultText1 = "输入待查询的" + infoName + "名";
		defaultText2 = "输入一个正整数N";
		staGuidePanel.text1.setText(defaultText1);
		staGuidePanel.text2.setText(defaultText2);
		staGuidePanel.text1.setForeground(Color.GRAY);
		staGuidePanel.text2.setForeground(Color.GRAY);
	}
	
	public void focusGained(FocusEvent e) {  // 文本框获得焦点，则会移除提示性文本，用户可以进行输入
		String defaultText;
		JTextField text = (JTextField) e.getSource();
		if (text == staGuidePanel.text1) {
			defaultText = defaultText1;
		}
		else {
			defaultText = defaultText2;
		}
        if (text.getText().equals(defaultText)) {  // 如果文本框获得焦点（即用户开始输入）且文本框内容为提示性文本defaultText，则清空文本框让用户正常输入
            text.setText("");
            text.setForeground(Color.BLACK);
        }
    }

    public void focusLost(FocusEvent e) {  // 文本框失去焦点且文本框内没有内容，则会重新展示提示性文本
    	String defaultText;
    	JTextField text = (JTextField) e.getSource();
    	if (text == staGuidePanel.text1) {
			defaultText = defaultText1;
		}
		else {
			defaultText = defaultText2;
		}
        if (text.getText().isEmpty()) { // 如果文本框失去焦点（用户不再在文本框中输入）且文本框中没有用户输入的内容，则将文本框内容设置为提示性文本defaultText
            text.setText(defaultText);
            text.setForeground(Color.GRAY);
        }
    }
    
}

class StaInfoSelectController implements ItemListener{  // 该监视器与StaEditController配合使用，实现了文本框展示提示性文本；注册在infoNames下拉列表上
	
	static StaEditController staEditController = new StaEditController();  // 设置静态成员并把它注册到文本框上，防止同一个文本框被注册多个不同StaEditController出现异常
	
	private StaGuidePanel staGuidePanel;
	
	void setStaGuidePanel(StaGuidePanel staGuidePanel) {
		this.staGuidePanel = staGuidePanel;
	}
	
	public void itemStateChanged(ItemEvent e) {
		String infoName = staGuidePanel.infoNames.getSelectedItem().toString();
		staGuidePanel.text1.setEditable(true);
		staGuidePanel.text2.setEditable(true);
		staEditController.setInfoName(infoName);  // 重新设置监视器的infoName
		staEditController.setStaGuidePanel(staGuidePanel);
		staGuidePanel.text1.addFocusListener(staEditController);
		staGuidePanel.text2.addFocusListener(staEditController);
	}
	
}

class StaInquiryController implements ActionListener {  // 统计功能实现监视器，注册在staGuidePanel的两个按钮成员上，点击按钮即可进行相应查询（TopN或单个查询）
	
	private MainPanel functionPanel;
	
	void setFunctionPanel(MainPanel functionPanel) {
		this.functionPanel = functionPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (functionPanel.staGuidePanel.infoNames.getSelectedItem() == null) {  // 如果下拉列表中没有选择任何项，则无法进行查询，弹出对话框提示用户按规程操作
			JOptionPane.showMessageDialog(functionPanel.staShowPanel, "请在下拉列表选择一个查询信息项后再查询！", "提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
		String chiInfoName = functionPanel.staGuidePanel.infoNames.getSelectedItem().toString(), infoName;  // chiInfoName是查询字段的中文名（如"关键词"）, infoName则是它对应的英文名（如"Keywords"）
		String word;
		int n;
		Object[][] data;  // data存储的是表格的数据
		Object[] columnNames = {chiInfoName, "出现频次"};  // columnNames存储的是字段名称 
		switch (chiInfoName) {
			case "关键词":
				infoName = "Keywords";
				break;
			case "作者":
				infoName = "Author";
				break;
			default:
				infoName = "Author Address";
	    }
		FrequencyCount table = new FrequencyCount(infoName);
		table.createFreqTables();  // 点击“开始查询”按钮就会开始创建对应的频次表
		if (functionPanel.staGuidePanel.oneButton == e.getSource()) {  // 判断事件源是oneButton（代表单个查询）还是nButton（代表TopN查询）
			functionPanel.staGuidePanel.text2.setText("输入一个正整数N");
			functionPanel.staGuidePanel.text2.setForeground(Color.GRAY);
			word = functionPanel.staGuidePanel.text1.getText();
			data = table.freqInquiry(word);	  // 以word为参数，调用实例方法进行单个查询，结果保存到data数组
			if (data == null) {
				JOptionPane.showMessageDialog(functionPanel.staShowPanel, "未查询到对应记录。", "提示", JOptionPane.WARNING_MESSAGE);
				return;
			}
			TableModel model = new TableModel(data, columnNames);  // 以数据data和列名columnNames构造表模型model
			functionPanel.staShowPanel.table = new JTable(model);  // 以构造的表模型model构造表格table
			functionPanel.staShowPanel.removeAll();  // 移除staShowPanel上的所有组件
			functionPanel.staShowPanel.init(chiInfoName, word);  // 调用重载的init方法init(String, String)展示本次查询的结果
		}
		else {
			functionPanel.staGuidePanel.text1.setText("输入待查询的" + chiInfoName + "名");
			functionPanel.staGuidePanel.text1.setForeground(Color.GRAY);
			try {
				n = Integer.parseInt(functionPanel.staGuidePanel.text2.getText());  // 获取用户输入的n，如果用户输入的内容无法被解析为整数（比如输入了词语），就会抛出NumberFormatException异常
				if (n <= 0) {  // 如果用户输入了一个非正整数，手动抛出NumberFormatException异常
					throw new NumberFormatException();
				}
			}
			catch(NumberFormatException ex) {  // 异常处理，提示用户应该输入一个正整数
				JOptionPane.showMessageDialog(functionPanel.staShowPanel, "请输入一个正整数！", "提示", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String[][] rawData = table.freqInquiry(n);  // TopN查询结果存储在rawData里
			data = dataProcess.getData(rawData, n, functionPanel, false, chiInfoName);  // 经过dataProcess的静态方法getData处理之后，获得处理后的数据data
			TableModel model = new TableModel(data, columnNames);  // 以数据data和列名columnNames构造表模型model
			functionPanel.staShowPanel.table = new JTable(model);  // 以构造的表模型model构造表格table
			functionPanel.staShowPanel.removeAll();  // 移除staShowPanel上的所有组件
			functionPanel.staShowPanel.init(chiInfoName, n);  // 调用重载的init方法init(Stirng, int)展示本次查询的结果
		}
	}
	
}

class TableModel extends DefaultTableModel {  // 自定义表格模型类TableModel，继承自DefaultTableModel；它禁止用户修改表格中的值
	
	TableModel(Object[][] data, Object[] columnNames){
		super(data, columnNames);  // 父类DefaultTableModel可以通过表格数据和表格列名来构造实例
	}

    public boolean isCellEditable(int row, int column) {
        return false; // 禁止所有单元格的编辑
    }
    
}

class dataProcess  {
// 该类用于所有涉及到TopN查询的查询结果数据处理，处理的原因是TopN查询结果rawData共有N列，但是由于N可能大于查询结果的记录总数，因此rawData数组靠后的几个一维数组中是null指针，需要将这部分空间进行压缩	
	static Object[][] data;  // 静态成员data是处理后的数据
	
	static Object[][] getData(Object[][] rawData, int n, MainPanel functionPanel, boolean isCooccurrence, String chiInfoName){
	// rawData是待处理的数据；n表示TopN查询的n；functionPanel控制对话框显示的位置；如果是共现的TopN查询，isCooccurrence为true，如果是频次的TopN查询，则为false；chiInfoName是共现查询的字段名
		int recordNum = process(rawData, n);  // 首先调用process方法处理数据，process方法返回一个整数，为处理后的数据的长度data.length
		if (data.length < rawData.length) { // 说明用户请求的N大于查询结果的记录总数，需要弹出对话框提示用户
			if (isCooccurrence) {
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "所请求的N(" + n + ")大于所有共现记录数，将显示所有记录（共" + String.valueOf(recordNum) + "条）。", "提示", JOptionPane.WARNING_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "所请求的N(" + n +")大于所有" + chiInfoName + "记录数，将显示所有记录（共" + String.valueOf(recordNum) + "条）。", "提示", JOptionPane.WARNING_MESSAGE);
			}
		}
		return data;
	}
	
	static Object[][] getData(Object[][] rawData, int n, MainPanel  functionPanel, String chiInfoName, String distributionName){  // 参数含义与另一个重载的getData一致，distributionName表示分布查询的字段名
		int recordNum = process(rawData, n);
		if (data.length < rawData.length) {
			if (distributionName.equals("关键词")) {
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "所请求的N（系统默认设定为30）大于所有该" + chiInfoName + "对应的不同" + distributionName + "数目，将显示所有记录（共" + String.valueOf(recordNum) + "条）。", "提示", JOptionPane.WARNING_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "所请求的N（系统默认设定为10）大于所有该" + chiInfoName + "对应的不同" + distributionName + "数目，将显示所有记录（共" + String.valueOf(recordNum) + "条）。", "提示", JOptionPane.WARNING_MESSAGE);
			}
		}
		return data;
	}
	
	private static int process(Object[][] rawData, int n) {  // 进行数据处理，并记录处理后数据data的长度返回
		int recordNum = n;  // recordNum赋初值为TopN的n
		for (int i = 0; i < rawData.length; i ++) {
			if (rawData[i][0] == null) {  // 说明rawData中的有效记录数只有i条
				recordNum = i;
				break;
			}
		}
		data = new Object[recordNum][2];
		for (int i = 0; i < data.length; i ++) {
			data[i] = rawData[i];
		}
		return recordNum;
	}
	
}

class DelController implements ActionListener{  // 删除监视器，注册在staShowPanel的delButton上
	
	private JTable table;
	private String chiInfoName;
	
	void setTable(JTable table) {  // 设置删除按钮的操作对象表格
		this.table = table;
	}
	
	void setInfoName(String chiInfoName) {
		this.chiInfoName = chiInfoName;
	}
	
	public void actionPerformed(ActionEvent e) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();  // 使用上转型获取表格的模型，模型中存储着表格的各项属性（行数、列数、数据等）
		if (model.getRowCount() < 1) {
			JOptionPane.showMessageDialog(null, "当前界面已经没有记录！", "提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
		Object ans = JOptionPane.showInputDialog(null, "请输入待删除的" + chiInfoName + "名：", "删除", JOptionPane.PLAIN_MESSAGE, null, null, "");  // 弹出输入对话框提示用户输入待删除的值
		String word = null;
		if  (ans != null) {  // 用户没有点击对话框上的"取消"，ans就不为null，ans是用户输入的内容
			word = ans.toString().trim();  // 去除ans首尾的无效空格
		}
		if (word != null) {  // 如果用户输入的不是空字符串
			boolean found = false;
			for (int i =0 ; i < model.getRowCount(); i ++) {  // 遍历表格每一行，直到找到word对应的行索引
				if (model.getValueAt(i, 0).toString().equals(word)) {
					found = true;  // found为true表示找到了word所在行
					model.removeRow(i);  // 找到了便移除该行
					table.revalidate();
					table.repaint();  // 这两句话用来刷新表格显示
				}
			}
			if (! found) {  // 如果未找到需要提示用户该word不存在
				JOptionPane.showMessageDialog(null, "未检索到该" + chiInfoName + "！", "提示", JOptionPane.WARNING_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "删除成功！", "提示", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	
}

class DelsController implements ActionListener{  // 多行删除监视器，注册在delsButton上
	
	JTable table;
	
	void setTable(JTable table) {  // 设置多行删除按钮的操作对象表格
		this.table = table;
	}
	public void actionPerformed(ActionEvent e) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int[] selectedRows = table.getSelectedRows();  // 获得表格被选中的行索引，并且在selectedRows中升序排列
		if (selectedRows.length == 0) {  // 如果没有行被选中，弹出消息提示框提示用户先选中待删除的行再点击按钮
			JOptionPane.showMessageDialog(null, "请选中待删除的行！", "提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
	    for (int i = selectedRows.length - 1; i >= 0; i--) {  // 逆向遍历selectedRows数组，防止删除一行后索引改变导致删除异常
	        model.removeRow(selectedRows[i]);
	    }
	    JOptionPane.showMessageDialog(null, "删除成功！", "提示", JOptionPane.PLAIN_MESSAGE);
	}
		
}

class CombineController implements ActionListener{  // 合并监视器，注册在combine Button上
	
	private JTable table;
	private String chiInfoName;
	
	void setTable(JTable table) {  // 设置合并按钮的操作对象表格
		this.table = table;
	}
	
	void setInfoName(String chiInfoName) {
		this.chiInfoName = chiInfoName;
	}
	
	public void actionPerformed(ActionEvent e) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		if (model.getRowCount() < 2) {  // 如果表格不足两行，直接提示用户无法合并
			JOptionPane.showMessageDialog(null, "当前界面不足2条记录，无法合并！", "提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
		JTextField text1 = new JTextField(20);
        JTextField text2 = new JTextField(20);
        JPanel dialogPanel = new JPanel(new GridLayout(2, 2));
        dialogPanel.add(new JLabel("合并源："));
        dialogPanel.add(text1);
        dialogPanel.add(new JLabel("合并目标："));
        dialogPanel.add(text2);
        int ans = JOptionPane.showOptionDialog(null, dialogPanel, "合并", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);  // 弹出对话框，提示用户输入合并源的词语名称和合并目标的词语名称
        if (ans == JOptionPane.OK_OPTION) {
        	String word1 = text1.getText().trim();
        	String word2 = text2.getText().trim();
        	boolean found1 = false, found2 = false;  // found1和found2指示是否找到word1（合并源）和word2（合并目标）所在的行
			int srcRow = 0, tarRow = 0;  // srcRow和tarRow分别代表合并源所在行和合并目标所在行
			for (int i =0 ; i < model.getRowCount(); i ++) {
				if (model.getValueAt(i, 0).equals(word1)) {
					found1 = true;
					srcRow = i;
				}
				if (model.getValueAt(i, 0).equals(word2)) {
					found2 = true;
					tarRow = i;
				}
				if (found1 && found2) {   // 如果两个删除行都找到便进行合并操作，包含三部分：1、设置合并目标的频次新值为两者之和；2、删除合并源所在行；3、移动合并目标所在行使数据重新降序排列
					model.setValueAt(Integer.parseInt(model.getValueAt(tarRow, 1).toString()) + Integer.parseInt(model.getValueAt(srcRow, 1).toString()), tarRow, 1);
					Object[] newData = new Object[model.getColumnCount()];
					for (int j = 0; j < model.getColumnCount(); j ++) {
						newData[j] = model.getValueAt(tarRow, j);  // 合并目标的新值复制给newData一维数据，之后会将其插入到表中
					}		
					int newSrcRow = tarRow, newTarRow = 0;
					for (int j = tarRow - 1; j >= 0; j --) {  // 逆向遍历表格，直到找到新值newData的插入位置，并赋值给newTarRow
						if (Integer.parseInt(newData[1].toString()) < Integer.parseInt(model.getValueAt(j, 1).toString())) {
							newTarRow = j + 1;
							break;
						}
					}				
					model.removeRow(srcRow);  // 移除合并源所在行
					if (srcRow < newSrcRow) {  // 如果合并源所在行小于newSrcRow（是合并目标所在行），则合并目标所在行会因为合并源所在行被删除而行索引减1
						newSrcRow --;
					}
					model.removeRow(newSrcRow);  // 移除合并目标所在行
					model.insertRow(newTarRow, newData);  // 在之前找到的位置插入新值
					table.revalidate();
					table.repaint();
					break;
				}
			}
			if (! found1 || ! found2) {  // 如果合并源和合并目标中有一个值未搜索到，则弹出消息对话框提示用户
				JOptionPane.showMessageDialog(null, "未检索到合并源/合并目标对应的" + chiInfoName + "！", "提示", JOptionPane.WARNING_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "合并成功！", "提示", JOptionPane.PLAIN_MESSAGE);
			}
        }
	}
	
}

class AnaEditController1 implements FocusListener {  // 作用同StaEditController，用来监视文本框的焦点事件，展示提示性文本

	private String defaultText1, defaultText2, infoName;
	private AnaGuidePanel anaGuidePanel;
	
	void setInfoName(String infoName) {
		this.infoName = infoName;
		
	}
	
	void setAnaGuidePanel(AnaGuidePanel anaGuidePanel) {
		this.anaGuidePanel = anaGuidePanel;
		defaultText1 = "输入待查询的共现" + infoName + "名";
		defaultText2 = "输入一个正整数N";
		anaGuidePanel.text1.setText(defaultText1 + "1");
		anaGuidePanel.text2.setText(defaultText1 + "2");
		anaGuidePanel.text3.setText(defaultText2);
		anaGuidePanel.text1.setForeground(Color.GRAY);
		anaGuidePanel.text2.setForeground(Color.GRAY);
		anaGuidePanel.text3.setForeground(Color.GRAY);
	}
	
	public void focusGained(FocusEvent e) {
		String defaultText;
		JTextField text = (JTextField) e.getSource();
		if (text == anaGuidePanel.text1) {
			defaultText = defaultText1 + "1";
		}
		else {
			if (text == anaGuidePanel.text2) {
				defaultText = defaultText1 + "2";
			}
			else {
				defaultText = defaultText2;
			}	
		}
	    if (text.getText().equals(defaultText)) {  // 如果文本框获得焦点（即用户开始输入）且文本框内容为提示性文本defaultText，则清空文本框让用户正常输入
	        text.setText("");
	        text.setForeground(Color.BLACK);
	    }
	}
	
	public void focusLost(FocusEvent e) {
		String defaultText;
		JTextField text = (JTextField) e.getSource();
		if (text == anaGuidePanel.text1) {
			defaultText = defaultText1 + "1";
		}
		else {
			if (text == anaGuidePanel.text2) {
				defaultText = defaultText1 + "2";
			}
			else {
				defaultText = defaultText2;
			}	
		}
	    if (text.getText().isEmpty()) { // 如果文本框失去焦点（用户不再在文本框中输入）且文本框中没有用户输入的内容，则将文本框内容设置为提示性文本defaultText
	        text.setText(defaultText);
	        text.setForeground(Color.GRAY);
	    }
	}

}

class AnaEditController2 implements FocusListener{
// 作用同StaEditController，用来监视文本框的焦点事件，展示提示性文本，与AnaEditController1的区别是它注册在分布查询的文本框上，而AnaEditController2注册在共现查询的文本框上
	private String defaultText, distributionName;
	
	void setDistributionName(String distributionName) {
		this.distributionName = distributionName;
		
	}
	
	void setAnaGuidePanel(AnaGuidePanel anaGuidePanel) {  // 首先设置提示性文本defaultText
		defaultText = "输入需查询分布的" + distributionName + "名";
		anaGuidePanel.text4.setText(defaultText);
		anaGuidePanel.text4.setForeground(Color.GRAY);
	}
	
	public void focusGained(FocusEvent e) {
		JTextField text = (JTextField) e.getSource();
	    if (text.getText().equals(defaultText)) {  // 如果文本框获得焦点（即用户开始输入）且文本框内容为提示性文本defaultText，则清空文本框让用户正常输入
	        text.setText("");
	        text.setForeground(Color.BLACK);
	    }
	}
	
	public void focusLost(FocusEvent e) {
		JTextField text = (JTextField) e.getSource();
	    if (text.getText().isEmpty()) { // 如果文本框失去焦点（用户不再在文本框中输入）且文本框中没有用户输入的内容，则将文本框内容设置为提示性文本defaultText
	        text.setText(defaultText);
	        text.setForeground(Color.GRAY);
	    }
	}
	
}

class AnaInfoSelectController implements ItemListener{  // 同StaInfoSelectController，与文本编辑监视器配合使用，显示文本框的提示性文本
	
	static AnaEditController1 anaEditController1 = new AnaEditController1();
	static AnaEditController2 anaEditController2 = new AnaEditController2();
	
	private AnaGuidePanel anaGuidePanel;
	
	void setAnaGuidePanel(AnaGuidePanel anaGuidePanel) {
		this.anaGuidePanel = anaGuidePanel;
	}
	
	public void itemStateChanged(ItemEvent e) {
		if (anaGuidePanel.infoNames1 == e.getSource()) {
			anaGuidePanel.text1.setEditable(true);
			anaGuidePanel.text2.setEditable(true);
			anaGuidePanel.text3.setEditable(true);
			String infoName = anaGuidePanel.infoNames1.getSelectedItem().toString();
			anaEditController1.setInfoName(infoName);  // 重新设置监视器的infoName
			anaEditController1.setAnaGuidePanel(anaGuidePanel);
			anaGuidePanel.text1.addFocusListener(anaEditController1);
			anaGuidePanel.text2.addFocusListener(anaEditController1);
			anaGuidePanel.text3.addFocusListener(anaEditController1);	
		}
		else {
			anaGuidePanel.text4.setEditable(true);
			String distributionName = anaGuidePanel.infoNames2.getSelectedItem().toString();
			anaEditController2.setDistributionName(distributionName);  // 重新设置监视器的distributionName
			anaEditController2.setAnaGuidePanel(anaGuidePanel);
			anaGuidePanel.text4.addFocusListener(anaEditController2);	
		}		
	}
	
}


class AnaInquiryController implements ActionListener{  // 处理分析功能查询的监视器，注册在anaGuidePanel的三个查询按钮上
	
	private MainPanel functionPanel;
	
	void setFunctionPanel(MainPanel functionPanel) {
		this.functionPanel = functionPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		TableModel model;
		if (functionPanel.anaGuidePanel.distributionButton == e.getSource()){  // 判断事件源是分布查询按钮还是共现TopN查询按钮还是共现单个查询按钮
			if (functionPanel.anaGuidePanel.infoNames2.getSelectedItem() == null) {  // 同样，如果下拉列表未选择任何内容，都会提出对话框提示用户
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "请在下拉列表选择一个查询分布内容后再查询！", "提示", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object[][] authorAddressData, keywordsData, authorData, journalData;  // 分别存储机构、关键词、作者和期刊的分布查询结果
			Object[] authorColNames, keywordsColNames, authorAddressColNames, journalColNames;  // 分别存储作者、关键词、机构、机构的分布查询结果的字段名称
			String chiInfoName = functionPanel.anaGuidePanel.infoNames2.getSelectedItem().toString(), infoName;
			switch (chiInfoName) {  // 判断需要查询分布的字段名为机构还是关键词，并建立相应的英文表达
				case "机构":
					infoName = "Author Address";
					break;
				default:
					infoName = "Keywords";
			}
			String wordName = functionPanel.anaGuidePanel.text4.getText().trim();  // 从文本框获取需要查询分布的字段的具体取值
			if (wordName.equals("")) {  // 如果为空值提示用户需要输入一个具体取值才能进行分布查询
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "未输入" + chiInfoName + "名！", "提示", JOptionPane.WARNING_MESSAGE);
				return;
			}
			TopNDistribution topNDistribution = new TopNDistribution(infoName, wordName);  // 首先创建TopNDistribution类实例，创建完后会获得infoName取值为wordName的所有论文的信息，并赋值给实例成员info
			if (! topNDistribution.infoNotNull()) {  // 调用infoNotNull方法，判断info是否为空，如果为空说明不存在infoName取值为wordName的记录，弹出对话框提示用户
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "该" + chiInfoName + "不存在！", "提示", JOptionPane.WARNING_MESSAGE);
				return;
			}
			JCheckBox author = new JCheckBox("作者"); 
			JCheckBox authorAddress = new JCheckBox("机构");
			JCheckBox keywords = new JCheckBox("关键词");
			JCheckBox journal = new JCheckBox("期刊");
			JPanel checkPanel = new JPanel(new GridLayout(4, 1));
			checkPanel.add(new JLabel("选择待查询的分布信息：")); checkPanel.add(author); checkPanel.add(journal);
			if (chiInfoName.equals("机构")) {
				checkPanel.add(keywords);
			}
			else {
				checkPanel.add(authorAddress);
			}
			int ans = JOptionPane.showOptionDialog(functionPanel.anaGuidePanel, checkPanel, "选择待查询的分布信息", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
			// 弹出对话框，对话框上有复选框，用户勾选复选框便可以进行对应的字段分布查询
			if (ans == JOptionPane.OK_OPTION) {
				boolean[] options = {false, false, false};  // options数组表示三个字段哪些被用户选中，选中则为true
				boolean selected = false;  // selected标记用户是否选择了至少一个复选框
				if (author.isSelected()) {  // "作者"被选中
					selected = true;
					options[0] = true;
					authorColNames = new Object[]{"作者", "出现频次"};  // 设置相应的列名
					authorData = dataProcess.getData(topNDistribution.distributionInquiry("Author"), 10, functionPanel, chiInfoName, "作者");  // 同样，调用dataProcess的getData方法获取相应的表格数据
					model = new TableModel(authorData, authorColNames);  // 以数据authorData和列名authorColumnNames构造表模型model
					functionPanel.anaShowPanel.authorTable = new JTable(model);  // 以构造的表模型model构造表格authorTable
				}
				if (authorAddress.isSelected()) {  // "机构"被选中
					selected = true;
					options[2] = true;
					authorAddressColNames = new Object[]{"机构", "出现频次"};  // 设置相应的列名
					authorAddressData = dataProcess.getData(topNDistribution.distributionInquiry("Author Address"), 10, functionPanel, chiInfoName, "机构");  // 调用dataProcess的getData方法获取相应的表格数据
					model = new TableModel(authorAddressData, authorAddressColNames);  // 以数据authorAddressData和列名authorAddressColumnNames构造表模型model
					functionPanel.anaShowPanel.authorAddressTable = new JTable(model);  // 以构造的表模型model构造表格authorAddressTable
				}
				if (journal.isSelected()) {  // "期刊"被选中
					selected = true;
					options[1] = true;
					journalColNames = new Object[]{"期刊", "出现频次"};  // 设置相应的列名
					journalData = dataProcess.getData(topNDistribution.distributionInquiry("Journal"), 10, functionPanel, chiInfoName, "期刊");  // 调用dataProcess的getData方法获取相应的表格数据
					model = new TableModel(journalData, journalColNames);  // 以数据journalData和列名journalColumnNames构造表模型model
					functionPanel.anaShowPanel.journalTable = new JTable(model);  // 以构造的表模型model构造表格journalTable
				}
				if (keywords.isSelected()) {  // "关键词"被选中
					selected = true;
					options[2] = true;
					keywordsColNames = new Object[]{"关键词", "出现频次"};  // 设置相应的列名
					keywordsData = dataProcess.getData(topNDistribution.distributionInquiry("Keywords"), 30, functionPanel, chiInfoName, "关键词");  // 调用dataProcess的getData方法获取相应的表格数据
					model = new TableModel(keywordsData, keywordsColNames);  // 以数据keywordsData和列名keywordsColumnNames构造表模型model
					functionPanel.anaShowPanel.keywordsTable = new JTable(model);  // 以构造的表模型model构造表格keywordsTable
				}
				if (! selected) {  // 如果用户一个都没有选择，则弹出消息对话框提示用户未选择任何分布项
					JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "未选择任何分布项！", "提示", JOptionPane.WARNING_MESSAGE);
					return;
				}
				functionPanel.anaShowPanel.removeAll();  // 移除anaShowPanel上的所有组件
				functionPanel.anaShowPanel.init(chiInfoName, wordName, options);  // 调用重载的init方法init(String, String, boolean[])展示本次查询的结果
			}		
		}
		else {  // else分支表示进行的是共现查询，分为单个共现查询和TopN共现查询
			if (functionPanel.anaGuidePanel.infoNames1.getSelectedItem() == null) {  // 同样，如果用户没有选择查询共现的字段，会弹出消息对话框提示用户
				JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "请在下拉列表选择一个查询共现的字段后再查询！", "提示", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object[][] data, rawData;
			String chiInfoName = functionPanel.anaGuidePanel.infoNames1.getSelectedItem().toString(), infoName, verb;  // 获取用户选择的贡献查询字段
			switch (chiInfoName) {
				case "关键词":
					infoName = "Keywords";
					verb = "共现";
					break;
				case "作者":
					infoName = "Author";
					verb = "合著";
					break;
				default:
					infoName = "Author Address";
					verb = "合作";
			}
			Object[] columnNames = {chiInfoName + "1", chiInfoName + "2", verb +"频次"};  // 设置表格的列名
			String word1, word2;  // 单个查询的用户输入为两个单词word1和word2
			int n;  // 表示TopN共现查询的N取值
			if (functionPanel.anaGuidePanel.oneButton == e.getSource()) {  // 如果是单个共现查询
				functionPanel.anaGuidePanel.text3.setText("输入一个正整数N");
				functionPanel.anaGuidePanel.text3.setForeground(Color.GRAY);
				word1 = functionPanel.anaGuidePanel.text1.getText();
				word2 = functionPanel.anaGuidePanel.text2.getText();
				rawData = CooccurrenceCount.cooccurrenceInquiry(infoName, word1, word2);
				if (rawData[0][1] == "null") {
					JOptionPane.showMessageDialog(functionPanel.anaShowPanel, "未查询到对应记录。", "提示", JOptionPane.WARNING_MESSAGE);
					return;
				}
				data =  new Object[1][3];  // data有3列，rawData的两个词语需要分别存放
				String[] word12 = rawData[0][0].toString().split(";");  // 按分号分隔rawData[0][0]，word12数组存放的是分隔出的两个查询词语
				data[0][0] = word12[0]; data[0][1] = word12[1]; data[0][2] = rawData[0][1];
				model = new TableModel(data, columnNames);  // 以数据data和列名columnNames构造表模型model
				functionPanel.anaShowPanel.cooccurrenceTable = new JTable(model);  // 以构造的表模型model构造表格cooccurrenceTable
				functionPanel.anaShowPanel.removeAll();  // 移除anaShowPanel上的所有组件
				functionPanel.anaShowPanel.init(chiInfoName, word1, word2);  // 调用重载的init方法init(String, String, String)展示本次查询的结果
			}
			else {			
				functionPanel.anaGuidePanel.text1.setText("输入待查询的共现" + chiInfoName + "名1");
				functionPanel.anaGuidePanel.text1.setForeground(Color.GRAY);
				functionPanel.anaGuidePanel.text2.setText("输入待查询的共现" + chiInfoName + "名2");
				functionPanel.anaGuidePanel.text2.setForeground(Color.GRAY);
				try {
					n = Integer.parseInt(functionPanel.anaGuidePanel.text3.getText()); 
					if (n <= 0) {
						throw new NumberFormatException();
					}
				}
				catch(NumberFormatException ex) {  // 同前，n为非正整数或无法解析为整数都会抛出该异常，然后提示用户输入一个正整数以进行共现TopN查询
					JOptionPane.showMessageDialog(functionPanel.staShowPanel, "请输入一个正整数！", "提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				rawData = dataProcess.getData(CooccurrenceCount.cooccurrenceInquiry(infoName, n), n, functionPanel, true, chiInfoName);  // 这里经getData处理后仍为rawData是因为共现查询需要将两个词语分开作为两列
				data =  new Object[rawData.length][3];
				for (int i =0; i < data.length; i ++) {
					String[] word12 = rawData[i][0].toString().split(";");
					data[i][0] = word12[0]; data[i][1] = word12[1]; data[i][2] = rawData[i][1];  // 分隔后的两个词语和对应的共现频次分别赋值给data[i][0], data[i][1]及data[i][2]
				}
				model = new TableModel(data, columnNames);  // 以数据data和列名columnNames构造表模型model
				functionPanel.anaShowPanel.cooccurrenceTable = new JTable(model);  // 以构造的表模型model构造表格cooccurrenceTable
				functionPanel.anaShowPanel.removeAll();  // 移除anaShowPanel上的所有组件
				functionPanel.anaShowPanel.init(chiInfoName, n);  // 调用重载的init方法init(String, int)展示本次查询的结果
			}
		}
	}
	
}