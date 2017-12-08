package com.jaspersoft.studio.extension.nestpublish.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jaspersoft.studio.extension.nestpublish.Activator;
import com.jaspersoft.studio.extension.nestpublish.WorkbenchPreferencePage;
import com.jaspersoft.studio.extension.nestpublish.console.ConsoleFactory;
import com.jaspersoft.studio.extension.nestpublish.util.Base64;
import com.jaspersoft.studio.extension.nestpublish.util.HttpUtils;

/**
 * 
 * Template Publish Handler
 * 
 * @author Cloud Leung
 * 
 */
public class PublishHandler extends AbstractHandler {

	public PublishHandler() {

	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		// ��֤����������
		String url = Activator.getDefault().getPreferenceStore()
				.getString(WorkbenchPreferencePage.URL_KEY);
		String user = Activator.getDefault().getPreferenceStore()
				.getString(WorkbenchPreferencePage.USERNAME_KEY);
		String password = Activator.getDefault().getPreferenceStore()
				.getString(WorkbenchPreferencePage.PASSWORD_KEY);
		if (!this.checkConfig(url, user, password)) {
			String errorInfo = "������ģ�������: ����->��ѡ��->Jaspersoft Studio->ģ�巢��������!";
			MessageDialog.openError(window.getShell(), "ģ�巢��", errorInfo);
			ConsoleFactory.printError("����: " + errorInfo, true);
			return null;
		}
		try {
			publishTemplateByEditor(window, url, user, password);
		} catch (Exception e) {
			ConsoleFactory.printToConsole(e, true);
		}

		return null;
	}

	private void publishTemplateByEditor(IWorkbenchWindow window, String url,
			String user, String password) {
		IEditorPart ieditorPart = window.getActivePage().getActiveEditor();
		if (null == ieditorPart) {
			ConsoleFactory.printError("����: ���ģ���ļ�.", true);
			return;
		}
		IEditorInput editorInput = ieditorPart.getEditorInput();
		if (!editorInput.exists()) {
			ConsoleFactory.printError("����: ���ģ���ļ�.", true);
			return;
		}
		Object file = editorInput.getAdapter(IFile.class);
		if (file instanceof IFile) {
			publishTemplate(file, url, user, password);
		} else {
			ConsoleFactory.printError("����: ��ǰ�򿪵����ݲ���һ��ģ���ļ�.", true);
		}
	}

	private void publishTemplate(Object object, String url, String user,
			String password) {
		IFile file = (IFile) object;
		java.io.File templateFile = file.getLocation().toFile();
		if (!templateFile.getName().endsWith(".jrxml")) {
			ConsoleFactory.printError("����: ��ǰ�򿪵����ݲ���һ��ģ���ļ�.", true);
			return;
		}
		try {

			ConsoleFactory.printToConsole(
					"��Ϣ: ��ʼ����ģ��...." + templateFile.getName(), true);
			String result = HttpUtils.uploadFile(url,
					Base64.encode((user + ":" + password).getBytes()),
					templateFile);
			ConsoleFactory.printToConsole("��Ϣ: ģ�巢���������....", true);
			JSONObject resultObject = JSON.parseObject(result);
			if (resultObject.getInteger("resultCode") == 1) {
				ConsoleFactory.printToConsole("��Ϣ: ģ�巢���ɹ�, �ϴ����ģ����Ϊ:"
						+ resultObject.getString("data"), true);
			} else {
				ConsoleFactory.printError(
						"����: ģ�巢��ʧ��, " + resultObject.getString("message"),
						true);
			}
		} catch (Exception e) {
			ConsoleFactory.printToConsole(e, true);
		}
		return;
	}

	private void publishTemplateBySelection(IWorkbenchWindow window,
			String url, String user, String password) {
		// ��ȡ��ѡ�ж���
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection)
					.getFirstElement();
			IFile file = (IFile) object;
			publishTemplate(file, url, user, password);
			return;
		}
		ConsoleFactory.printError("����: ��ǰѡ�е����ݲ���һ��ģ���ļ�.", true);
		return;
	}

	private boolean checkConfig(String url, String user, String password) {
		if (null == url || "".equals(url.trim())) {
			return false;
		}
		if (null == user || "".equals(user.trim())) {
			return false;
		}
		if (null == password || "".equals(password.trim())) {
			return false;
		}
		return true;
	}

}
