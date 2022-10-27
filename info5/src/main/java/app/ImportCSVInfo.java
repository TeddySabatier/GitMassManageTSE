package app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import java.awt.Color;
/**
 * @author Teddy SABATIER
 *
 */
public class ImportCSVInfo extends JFrame {

	private JTable table;
	static JFrame frame = new JFrame();

	/**
	 * Create the frame.
	 */
	// Transform the data from the Jtable onto a CSV file
	/**Export the jtable to a CSV file
	 * @param tableToExport
	 * @param pathToExportTo
	 * @return
	 */
	public static boolean exportToCSV(JTable tableToExport, String pathToExportTo) {

		try {

			TableModel model = tableToExport.getModel();
			// Get the date the CSV File name will be the current date of the system
			long millis = System.currentTimeMillis();
			java.sql.Date date = new java.sql.Date(millis);
			FileWriter csv = new FileWriter(
					new File(pathToExportTo + "\\" + date.toString().replace('-', '_') + ".csv"));
			// Write the headers
			for (int i = 0; i < model.getColumnCount(); i++) {
				csv.write(model.getColumnName(i) + ";");
			}

			csv.write("\n");
			// Write the data replace some characters that can be interpreted by the csv
			// file
			for (int i = 0; i < model.getRowCount(); i++) {
				for (int j = 0; j < model.getColumnCount(); j++) {
					if (model.getValueAt(i, j) != null) {
						//Replace characters that would do a line break in the csv
						csv.write(model.getValueAt(i, j).toString().replace("\n", "|").replace("	", "|")
								.replace("\r", "|").replace(";", ",") + ";");
					}
				}
				csv.write("\n");
			}

			csv.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Custom renderer to display red or green column on rows depending on how the
	// operation went
	public class CustomTableCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int vColIndex) {
			final Color green = new Color(102, 204, 51);
			final Color red = new Color(255, 100, 100);
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex,
					vColIndex);
			String ok_Nok = (String) table.getModel().getValueAt(rowIndex, 3);
			if (ok_Nok == "OK") {
				comp.setBackground(green);
				comp.setForeground(Color.BLACK);

			} else if (ok_Nok == "NOK") {
				comp.setBackground(red);
				comp.setForeground(Color.BLACK);
			} else {
				comp.setBackground(Color.WHITE);
				comp.setForeground(Color.BLACK);
			}
			return comp;
		}
	}

	/**Displa the informations from the import
	 * @param args
	 * @param panel
	 */
	public ImportCSVInfo(ArrayList<String[]> args, JPanel panel) {
		// Does not close the whole application only the frame
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 455);
		setLocationRelativeTo(panel);
		setIconImage(new ImageIcon("./src/main/java/assets/icons/gitlab.png").getImage());
		// Header of the columns
		List<String> columns = new ArrayList<String>();
		columns.add("Action");
		columns.add("Cible");
		columns.add("Etat/Erreur");
		columns.add("Ok/NOK");
		// Build the table from the datas
		TableModel tableModel = new DefaultTableModel(args.toArray(new Object[][] {}), columns.toArray());
		table = new JTable(tableModel);
		table.getTableHeader().setVisible(true);
		// Hide the Ok/NOK column
		table.getColumnModel().getColumn(3).setMinWidth(0);
		table.getColumnModel().getColumn(3).setMaxWidth(0);
		// Apply the colors
		table.getColumnModel().getColumn(0).setCellRenderer(new CustomTableCellRenderer());
		table.getColumnModel().getColumn(1).setCellRenderer(new CustomTableCellRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new CustomTableCellRenderer());
		// Copy the end message on the 3 columns at the last row
		table.getModel().setValueAt(table.getModel().getValueAt(table.getModel().getRowCount() - 1, 0),
				table.getModel().getRowCount() - 1, 1);
		table.getModel().setValueAt(table.getModel().getValueAt(table.getModel().getRowCount() - 1, 0),
				table.getModel().getRowCount() - 1, 2);
		getContentPane().add(new JScrollPane(table));

		setVisible(true);

		// Directory chooser
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choisir dossier ou enregitrer le fichier CSV");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setVisible(false);
		// Button to save the result as a csv file
		JButton btnNewButton = new JButton("ToCSV");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setVisible(true);
				int returnValue = chooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					if (exportToCSV(table, chooser.getSelectedFile().toString())) {
						// Display the state of the message and then dispose of the frame
						JOptionPane.showMessageDialog(null, "Export terminé");
						frame.dispose();
					} else {
						// Display the state of the message and then dispose of the frame
						JOptionPane.showMessageDialog(null, "Erreur export");
						frame.dispose();
					}
				} else {
					System.out.println("No Selection ");
				}
			}
		});
		getContentPane().add(btnNewButton, BorderLayout.SOUTH);

		JLabel lblNewLabel = new JLabel("Etat de l'export");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblNewLabel, BorderLayout.NORTH);

	}

}
