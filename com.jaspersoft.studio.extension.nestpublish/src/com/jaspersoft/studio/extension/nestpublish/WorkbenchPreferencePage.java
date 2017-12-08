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
	// Ϊ�ı���ֵ��������Ĭ��ֵ
	public static final String URL_DEFAULT = "http://xxxx/xxx";
	public static final String USERNAME_DEFAULT = "user";
	public static final String PASSWORD_DEFAULT = "password";
	// ���������ı���
	private Text urlText, usernameText, passwordText;
	// ����һ��IPreferenceStore����
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
		
		// ���������ı������ǩ
		new Label(topComp, SWT.NONE).setText("��������ַ");
		urlText = new Text(topComp, SWT.BORDER);
		urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(topComp, SWT.NONE).setText("�û���");
		usernameText = new Text(topComp, SWT.BORDER);
		usernameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(topComp, SWT.NONE).setText("����");
		passwordText = new Text(topComp, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// ȡ����ǰ�����ֵ�������õ��ı����С����ȡ��ֵΪ��ֵ����ִ���������Ĭ��ֵ��
		ps = getPreferenceStore();// ȡ��һ��IPreferenceStore����
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

		// ����¼���������this�����࣬��Ϊ����ʵ����ModifyListener�ӿڳ��˼�����
		usernameText.addModifyListener(this);
		passwordText.addModifyListener(this);
		urlText.addModifyListener(this);
		return topComp;
	}

	public void modifyText(ModifyEvent e) {
		// TODO Auto-generated method stub
	}

	// ���෽������������ԭĬ��ֵ����ťʱ��ִ�д˷�����ȡ��Ĭ��ֵ���õ��ı�����
	protected void performDefaults() {
		urlText.setText(URL_DEFAULT);
		usernameText.setText(USERNAME_DEFAULT);
		passwordText.setText(PASSWORD_DEFAULT);
	}

	// ���෽����������Ӧ�á���ťʱִ�д˷��������ı���ֵ���沢�����ɹ�����ʾ��Ϣ
	protected void performApply() {
		doSave(); // �Զ��巽������������
		MessageDialog.openInformation(getShell(), "��Ϣ", "�ɹ������޸�!");
	}

	// ���෽����������ȷ������ťʱִ�д˷��������ı���ֵ���沢�����ɹ�����ʾ��Ϣ
	public boolean performOk() {
		doSave();
		// MessageDialog.openInformation(getShell(), "��Ϣ", "�޸����´�������Ч");
		return true; // true��ʾ�ɹ��˳�
	}

	// �Զ��巽���������ı����ֵ
	private void doSave() {
		ps.setValue(URL_KEY, urlText.getText());
		ps.setValue(USERNAME_KEY, usernameText.getText());
		ps.setValue(PASSWORD_KEY, passwordText.getText());
	}

}
