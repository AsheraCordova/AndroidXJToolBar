package androidx.appcompat.widget;
import r.android.graphics.drawable.Drawable;
import r.android.util.SparseBooleanArray;
import r.android.view.View;
import r.android.view.View.MeasureSpec;
import r.android.view.ViewGroup;
import androidx.appcompat.view.menu.BaseMenuPresenter;
import androidx.appcompat.view.menu.MenuItemImpl;
import java.util.ArrayList;
class ActionMenuPresenter extends BaseMenuPresenter {
  private boolean mPendingOverflowIconSet;
  private boolean mReserveOverflow;
  private boolean mReserveOverflowSet;
  private int mWidthLimit;
  private int mActionItemWidthLimit;
  private int mMaxItems;
  private boolean mMaxItemsSet;
  private boolean mStrictWidthLimit;
  private boolean mWidthLimitSet;
  private boolean mExpandedActionViewsExclusive;
  private int mMinCellSize;
  private final SparseBooleanArray mActionButtonGroups=new SparseBooleanArray();
  int mOpenSubMenuId;
  public void setReserveOverflow(  boolean reserveOverflow){
    mReserveOverflow=reserveOverflow;
    mReserveOverflowSet=true;
  }
  public View getItemView(  final MenuItemImpl item,  View convertView,  ViewGroup parent){
    View actionView=item.getActionView();
    if (actionView == null || item.hasCollapsibleActionView()) {
      actionView=super.getItemView(item,convertView,parent);
    }
    actionView.setVisibility(item.isActionViewExpanded() ? View.GONE : View.VISIBLE);
    final ActionMenuView menuParent=(ActionMenuView)parent;
    final ViewGroup.LayoutParams lp=actionView.getLayoutParams();
    if (!menuParent.checkLayoutParams(lp)) {
      actionView.setLayoutParams(menuParent.generateLayoutParams(lp));
    }
    return actionView;
  }
  public boolean shouldIncludeItem(  int childIndex,  MenuItemImpl item){
    return item.isActionButton();
  }
  public void updateMenuView(  boolean cleared){
    super.updateMenuView(cleared);
    ((View)mMenuView).requestLayout();
    if (mMenu != null) {
      final ArrayList<MenuItemImpl> actionItems=mMenu.getActionItems();
      final int count=actionItems.size();
      for (int i=0; i < count; i++) {
        final ActionProvider provider=null;//actionItems.get(i).getSupportActionProvider();
        if (provider != null) {
          provider.setSubUiVisibilityListener(this);
        }
      }
    }
    final ArrayList<MenuItemImpl> nonActionItems=mMenu != null ? mMenu.getNonActionItems() : null;
    boolean hasOverflow=false;
    if (mReserveOverflow && nonActionItems != null) {
      final int count=nonActionItems.size();
      if (count == 1) {
        hasOverflow=!nonActionItems.get(0).isActionViewExpanded();
      }
 else {
        hasOverflow=count > 0;
      }
    }
    if (!hasOverflow && mOverflowButton != null) {mOverflowButton.setVisibility(View.INVISIBLE);}if (hasOverflow) {
      if (mOverflowButton == null) {
        mOverflowButton=mMenuView.getOverFlowButton();//mOverflowButton=new OverflowMenuButton(mSystemContext);
      }
      ViewGroup parent=(ViewGroup)mOverflowButton.getParent();
      if (parent != mMenuView) {
        if (parent != null) {
          parent.removeView(mOverflowButton);
        }
        ActionMenuView menuView=(ActionMenuView)mMenuView;
        menuView.addView(mOverflowButton,menuView.generateOverflowButtonLayoutParams());
      }
    }
 else     if (mOverflowButton != null && mOverflowButton.getParent() == mMenuView) {
      ((ViewGroup)mMenuView).removeView(mOverflowButton);
    }
    ((ActionMenuView)mMenuView).setOverflowReserved(mReserveOverflow);
  }
  public boolean flagActionItems(){
    final ArrayList<MenuItemImpl> visibleItems;
    final int itemsSize;
    if (mMenu != null) {
      visibleItems=mMenu.getVisibleItems();
      itemsSize=visibleItems.size();
    }
 else {
      visibleItems=null;
      itemsSize=0;
    }
    int maxActions=mMaxItems;
    int widthLimit=mActionItemWidthLimit;
    final int querySpec=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
    final ViewGroup parent=(ViewGroup)mMenuView;
    int requiredItems=0;
    int requestedItems=0;
    int firstActionWidth=0;
    boolean hasOverflow=false;
    for (int i=0; i < itemsSize; i++) {
      MenuItemImpl item=visibleItems.get(i);
      if (item.requiresActionButton()) {
        requiredItems++;
      }
 else       if (item.requestsActionButton()) {
        requestedItems++;
      }
 else {
        hasOverflow=true;
      }
      if (mExpandedActionViewsExclusive && item.isActionViewExpanded()) {
        maxActions=0;
      }
    }
    if (mReserveOverflow && (hasOverflow || requiredItems + requestedItems > maxActions)) {
      maxActions--;
    }
    maxActions-=requiredItems;
    final SparseBooleanArray seenGroups=mActionButtonGroups;
    seenGroups.clear();
    int cellSize=0;
    int cellsRemaining=0;
    if (mStrictWidthLimit) {
      cellsRemaining=widthLimit / mMinCellSize;
      final int cellSizeRemaining=widthLimit % mMinCellSize;
      cellSize=mMinCellSize + cellSizeRemaining / cellsRemaining;
    }
    for (int i=0; i < itemsSize; i++) {
      MenuItemImpl item=visibleItems.get(i);
      if (item.requiresActionButton()) {
        View v=getItemView(item,null,parent);
        if (mStrictWidthLimit) {
          cellsRemaining-=ActionMenuView.measureChildForCells(v,cellSize,cellsRemaining,querySpec,0);
        }
 else {
          v.measure(querySpec,querySpec);
        }
        final int measuredWidth=v.getMeasuredWidth();
        widthLimit-=measuredWidth;
        if (firstActionWidth == 0) {
          firstActionWidth=measuredWidth;
        }
        final int groupId=item.getGroupId();
        if (groupId != 0) {
          seenGroups.put(groupId,true);
        }
        item.setIsActionButton(true);
      }
 else       if (item.requestsActionButton()) {
        final int groupId=item.getGroupId();
        final boolean inGroup=seenGroups.get(groupId);
        boolean isAction=(maxActions > 0 || inGroup) && widthLimit > 0 && (!mStrictWidthLimit || cellsRemaining > 0);
        if (isAction) {
          View v=getItemView(item,null,parent);
          if (mStrictWidthLimit) {
            final int cells=ActionMenuView.measureChildForCells(v,cellSize,cellsRemaining,querySpec,0);
            cellsRemaining-=cells;
            if (cells == 0) {
              isAction=false;
            }
          }
 else {
            v.measure(querySpec,querySpec);
          }
          final int measuredWidth=v.getMeasuredWidth();
          widthLimit-=measuredWidth;
          if (firstActionWidth == 0) {
            firstActionWidth=measuredWidth;
          }
          if (mStrictWidthLimit) {
            isAction&=widthLimit >= 0;
          }
 else {
            isAction&=widthLimit + firstActionWidth > 0;
          }
        }
        if (isAction && groupId != 0) {
          seenGroups.put(groupId,true);
        }
 else         if (inGroup) {
          seenGroups.put(groupId,false);
          for (int j=0; j < i; j++) {
            MenuItemImpl areYouMyGroupie=visibleItems.get(j);
            if (areYouMyGroupie.getGroupId() == groupId) {
              if (areYouMyGroupie.isActionButton())               maxActions++;
              areYouMyGroupie.setIsActionButton(false);
            }
          }
        }
        if (isAction)         maxActions--;
        item.setIsActionButton(isAction);
      }
 else {
        item.setIsActionButton(false);
      }
    }
    return true;
  }
  public void setMenuView(  ActionMenuView menuView){
    mMenuView=menuView;
    menuView.initialize(mMenu);subtractOverFlow();
  }
  public ActionMenuPresenter(){
    mReserveOverflow=true;
    mWidthLimit=com.ashera.widget.PluginInvoker.getScreenWidth() / 2;
    mActionItemWidthLimit=mWidthLimit;
    mMinCellSize=(int)(ActionMenuView.MIN_CELL_SIZE);
    mMaxItems=getMaxActionButtons();
  }
  public int getMaxActionButtons(){
    int widthDp=com.ashera.widget.PluginInvoker.getScreenWidthDp();
    int heightDp=com.ashera.widget.PluginInvoker.getScreenHeightDp();
    int smallest=widthDp > heightDp ? widthDp : heightDp;
    if (smallest > 600 || widthDp > 600 || widthDp > 960 && heightDp > 720 || widthDp > 720 && heightDp > 960) {
      return 5;
    }
 else     if (widthDp >= 500 || widthDp > 640 && heightDp > 480 || widthDp > 480 && heightDp > 640) {
      return 4;
    }
 else {
      return widthDp >= 360 ? 3 : 2;
    }
  }
  private void subtractOverFlow(){
    int width=mWidthLimit;
    if (mReserveOverflow) {
      if (mOverflowButton == null) {
        mOverflowButton=mMenuView.getOverFlowButton();
        final int spec=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        mOverflowButton.measure(spec,spec);
      }
      width-=mOverflowButton.getMeasuredWidth();
    }
 else {
      mOverflowButton=null;
    }
    mWidthLimit=width;
    mActionItemWidthLimit=mWidthLimit;
  }
  public void setOverFlowButton(  View overflowButton){
    mOverflowButton=overflowButton;
  }
class ActionProvider {
    public void setSubUiVisibilityListener(    ActionMenuPresenter actionMenuPresenter){
    }
  }
  private View mOverflowButton;
}
