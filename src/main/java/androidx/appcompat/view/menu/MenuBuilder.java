package androidx.appcompat.view.menu;
import r.android.content.res.Resources;
import r.android.graphics.drawable.Drawable;
import r.android.view.MenuItem;
import r.android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
public class MenuBuilder {
  private static final int[] sCategoryToOrder=new int[]{1,4,5,3,2,0};
  private final Resources mResources;
  private boolean mQwertyMode;
  private boolean mShortcutsVisible;
  private ArrayList<MenuItemImpl> mItems;
  private ArrayList<MenuItemImpl> mVisibleItems;
  private boolean mIsVisibleItemsStale;
  private ArrayList<MenuItemImpl> mActionItems;
  private ArrayList<MenuItemImpl> mNonActionItems;
  private boolean mIsActionItemsStale;
  private int mDefaultShowAsAction=SupportMenuItem.SHOW_AS_ACTION_NEVER;
  CharSequence mHeaderTitle;
  Drawable mHeaderIcon;
  View mHeaderView;
  private boolean mPreventDispatchingItemsChanged=false;
  private boolean mItemsChangedWhileDispatchPrevented=false;
  private boolean mStructureChangedWhileDispatchPrevented=false;
  private boolean mOptionalIconsVisible=false;
  private boolean mIsClosing=false;
  private CopyOnWriteArrayList<WeakReference<MenuPresenter>> mPresenters=new CopyOnWriteArrayList<WeakReference<MenuPresenter>>();
  private MenuItemImpl mExpandedItem;
  private boolean mGroupDividerEnabled=false;
  private boolean mOverrideVisibleItems;
  protected MenuItem addInternal(  int group,  int id,  int categoryOrder,  CharSequence title){
    final int ordering=getOrdering(categoryOrder);
    final MenuItemImpl item=createNewMenuItem(group,id,categoryOrder,ordering,title,mDefaultShowAsAction);
    if (false) {
      //item.setMenuInfo(mCurrentMenuInfo);
    }
    mItems.add(findInsertIndex(mItems,ordering),item);
    onItemsChanged(true);
    return item;
  }
  private MenuItemImpl createNewMenuItem(  int group,  int id,  int categoryOrder,  int ordering,  CharSequence title,  int defaultShowAsAction){
    return new MenuItemImpl(this,group,id,categoryOrder,ordering,title,defaultShowAsAction);
  }
  public MenuItem add(  CharSequence title){
    return addInternal(0,0,0,title);
  }
  public MenuItem add(  int titleRes){
    return addInternal(0,0,0,mResources.getString(titleRes));
  }
  public MenuItem add(  int group,  int id,  int categoryOrder,  CharSequence title){
    return addInternal(group,id,categoryOrder,title);
  }
  public MenuItem add(  int group,  int id,  int categoryOrder,  int title){
    return addInternal(group,id,categoryOrder,mResources.getString(title));
  }
  public void clearAll(){
    mPreventDispatchingItemsChanged=true;
    clear();
    clearHeader();
    mPresenters.clear();
    mPreventDispatchingItemsChanged=false;
    mItemsChangedWhileDispatchPrevented=false;
    mStructureChangedWhileDispatchPrevented=false;
    onItemsChanged(true);
  }
  public void clear(){
    if (mExpandedItem != null) {
      collapseItemActionView(mExpandedItem);
    }
    mItems.clear();
    onItemsChanged(true);
  }
  private static int getOrdering(  int categoryOrder){
    final int index=(categoryOrder & CATEGORY_MASK) >> CATEGORY_SHIFT;
    if (index < 0 || index >= sCategoryToOrder.length) {
      throw new IllegalArgumentException("order does not contain a valid category.");
    }
    return (sCategoryToOrder[index] << CATEGORY_SHIFT) | (categoryOrder & USER_MASK);
  }
  private static int findInsertIndex(  ArrayList<MenuItemImpl> items,  int ordering){
    for (int i=items.size() - 1; i >= 0; i--) {
      MenuItemImpl item=items.get(i);
      if (item.getOrdering() <= ordering) {
        return i + 1;
      }
    }
    return 0;
  }
  public ArrayList<MenuItemImpl> getVisibleItems(){
    if (!mIsVisibleItemsStale)     return mVisibleItems;
    mVisibleItems.clear();
    final int itemsSize=mItems.size();
    MenuItemImpl item;
    for (int i=0; i < itemsSize; i++) {
      item=mItems.get(i);
      if (item.isVisible())       mVisibleItems.add(item);
    }
    mIsVisibleItemsStale=false;
    mIsActionItemsStale=true;
    return mVisibleItems;
  }
  public void flagActionItems(){
    final ArrayList<MenuItemImpl> visibleItems=getVisibleItems();
    if (!mIsActionItemsStale) {
      return;
    }
    boolean flagged=false;
    for (    WeakReference<MenuPresenter> ref : mPresenters) {
      final MenuPresenter presenter=ref.get();
      if (presenter == null) {
        mPresenters.remove(ref);
      }
 else {
        flagged|=presenter.flagActionItems();
      }
    }
    if (flagged) {
      mActionItems.clear();
      mNonActionItems.clear();
      final int itemsSize=visibleItems.size();
      for (int i=0; i < itemsSize; i++) {
        MenuItemImpl item=visibleItems.get(i);
        if (item.isActionButton()) {
          mActionItems.add(item);
        }
 else {
          mNonActionItems.add(item);
        }
      }
    }
 else {
      mActionItems.clear();
      mNonActionItems.clear();
      mNonActionItems.addAll(getVisibleItems());
    }
    mIsActionItemsStale=false;
  }
  public ArrayList<MenuItemImpl> getActionItems(){
    flagActionItems();
    return mActionItems;
  }
  public ArrayList<MenuItemImpl> getNonActionItems(){
    flagActionItems();
    return mNonActionItems;
  }
  public void clearHeader(){
    mHeaderIcon=null;
    mHeaderTitle=null;
    mHeaderView=null;
    onItemsChanged(false);
  }
  final static int USER_MASK=65535;
  final static int USER_SHIFT=0;
  final static int CATEGORY_MASK=-65536;
  final static int CATEGORY_SHIFT=16;
  final static int SUPPORTED_MODIFIERS_MASK=69647;
  final static int FLAG_KEEP_OPEN_ON_SUBMENU_OPENED=4;
  public MenuBuilder(){
    mResources=null;
    mItems=new ArrayList<MenuItemImpl>();
    mVisibleItems=new ArrayList<MenuItemImpl>();
    mActionItems=new ArrayList<MenuItemImpl>();
    mNonActionItems=new ArrayList<MenuItemImpl>();
    this.mIsVisibleItemsStale=true;
  }
  private void collapseItemActionView(  MenuItem mExpandedItem){
  }
  public void setActionMenuPresenter(  MenuPresenter mPresenter){
    mPresenters.add(new WeakReference<MenuPresenter>(mPresenter));
  }
  public void onItemActionRequestChanged(  MenuItem menuItem){
  }
  public void onItemsChanged(  boolean changed){
  }
interface SupportMenuItem extends MenuItem {
  }
class ContextMenu {
  }
}
