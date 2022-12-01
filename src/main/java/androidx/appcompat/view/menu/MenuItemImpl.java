package androidx.appcompat.view.menu;
import r.android.content.res.ColorStateList;
import r.android.graphics.drawable.Drawable;
import r.android.view.MenuItem;
import r.android.view.View;
import r.android.widget.LinearLayout;
public final class MenuItemImpl implements MenuItem {
  private static final int SHOW_AS_ACTION_MASK=SHOW_AS_ACTION_NEVER | SHOW_AS_ACTION_IF_ROOM | SHOW_AS_ACTION_ALWAYS;
  private final int mId;
  private final int mGroup;
  private final int mCategoryOrder;
  private final int mOrdering;
  private CharSequence mTitle;
  private Drawable mIconDrawable;
  private int mIconResId=NO_ICON;
  MenuBuilder mMenu;
  private ColorStateList mIconTintList=null;
  private boolean mHasIconTint=false;
  private boolean mHasIconTintMode=false;
  private boolean mNeedToApplyIconTint=false;
  private int mFlags=ENABLED;
  private static final int CHECKABLE=0x00000001;
  private static final int CHECKED=0x00000002;
  private static final int EXCLUSIVE=0x00000004;
  private static final int HIDDEN=0x00000008;
  private static final int ENABLED=0x00000010;
  private static final int IS_ACTION=0x00000020;
  private int mShowAsAction=SHOW_AS_ACTION_NEVER;
  private boolean mIsActionViewExpanded=false;
  static final int NO_ICON=0;
  MenuItemImpl(  MenuBuilder menu,  int group,  int id,  int categoryOrder,  int ordering,  CharSequence title,  int showAsAction){
    mMenu=menu;
    mId=id;
    mGroup=group;
    mCategoryOrder=categoryOrder;
    mOrdering=ordering;
    mTitle=title;
    mShowAsAction=showAsAction;
  }
  public int getGroupId(){
    return mGroup;
  }
  public int getItemId(){
    return mId;
  }
  public int getOrdering(){
    return mOrdering;
  }
  public MenuItem setIcon(  Drawable icon){
    mIconResId=NO_ICON;
    mIconDrawable=icon;
    mNeedToApplyIconTint=true;
    mMenu.onItemsChanged(false);
    return this;
  }
  public MenuItem setIcon(  int iconResId){
    mIconDrawable=null;
    mIconResId=iconResId;
    mNeedToApplyIconTint=true;
    mMenu.onItemsChanged(false);
    return this;
  }
  public MenuItem setIconTintList(  ColorStateList iconTintList){
    mIconTintList=iconTintList;
    mHasIconTint=true;
    mNeedToApplyIconTint=true;
    mMenu.onItemsChanged(false);
    return this;
  }
  public boolean isVisible(){
    if (mActionProvider != null && mActionProvider.overridesItemVisibility()) {
      return (mFlags & HIDDEN) == 0 && mActionProvider.isVisible();
    }
    return (mFlags & HIDDEN) == 0;
  }
  public boolean isActionButton(){
    return (mFlags & IS_ACTION) == IS_ACTION;
  }
  public boolean requestsActionButton(){
    return (mShowAsAction & SHOW_AS_ACTION_IF_ROOM) == SHOW_AS_ACTION_IF_ROOM;
  }
  public boolean requiresActionButton(){
    return (mShowAsAction & SHOW_AS_ACTION_ALWAYS) == SHOW_AS_ACTION_ALWAYS;
  }
  public void setIsActionButton(  boolean isActionButton){
    if (isActionButton) {
      mFlags|=IS_ACTION;
    }
 else {
      mFlags&=~IS_ACTION;
    }
  }
  public void setShowAsAction(  int actionEnum){
switch (actionEnum & SHOW_AS_ACTION_MASK) {
case SHOW_AS_ACTION_ALWAYS:
case SHOW_AS_ACTION_IF_ROOM:
case SHOW_AS_ACTION_NEVER:
      break;
default :
    throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM," + " and SHOW_AS_ACTION_NEVER are mutually exclusive.");
}
mShowAsAction=actionEnum;
mMenu.onItemActionRequestChanged(this);
}
public View getActionView(){
if (mActionView != null) {
  return mActionView;
}
 else if (mActionProvider != null) {
  mActionView=mActionProvider.onCreateActionView(this);
  return mActionView;
}
 else {
  return null;
}
}
public boolean hasCollapsibleActionView(){
if ((mShowAsAction & SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW) != 0) {
  if (mActionView == null && mActionProvider != null) {
    mActionView=mActionProvider.onCreateActionView(this);
  }
  return mActionView != null;
}
return false;
}
public boolean isActionViewExpanded(){
return mIsActionViewExpanded;
}
private r.android.view.View mActionView;
private ActionProvider mActionProvider;
public String getTitle(){
return (String)mTitle;
}
public Drawable getIcon(){
return mIconDrawable;
}
interface ActionProvider {
boolean overridesItemVisibility();
View onCreateActionView(MenuItemImpl menuItemImpl);
boolean isVisible();
}
}
