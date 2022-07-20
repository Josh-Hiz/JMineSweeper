package Minesweeper;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MineSweeper extends JFrame {
	
	private JLabel status;
	
	public MineSweeper() {
		
		initUI();
	}
	
	private void initUI() {
		
		status = new JLabel("");
		add(status, BorderLayout.SOUTH);
		
		add(new Board(status));
		
		setResizable(false);
		pack();
		
		setTitle("MineSweeper");
		setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void main(String[] args) { //MAIN
		
		EventQueue.invokeLater(() -> {
			
			var Miner = new MineSweeper();
			 Miner.setVisible(true);
			
		});
	}
		
	

}
