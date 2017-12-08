package com.jaspersoft.studio.extension.nestpublish.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleFactory implements IConsoleFactory {

	private static MessageConsole msgConsole = new MessageConsole(
			"模板发布信息", null);
	private static boolean exists = false;

	public void openConsole() {
		showConsole();
	}

	/**
	 *
	 */
	private static void showConsole() {
		if (msgConsole == null) {
			return;
		}

		IConsoleManager consoleMgr = ConsolePlugin.getDefault()
				.getConsoleManager();

		IConsole[] existing = consoleMgr.getConsoles();

		for (IConsole console : existing) {
			if (msgConsole == console) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			consoleMgr.addConsoles(new IConsole[] { msgConsole });
		}
	}

	public static void closeConsole() {
		if (msgConsole == null) {
			return;
		}

		IConsoleManager consoleMgr = ConsolePlugin.getDefault()
				.getConsoleManager();
		consoleMgr.removeConsoles(new IConsole[] { msgConsole });
	}

	public static MessageConsole getConsole() {
		showConsole();

		return msgConsole;
	}

	public static void printToConsole(String message, boolean activate) {
		showConsole();
		MessageConsoleStream printer = ConsoleFactory.getConsole()
				.newMessageStream();
		printer.setActivateOnWrite(activate);
		printer.println(message);
	}
	
	public static void printError(String message, boolean activate) {
		showConsole();
		MessageConsoleStream printer = ConsoleFactory.getConsole()
				.newMessageStream();
		printer.setActivateOnWrite(activate);
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		printer.setColor(color);
		printer.println(message);
	}
	
	public static void printToConsole(Exception e, boolean activate) {
		showConsole();
		MessageConsoleStream printer = ConsoleFactory.getConsole()
				.newMessageStream();
		printer.setActivateOnWrite(activate);
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		printer.setColor(color);
		printer.println("请求错误:" + e.getMessage());
		StackTraceElement[] trace = e.getStackTrace();
		for (StackTraceElement traceElement : trace)
			printer.println("\tat " + traceElement);
	}

}
