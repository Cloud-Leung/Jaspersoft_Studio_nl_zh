package com.jaspersoft.studio.extension.nestpublish;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkbenchPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, ModifyListener {

	public static final String URL_KEY = "$PUBLISH_URL_KEY";
	public static final String USERNAME_KEY = "$PUBLISH_USERNAME_KEY";
	public static final String PASSWORD_KEY = "$PUBLISH_PASSWORD_KEY";
	// 为文本框值定义三个默认值
	public static final String URL_DEFAULT = "http://xxxx/xxx";
	public static final String USERNAME_DEFAULT = "user";
	public static final String PASSWORD_DEFAULT = "password";
	// 定义三个文本框
	private Text urlText, usernameText, passwordText;
	// 定义一个IPreferenceStore对象
	private IPreferenceStore ps;

	public WorkbenchPreferencePage() {
	}

	public WorkbenchPreferencePage(String title) {
		super(title);
	}

	public WorkbenchPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	protected Control createContents(Composite parent) {
		Composite topComp = new Composite(parent, SWT.NONE);
		topComp.setLayout(new GridLayout(2, false));
		
		// 创建三个文本框及其标签
		new Label(topComp, SWT.NONE).setText("服务器地址");
		urlText = new Text(topComp, SWT.BORDER);
		urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(topComp, SWT.NONE).setText("用户名");
		usernameText = new Text(topComp, SWT.BORDER);
		usernameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(topComp, SWT.NONE).setText("密码");
		passwordText = new Text(topComp, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// 取出以前保存的值，并设置到文本框中。如果取出值为空值或空字串，则填入默认值。
		ps = getPreferenceStore();// 取得一个IPreferenceStore对象
		String url = ps.getString(URL_KEY);
		if (url == null || url.trim().equals(""))
			urlText.setText(URL_DEFAULT);
		else
			urlText.setText(url);

		String username = ps.getString(USERNAME_KEY);
		if (username == null || username.trim().equals(""))
			usernameText.setText(USERNAME_DEFAULT);
		else
			usernameText.setText(username);

		String password = ps.getString(PASSWORD_KEY);
		if (password == null || password.trim().equals(""))
			passwordText.setText(PASSWORD_DEFAULT);
		else
			passwordText.setText(password);

		// 添加事件监听器。this代表本类，因为本类实现了ModifyListener接口成了监听器
		usernameText.addModifyListener(this);
		passwordText.addModifyListener(this);
		urlText.addModifyListener(this);
		return topComp;
	}

	public void modifyText(ModifyEvent e) {
		// TODO Auto-generated method stub
	}

	// 父类方法。单击“复原默认值”按钮时将执行此方法，取出默认值设置到文本框中
	protected void performDefaults() {
		urlText.setText(URL_DEFAULT);
		usernameText.setText(USERNAME_DEFAULT);
		passwordText.setText(PASSWORD_DEFAULT);
	}

	// 父类方法。单击“应用”按钮时执行此方法，将文本框值保存并弹出成功的提示信息
	protected void performApply() {
		doSave(); // 自定义方法，保存设置
		MessageDialog.openInformation(getShell(), "信息", "成功保存修改!");
	}

	// 父类方法。单击“确定”按钮时执行此方法，将文本框值保存并弹出成功的提示信息
	public boolean performOk() {
		doSave();
		// MessageDialog.openInformation(getShell(), "信息", "修改在下次启动生效");
		return true; // true表示成功退出
	}

	// 自定义方法。保存文本框的值
	private void doSave() {
		ps.setValue(URL_KEY, urlText.getText());
		ps.setValue(USERNAME_KEY, usernameText.getText());
		ps.setValue(PASSWORD_KEY, passwordText.getText());
	}

}
