package androidx.appcompat.view.menu;
import r.android.content.Context;
import r.android.view.View;
import r.android.view.ViewGroup;
import java.util.ArrayList;
public abstract class BaseMenuPresenter implements MenuPresenter {
  protected Context mSystemContext;
  protected MenuBuilder mMenu;
  private int mMenuLayoutRes;
  private int mItemLayoutRes;
  private int mId;
  public void updateMenuView(  boolean cleared){
    final ViewGroup parent=(ViewGroup)mMenuView;
    if (parent == null)     return;
    int childIndex=0;
    if (mMenu != null) {
      mMenu.flagActionItems();
      ArrayList<MenuItemImpl> visibleItems=mMenu.getVisibleItems();
      final int itemCount=visibleItems.size();
      for (int i=0; i < itemCount; i++) {
        MenuItemImpl item=visibleItems.get(i);
        if (shouldIncludeItem(childIndex,item)) {
          final View convertView=parent.getChildAt(childIndex);
          final MenuItemImpl oldItem=convertView instanceof MenuView.ItemView ? ((MenuView.ItemView)convertView).getItemData() : null;
          final View itemView=getItemView(item,convertView,parent);
          if (item != oldItem) {
            itemView.setPressed(false);
            itemView.jumpDrawablesToCurrentState();
          }
          if (itemView != convertView) {
            addItemView(itemView,childIndex);
          }
          childIndex++;
        }
      }
    }
    while (childIndex < parent.getChildCount()) {
      if (!filterLeftoverView(parent,childIndex)) {
        childIndex++;
      }
    }
  }
  protected void addItemView(  View itemView,  int childIndex){
    final ViewGroup currentParent=(ViewGroup)itemView.getParent();
    if (currentParent != null) {
      currentParent.removeView(itemView);
    }
    ((ViewGroup)mMenuView).addView(itemView,childIndex);
  }
  protected boolean filterLeftoverView(  ViewGroup parent,  int childIndex){
    parent.removeViewAt(childIndex);
    return true;
  }
  public boolean shouldIncludeItem(  int childIndex,  MenuItemImpl item){
    return true;
  }
  public boolean flagActionItems(){
    return false;
  }
  protected androidx.appcompat.widget.ActionMenuView mMenuView;
  public View getItemView(  final MenuItemImpl item,  View convertView,  ViewGroup parent){
    View itemView=null;
    if (convertView != null && androidx.appcompat.widget.ActionMenuView.isActionMenuItemView(convertView)) {
      itemView=convertView;
    }
    final androidx.appcompat.widget.ActionMenuView menuParent=(androidx.appcompat.widget.ActionMenuView)parent;
    itemView=menuParent.getItemView(item);
    return itemView;
  }
  public void setMenu(  MenuBuilder mMenu){
    this.mMenu=mMenu;
  }
interface MenuView {
interface ItemView {
      MenuItemImpl getItemData();
    }
  }
}
