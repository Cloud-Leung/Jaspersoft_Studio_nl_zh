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

		// 验证服务器配置
		String url = Activator.getDefault().getPreferenceStore()
				.getString(WorkbenchPreferencePage.URL_KEY);
		String user = Activator.getDefault().getPreferenceStore()
				.getString(WorkbenchPreferencePage.USERNAME_KEY);
		String password = Activator.getDefault().getPreferenceStore()
				.getString(WorkbenchPreferencePage.PASSWORD_KEY);
		if (!this.checkConfig(url, user, password)) {
			String errorInfo = "请配置模板服务器: 窗口->首选项->Jaspersoft Studio->模板发布服务器!";
			MessageDialog.openError(window.getShell(), "模板发布", errorInfo);
			ConsoleFactory.printError("错误: " + errorInfo, true);
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
			ConsoleFactory.printError("错误: 请打开模板文件.", true);
			return;
		}
		IEditorInput editorInput = ieditorPart.getEditorInput();
		if (!editorInput.exists()) {
			ConsoleFactory.printError("错误: 请打开模板文件.", true);
			return;
		}
		Object file = editorInput.getAdapter(IFile.class);
		if (file instanceof IFile) {
			publishTemplate(file, url, user, password);
		} else {
			ConsoleFactory.printError("错误: 当前打开的内容不是一个模板文件.", true);
		}
	}

	private void publishTemplate(Object object, String url, String user,
			String password) {
		IFile file = (IFile) object;
		java.io.File templateFile = file.getLocation().toFile();
		if (!templateFile.getName().endsWith(".jrxml")) {
			ConsoleFactory.printError("错误: 当前打开的内容不是一个模板文件.", true);
			return;
		}
		try {

			ConsoleFactory.printToConsole(
					"信息: 开始发布模板...." + templateFile.getName(), true);
			String result = HttpUtils.uploadFile(url,
					Base64.encode((user + ":" + password).getBytes()),
					templateFile);
			ConsoleFactory.printToConsole("信息: 模板发布请求完成....", true);
			JSONObject resultObject = JSON.parseObject(result);
			if (resultObject.getInteger("resultCode") == 1) {
				ConsoleFactory.printToConsole("信息: 模板发布成功, 上传后的模板名为:"
						+ resultObject.getString("data"), true);
			} else {
				ConsoleFactory.printError(
						"错误: 模板发布失败, " + resultObject.getString("message"),
						true);
			}
		} catch (Exception e) {
			ConsoleFactory.printToConsole(e, true);
		}
		return;
	}

	private void publishTemplateBySelection(IWorkbenchWindow window,
			String url, String user, String password) {
		// 获取被选中对象
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection)
					.getFirstElement();
			IFile file = (IFile) object;
			publishTemplate(file, url, user, password);
			return;
		}
		ConsoleFactory.printError("错误: 当前选中的内容不是一个模板文件.", true);
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
