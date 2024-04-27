package androidx.appcompat.widget;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ashera.widget.IWidget;

import androidx.appcompat.view.menu.MenuBuilder;
import r.android.graphics.drawable.Drawable;
import r.android.view.MenuItem;
import r.android.view.View;

public class MenuParser {
	public static void parseMenu(com.ashera.widget.HasWidgets parent, MenuBuilder menu, String json, com.ashera.core.IFragment fragment) {
		Map<String, Object> jsonMap = com.ashera.widget.PluginInvoker.unmarshal(json, java.util.Map.class);
		
		if (jsonMap.containsKey("menu")) {
			Map<String, Object> menuMap = com.ashera.widget.PluginInvoker.getMap(jsonMap.get("menu"));
			parseGroupAndItem(parent, menu, fragment, menuMap, 0); 			
		}
	}

	private static void parseGroupAndItem(com.ashera.widget.HasWidgets parent, MenuBuilder menu,
			com.ashera.core.IFragment fragment, Map<String, Object> parentMap, int groupId) {
		for (Iterator<Entry<String, Object>> iterator = parentMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			Object item = entry.getValue();
			switch (entry.getKey()) {
			case "item":
				createMenuItemMapOrList(parent, menu, fragment, item, groupId);
				break;
			case "group":
				List<Object> itemList = com.ashera.widget.PluginInvoker.getList(item);
				if (itemList != null) {
					for (Object itemObj : itemList) {
						parseGroup(parent, menu, fragment, itemObj);
					}
				} else {
					parseGroup(parent, menu, fragment, item);
				}
				break;
			default:
				break;
			}
		}
	}

	private static void parseGroup(com.ashera.widget.HasWidgets parent, MenuBuilder menu,
			com.ashera.core.IFragment fragment, Object item) {
		Map<String, Object> itemMap = com.ashera.widget.PluginInvoker.getMap(item);
		int menugroupId = 0;
		if (itemMap.containsKey("@android:id")) {
			menugroupId = (int) com.ashera.widget.PluginInvoker.convertFrom(com.ashera.widget.PluginInvoker.getConverter("id"), 
					null, itemMap.get("@android:id"), fragment);
		}
		
		parseGroupAndItem(parent, menu, fragment, itemMap, menugroupId);
	}

	private static void createMenuItemMapOrList(com.ashera.widget.HasWidgets parent, MenuBuilder menu,
			com.ashera.core.IFragment fragment, Object item, int groupId) {
		List<Object> itemList = com.ashera.widget.PluginInvoker.getList(item);
		if (itemList != null) {
			for (Object itemObj : itemList) {
				createMenuItem(parent, menu, fragment, itemObj, groupId);
			}
		} else {
			createMenuItem(parent, menu, fragment, item, groupId);
		}
	}

	private static void createMenuItem(com.ashera.widget.HasWidgets parent, MenuBuilder menu, com.ashera.core.IFragment fragment, Object payLoad, int groupId) {
		Map<String, Object> itemMap = com.ashera.widget.PluginInvoker.getMap(payLoad);
		
		//"@android:id" : "@+id/menu_main_setting", "@android:icon" : "@drawable/icon", "@app:showAsAction" : "always", "@android:title" : "Setting"
		int id = 0;
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
		

		boolean actionViewSpecified = false;
        if (itemMap.containsKey("@app:actionViewClass")) {
            View actionView = ((View) com.ashera.widget.WidgetFactory.createWidget(
            		(String) itemMap.get("@app:actionViewClass"), "",  parent, false).asWidget());
            if (menuItem instanceof androidx.appcompat.view.menu.SupportMenuItem) {
            	((androidx.appcompat.view.menu.SupportMenuItem)menuItem).setActionView(actionView);
            }
            actionViewSpecified = true;
        }
        
        if (itemMap.containsKey("@app:actionLayout")) {
        	String actionLayout = (String) itemMap.get("@app:actionLayout");
			createActionLayout(parent, menuItem, actionLayout);
        }
        
        if (itemMap.containsKey("@android:actionLayout")) {
        	String actionLayout = (String) itemMap.get("@android:actionLayout");
			createActionLayout(parent, menuItem, actionLayout);
        }
		
		if (icon != null) {
			menuItem.setIcon(icon);
		}
		if (showAsAction != -1) {
			menuItem.setShowAsAction(showAsAction);
		}
	}

	private static void createActionLayout(com.ashera.widget.HasWidgets parent, MenuItem menuItem,
			String actionLayout) {
		IWidget template = (IWidget) parent.quickConvert(actionLayout, "template");
		IWidget widget = template.loadLazyWidgets(parent);
		if (menuItem instanceof androidx.appcompat.view.menu.SupportMenuItem) {
			((androidx.appcompat.view.menu.SupportMenuItem)menuItem).setActionView((View) widget.asWidget());
		}
	}
}
