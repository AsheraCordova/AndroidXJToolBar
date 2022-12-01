package androidx.appcompat.widget;

import java.util.List;
import java.util.Map;

import androidx.appcompat.view.menu.MenuBuilder;
import r.android.graphics.drawable.Drawable;
import r.android.view.MenuItem;

public class MenuParser {
	public static void parseMenu(MenuBuilder menu, String json, com.ashera.core.IFragment fragment) {
		Map<String, Object> jsonMap = com.ashera.widget.PluginInvoker.unmarshal(json, java.util.Map.class);
		
		if (jsonMap.containsKey("menu")) {
			Map<String, Object> menuMap = com.ashera.widget.PluginInvoker.getMap(jsonMap.get("menu")); 
			if (menuMap.containsKey("item")) {
				Object item = menuMap.get("item");
				List<Object> itemList = com.ashera.widget.PluginInvoker.getList(item);
				if (itemList != null) {
					for (Object itemObj : itemList) {
						createMenuItem(menu, fragment, itemObj);
					}
				} else {
					createMenuItem(menu, fragment, item);
				}
			}
		}
	}

	private static void createMenuItem(MenuBuilder menu, com.ashera.core.IFragment fragment, Object payLoad) {
		Map<String, Object> itemMap = com.ashera.widget.PluginInvoker.getMap(payLoad);
		
		//"@android:id" : "@+id/menu_main_setting", "@android:icon" : "@drawable/icon", "@app:showAsAction" : "always", "@android:title" : "Setting"
		int id = 0;
		int groupId = 0;
		int categoryOrder = 0;
		String title = "";
		Drawable icon = null;
		int showAsAction = -1;
		if (itemMap.containsKey("@android:id")) {
			id = (int) com.ashera.widget.PluginInvoker.convertFrom(com.ashera.widget.PluginInvoker.getConverter("id"), 
					null, itemMap.get("@android:id"), fragment);
		}

		if (itemMap.containsKey("@android:title")) {
			title = (String) com.ashera.widget.PluginInvoker.convertFrom(com.ashera.widget.PluginInvoker.getConverter("resourcestring"), 
					null, itemMap.get("@android:title"), fragment);
		}
		
		if (itemMap.containsKey("@android:icon")) {
			icon = (Drawable) com.ashera.widget.PluginInvoker.convertFrom(com.ashera.widget.PluginInvoker.getConverter("drawable"), 
					null, itemMap.get("@android:icon"), fragment);
		}
		
		if (itemMap.containsKey("@app:showAsAction")) {
			showAsAction = (int) com.ashera.widget.PluginInvoker.convertFrom(com.ashera.widget.PluginInvoker.getConverter("androidx.appcompat.widget.ActionMenuView.showAsAction"), 
					null, itemMap.get("@app:showAsAction"), fragment);
		}
		
		MenuItem menuItem = menu.add(groupId, id, categoryOrder, title);
		
		if (icon != null) {
			menuItem.setIcon(icon);
		}
		if (showAsAction != -1) {
			menuItem.setShowAsAction(showAsAction);
		}
	}
}
