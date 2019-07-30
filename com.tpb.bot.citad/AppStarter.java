package com.tpb.bot.citad;

import javax.swing.SwingUtilities;
import com.tpb.bot.citad.ui.BotUI;

public class AppStarter {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new BotUI();
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
		});
	}
}
