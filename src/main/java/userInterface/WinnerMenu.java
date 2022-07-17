package userInterface;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Philip Borchert
 */
public class WinnerMenu extends JFrame {
	public WinnerMenu() {
		initComponents();
	}

	/**
	 * Öffnet das WinnerMenue und gibt an, ob der linke Spieler gewonnen, verloren
	 * oder ein unentschieden an.
	 * 
	 * @param playerLeftLost  ist true, wenn linker Spieler verloren hat.
	 * @param playerRightLost ist true, wenn rechter Spieler verloren hat.
	 */
	public void show(boolean playerLeftLost, boolean playerRightLost) {
			if (!playerLeftLost && playerRightLost) {
				this.txt_message.setText("Player left won!");

			} else if (playerLeftLost && !playerRightLost) {
				this.txt_message.setText("Player right won!");
			}else {
				this.txt_message.setText("UNENTSCHIEDEN!!!");
			}

	}

	private void btn_closeActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	private void initComponents() {
		btn_close = new JButton();
		separator1 = new JSeparator();
		txt_message = new JTextField();

		// ======== this ========
		setResizable(false);
		setVisible(true);
		Container contentPane = getContentPane();

		// ---- btn_close ----
		btn_close.setText("close");
		btn_close.addActionListener(e -> btn_closeActionPerformed(e));

		// ---- txt_message ----
		txt_message.setText("Nachricht eintragen ;)");
		txt_message.setHorizontalAlignment(SwingConstants.CENTER);
		txt_message.setFont(txt_message.getFont().deriveFont(txt_message.getFont().getSize() + 20f));
		txt_message.setEditable(false);

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(contentPaneLayout.createParallelGroup()
										.addComponent(separator1, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
										.addComponent(btn_close, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
										.addComponent(txt_message, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
								.addContainerGap()));
		contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(txt_message, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(btn_close, GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
								.addContainerGap()));
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JButton btn_close;
	private JSeparator separator1;
	private JTextField txt_message;
}
