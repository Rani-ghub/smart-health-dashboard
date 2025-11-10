package HealthManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.jdatepicker.impl.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

public class DashboardFrame extends JFrame {
	private JTable table;
	private DefaultTableModel tableModel;
	private JDatePickerImpl fromDatePicker, toDatePicker;
	private JTextField bpField, hrField, userField, usernameFilterField;
	private final String databaseName = LoginFrame.databaseName;;
	private final String currentUsername;

	public DashboardFrame(String currentUsername) {
		this.currentUsername = currentUsername;
		setTitle("Health Dashboard");
		setSize(1000, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		initUI();
		setVisible(true);
	}

	private void initUI() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		setupTable();
		setupDatePickers();

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(createAddPanel());
		mainPanel.add(Box.createVerticalStrut(15));
		mainPanel.add(createFilterPanel());
		mainPanel.add(Box.createVerticalStrut(15));

		add(mainPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);

		Calendar today = Calendar.getInstance();
		Calendar lastWeek = Calendar.getInstance();
		lastWeek.add(Calendar.DAY_OF_MONTH, -7);
		loadData("", lastWeek.getTime(), today.getTime());
	}

	private void setupTable() {
		tableModel = new DefaultTableModel(
				new String[] { "ID", "Username", "Blood Pressure", "Heart Rate", "Timestamp" }, 0);
		table = new JTable(tableModel);
	}

	private void setupDatePickers() {
		UtilDateModel fromModel = new UtilDateModel();
		UtilDateModel toModel = new UtilDateModel();
		Calendar today = Calendar.getInstance();
		Calendar lastWeek = Calendar.getInstance();
		lastWeek.add(Calendar.DAY_OF_MONTH, -7);
		fromModel.setValue(lastWeek.getTime());
		fromModel.setSelected(true);
		toModel.setValue(today.getTime());
		toModel.setSelected(true);

		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");

		fromDatePicker = new JDatePickerImpl(new JDatePanelImpl(fromModel, p), new DateLabelFormatter());
		toDatePicker = new JDatePickerImpl(new JDatePanelImpl(toModel, p), new DateLabelFormatter());
	}

	private JPanel createAddPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		userField = new JTextField(currentUsername, 10);
		bpField = new JTextField(6);
		hrField = new JTextField(6);
		JButton addBtn = new JButton("Add Data");

		panel.add(new JLabel("Username:"));
		panel.add(userField);
		panel.add(new JLabel("Blood Pressure:"));
		panel.add(bpField);
		panel.add(new JLabel("Heart Rate:"));
		panel.add(hrField);
		panel.add(addBtn);

		addBtn.addActionListener(e -> handleAddData());
		return panel;
	}

	private JPanel createFilterPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		usernameFilterField = new JTextField(currentUsername, 10);
		JButton filterBtn = new JButton("Filter");
		JButton exportBtn = new JButton("Export to CSV");
		JButton chartBtn = new JButton("Show Chart");

		panel.add(new JLabel("Username:"));
		panel.add(usernameFilterField);
		panel.add(new JLabel("From:"));
		panel.add(fromDatePicker);
		panel.add(new JLabel("To:"));
		panel.add(toDatePicker);
		panel.add(filterBtn);
		panel.add(exportBtn);
		panel.add(chartBtn);

		filterBtn.addActionListener(e -> {
			Date from = (Date) fromDatePicker.getModel().getValue();
			Date to = (Date) toDatePicker.getModel().getValue();
			String username = usernameFilterField.getText().trim();
			loadData(username, from, to);
		});

		exportBtn.addActionListener(e -> exportToCSV());
		chartBtn.addActionListener(e -> showChart());

		return panel;
	}

	private void handleAddData() {
		try {
			String username = userField.getText().trim();
			int bp = Integer.parseInt(bpField.getText());
			int hr = Integer.parseInt(hrField.getText());
			String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

			String query = String.format(
					"INSERT INTO health_data (username, blood_pressure, heart_rate, timestamp) VALUES ('%s', %d, %d, '%s')",
					username, bp, hr, ts);
			DBManager.UpdateDeleteInsertDataIntoDB(query, databaseName);

			Date from = (Date) fromDatePicker.getModel().getValue();
			Date to = (Date) toDatePicker.getModel().getValue();
			loadData("", from, to);

			bpField.setText("");
			hrField.setText("");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
		}
	}

	private void loadData(String username, Date from, Date to) {
		tableModel.setRowCount(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String query = "SELECT * FROM health_data WHERE 1=1";

		if (username != null && !username.isEmpty()) {
			query += " AND username = '" + username + "'";
		}

		if (from != null && to != null) {
			query += " AND DATE(timestamp) BETWEEN '" + sdf.format(from) + "' AND '" + sdf.format(to) + "'";
		}

		try {
			ResultSet rs = DBManager.FetchDataFromDB(query, databaseName);
			while (rs.next()) {
				tableModel.addRow(new Object[] { rs.getInt("id"), rs.getString("username"),
						rs.getString("blood_pressure"), rs.getInt("heart_rate"), rs.getTimestamp("timestamp") });
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
		}
	}

	private void exportToCSV() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save CSV File");
		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".csv")) {
				file = new File(file.getAbsolutePath() + ".csv");
			}

			try (PrintWriter pw = new PrintWriter(file)) {
				pw.println("ID,Username,Blood Pressure,Heart Rate,Timestamp");
				for (int i = 0; i < tableModel.getRowCount(); i++) {
					pw.println(tableModel.getValueAt(i, 0) + "," + tableModel.getValueAt(i, 1) + ","
							+ tableModel.getValueAt(i, 2) + "," + tableModel.getValueAt(i, 3) + ","
							+ tableModel.getValueAt(i, 4));
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage());
			}
		}
	}

	private void showChart() {
		try {
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (int i = 0; i < tableModel.getRowCount(); i++) {
				String username = tableModel.getValueAt(i, 1).toString(); // Username column
				String timestamp = tableModel.getValueAt(i, 4).toString(); // Timestamp column
				int heartRate = Integer.parseInt(tableModel.getValueAt(i, 3).toString()); // Heart Rate column
				String bpRaw = tableModel.getValueAt(i, 2).toString(); // Blood Pressure column
				int systolicBP = bpRaw.contains("/") ? Integer.parseInt(bpRaw.split("/")[0]) : Integer.parseInt(bpRaw);

				// Add user-wise series
				dataset.addValue(heartRate, username + " - Heart Rate", timestamp);
				dataset.addValue(systolicBP, username + " - Blood Pressure", timestamp);
			}

			JFreeChart chart = ChartFactory.createLineChart("Health Trends by User", "Timestamp", "Value", dataset,
					PlotOrientation.VERTICAL, true, true, false);

			ChartPanel chartPanel = new ChartPanel(chart);
			JFrame chartFrame = new JFrame("User-wise Health Chart");
			chartFrame.setSize(900, 500);
			chartFrame.setLocationRelativeTo(this);
			chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			chartFrame.add(chartPanel);
			chartFrame.setVisible(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error generating chart: " + ex.getMessage());
		}
	}

	// Formatter for date picker
	class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		@Override
		public Object stringToValue(String text) throws ParseException {
			return dateFormat.parse(text);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value instanceof Date) {
				return dateFormat.format((Date) value);
			} else if (value instanceof Calendar) {
				return dateFormat.format(((Calendar) value).getTime());
			}
			return "";
		}
	}

}
