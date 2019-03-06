package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class AwakeBot {
	public static void start()	{
		AwakeBotMain.lblProgramIsWorking.setText("Program is working :D");
		Thread clickThread = new Thread()	{
			public void run()	{
				try {
					System.out.println("Started");
					Robot robot = new Robot();
					while (!(AwakeBotMain.exited))	{
						Thread.sleep(59999);
						robot.keyPress(KeyEvent.VK_F15);
						System.out.println("F15 pressed");
					}
				} catch (AWTException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		};
		clickThread.start();
	}
}