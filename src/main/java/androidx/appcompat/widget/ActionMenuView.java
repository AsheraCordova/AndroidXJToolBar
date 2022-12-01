package androidx.appcompat.widget;
import r.android.graphics.drawable.Drawable;
import r.android.view.Gravity;
import r.android.view.View;
import r.android.view.ViewGroup;
import androidx.appcompat.view.menu.MenuBuilder;
public class ActionMenuView extends LinearLayoutCompat {
  static final int MIN_CELL_SIZE=56;
  static final int GENERATED_ITEM_PADDING=4;
  private MenuBuilder mMenu;
  private int mPopupTheme;
  private boolean mReserveOverflow;
  private boolean mFormatItems;
  private int mFormatItemsWidth;
  private int mMinCellSize;
  private int mGeneratedItemPadding;
  protected void onMeasure(  int widthMeasureSpec,  int heightMeasureSpec){
    final boolean wasFormatted=mFormatItems;
    mFormatItems=MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY;
    if (wasFormatted != mFormatItems) {
      mFormatItemsWidth=0;
    }
    final int widthSize=MeasureSpec.getSize(widthMeasureSpec);
    if (mFormatItems && mMenu != null && widthSize != mFormatItemsWidth) {
      mFormatItemsWidth=widthSize;
      mMenu.onItemsChanged(true);
    }
    final int childCount=getChildCount();
    if (mFormatItems && childCount > 0) {
      onMeasureExactFormat(widthMeasureSpec,heightMeasureSpec);
    }
 else {
      for (int i=0; i < childCount; i++) {
        final View child=getChildAt(i);
        final LayoutParams lp=(LayoutParams)child.getLayoutParams();
        lp.leftMargin=lp.rightMargin=0;
      }
      super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
  }
  private void onMeasureExactFormat(  int widthMeasureSpec,  int heightMeasureSpec){
    final int heightMode=MeasureSpec.getMode(heightMeasureSpec);
    int widthSize=MeasureSpec.getSize(widthMeasureSpec);
    int heightSize=MeasureSpec.getSize(heightMeasureSpec);
    final int widthPadding=getPaddingLeft() + getPaddingRight();
    final int heightPadding=getPaddingTop() + getPaddingBottom();
    final int itemHeightSpec=getChildMeasureSpec(heightMeasureSpec,heightPadding,ViewGroup.LayoutParams.WRAP_CONTENT);
    widthSize-=widthPadding;
    final int cellCount=widthSize / mMinCellSize;
    final int cellSizeRemaining=widthSize % mMinCellSize;
    if (cellCount == 0) {
      setMeasuredDimension(widthSize,0);
      return;
    }
    final int cellSize=mMinCellSize + cellSizeRemaining / cellCount;
    int cellsRemaining=cellCount;
    int maxChildHeight=0;
    int maxCellsUsed=0;
    int expandableItemCount=0;
    int visibleItemCount=0;
    boolean hasOverflow=false;
    long smallestItemsAt=0;
    final int childCount=getChildCount();
    for (int i=0; i < childCount; i++) {
      final View child=getChildAt(i);
      if (child.getVisibility() == GONE)       continue;
      final boolean isGeneratedItem=isActionMenuItemView(child);
      visibleItemCount++;
      if (isGeneratedItem) {
        child.setPadding(mGeneratedItemPadding,0,mGeneratedItemPadding,0);
      }
      final LayoutParams lp=(LayoutParams)child.getLayoutParams();
      lp.expanded=false;
      lp.extraPixels=0;
      lp.cellsUsed=0;
      lp.expandable=false;
      lp.leftMargin=0;
      lp.rightMargin=0;
      lp.preventEdgeOffset=isGeneratedItem && hasText(child);
      final int cellsAvailable=lp.isOverflowButton ? 1 : cellsRemaining;
      final int cellsUsed=measureChildForCells(child,cellSize,cellsAvailable,itemHeightSpec,heightPadding);
      maxCellsUsed=Math.max(maxCellsUsed,cellsUsed);
      if (lp.expandable)       expandableItemCount++;
      if (lp.isOverflowButton)       hasOverflow=true;
      cellsRemaining-=cellsUsed;
      maxChildHeight=Math.max(maxChildHeight,child.getMeasuredHeight());
      if (cellsUsed == 1)       smallestItemsAt|=(1 << i);
    }
    final boolean centerSingleExpandedItem=hasOverflow && visibleItemCount == 2;
    boolean needsExpansion=false;
    while (expandableItemCount > 0 && cellsRemaining > 0) {
      int minCells=Integer.MAX_VALUE;
      long minCellsAt=0;
      int minCellsItemCount=0;
      for (int i=0; i < childCount; i++) {
        final View child=getChildAt(i);
        final LayoutParams lp=(LayoutParams)child.getLayoutParams();
        if (!lp.expandable)         continue;
        if (lp.cellsUsed < minCells) {
          minCells=lp.cellsUsed;
          minCellsAt=1L << i;
          minCellsItemCount=1;
        }
 else         if (lp.cellsUsed == minCells) {
          minCellsAt|=1L << i;
          minCellsItemCount++;
        }
      }
      smallestItemsAt|=minCellsAt;
      if (minCellsItemCount > cellsRemaining)       break;
      minCells++;
      for (int i=0; i < childCount; i++) {
        final View child=getChildAt(i);
        final LayoutParams lp=(LayoutParams)child.getLayoutParams();
        if ((minCellsAt & (1 << i)) == 0) {
          if (lp.cellsUsed == minCells)           smallestItemsAt|=1 << i;
          continue;
        }
        if (centerSingleExpandedItem && lp.preventEdgeOffset && cellsRemaining == 1) {
          child.setPadding(mGeneratedItemPadding + cellSize,0,mGeneratedItemPadding,0);
        }
        lp.cellsUsed++;
        lp.expanded=true;
        cellsRemaining--;
      }
      needsExpansion=true;
    }
    final boolean singleItem=!hasOverflow && visibleItemCount == 1;
    if (cellsRemaining > 0 && smallestItemsAt != 0 && (cellsRemaining < visibleItemCount - 1 || singleItem || maxCellsUsed > 1)) {
      float expandCount=Long.bitCount(smallestItemsAt);
      if (!singleItem) {
        if ((smallestItemsAt & 1) != 0) {
          LayoutParams lp=(LayoutParams)getChildAt(0).getLayoutParams();
          if (!lp.preventEdgeOffset)           expandCount-=0.5f;
        }
        if ((smallestItemsAt & (1 << (childCount - 1))) != 0) {
          LayoutParams lp=((LayoutParams)getChildAt(childCount - 1).getLayoutParams());
          if (!lp.preventEdgeOffset)           expandCount-=0.5f;
        }
      }
      final int extraPixels=expandCount > 0 ? (int)(cellsRemaining * cellSize / expandCount) : 0;
      for (int i=0; i < childCount; i++) {
        if ((smallestItemsAt & (1 << i)) == 0)         continue;
        final View child=getChildAt(i);
        final LayoutParams lp=(LayoutParams)child.getLayoutParams();
        if (isActionMenuItemView(child)) {
          lp.extraPixels=extraPixels;
          lp.expanded=true;
          if (i == 0 && !lp.preventEdgeOffset) {
            lp.leftMargin=-extraPixels / 2;
          }
          needsExpansion=true;
        }
 else         if (lp.isOverflowButton) {
          lp.extraPixels=extraPixels;
          lp.expanded=true;
          lp.rightMargin=-extraPixels / 2;
          needsExpansion=true;
        }
 else {
          if (i != 0) {
            lp.leftMargin=extraPixels / 2;
          }
          if (i != childCount - 1) {
            lp.rightMargin=extraPixels / 2;
          }
        }
      }
      cellsRemaining=0;
    }
    if (needsExpansion) {
      for (int i=0; i < childCount; i++) {
        final View child=getChildAt(i);
        final LayoutParams lp=(LayoutParams)child.getLayoutParams();
        if (!lp.expanded)         continue;
        final int width=lp.cellsUsed * cellSize + lp.extraPixels;
        child.measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),itemHeightSpec);
      }
    }
    if (heightMode != MeasureSpec.EXACTLY) {
      heightSize=maxChildHeight;
    }
    setMeasuredDimension(widthSize,heightSize);
  }
  static int measureChildForCells(  View child,  int cellSize,  int cellsRemaining,  int parentHeightMeasureSpec,  int parentHeightPadding){
    final LayoutParams lp=(LayoutParams)child.getLayoutParams();
    final int childHeightSize=MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding;
    final int childHeightMode=MeasureSpec.getMode(parentHeightMeasureSpec);
    final int childHeightSpec=MeasureSpec.makeMeasureSpec(childHeightSize,childHeightMode);
    final View itemView=isActionMenuItemView(child) ? (View)child : null;
    final boolean hasText=itemView != null && hasTextStatic(itemView);
    int cellsUsed=0;
    if (cellsRemaining > 0 && (!hasText || cellsRemaining >= 2)) {
      final int childWidthSpec=MeasureSpec.makeMeasureSpec(cellSize * cellsRemaining,MeasureSpec.AT_MOST);
      child.measure(childWidthSpec,childHeightSpec);
      final int measuredWidth=child.getMeasuredWidth();
      cellsUsed=measuredWidth / cellSize;
      if (measuredWidth % cellSize != 0)       cellsUsed++;
      if (hasText && cellsUsed < 2)       cellsUsed=2;
    }
    final boolean expandable=!lp.isOverflowButton && hasText;
    lp.expandable=expandable;
    lp.cellsUsed=cellsUsed;
    final int targetWidth=cellsUsed * cellSize;
    child.measure(MeasureSpec.makeMeasureSpec(targetWidth,MeasureSpec.EXACTLY),childHeightSpec);
    return cellsUsed;
  }
  protected void onLayout(  boolean changed,  int left,  int top,  int right,  int bottom){
    if (!mFormatItems) {
      super.onLayout(changed,left,top,right,bottom);
      return;
    }
    final int childCount=getChildCount();
    final int midVertical=(bottom - top) / 2;
    final int dividerWidth=getDividerWidth();
    int overflowWidth=0;
    int nonOverflowCount=0;
    int widthRemaining=right - left - getPaddingRight()- getPaddingLeft();
    boolean hasOverflow=false;
    final boolean isLayoutRtl=ViewUtils.isLayoutRtl(this);
    for (int i=0; i < childCount; i++) {
      final View v=getChildAt(i);
      if (v.getVisibility() == GONE) {
        continue;
      }
      LayoutParams p=(LayoutParams)v.getLayoutParams();
      if (p.isOverflowButton) {
        overflowWidth=v.getMeasuredWidth();
        if (hasSupportDividerBeforeChildAt(i)) {
          overflowWidth+=dividerWidth;
        }
        int height=v.getMeasuredHeight();
        int r;
        int l;
        if (isLayoutRtl) {
          l=getPaddingLeft() + p.leftMargin;
          r=l + overflowWidth;
        }
 else {
          r=getWidth() - getPaddingRight() - p.rightMargin;
          l=r - overflowWidth;
        }
        int t=midVertical - (height / 2);
        int b=t + height;
        v.layout(l,t,r,b);
        widthRemaining-=overflowWidth;
        hasOverflow=true;
      }
 else {
        final int size=v.getMeasuredWidth() + p.leftMargin + p.rightMargin;
        widthRemaining-=size;
        if (hasSupportDividerBeforeChildAt(i)) {
        }
        nonOverflowCount++;
      }
    }
    if (childCount == 1 && !hasOverflow) {
      final View v=getChildAt(0);
      final int width=v.getMeasuredWidth();
      final int height=v.getMeasuredHeight();
      final int midHorizontal=(right - left) / 2;
      final int l=midHorizontal - width / 2;
      final int t=midVertical - height / 2;
      v.layout(l,t,l + width,t + height);
      return;
    }
    final int spacerCount=nonOverflowCount - (hasOverflow ? 0 : 1);
    final int spacerSize=Math.max(0,spacerCount > 0 ? widthRemaining / spacerCount : 0);
    if (isLayoutRtl) {
      int startRight=getWidth() - getPaddingRight();
      for (int i=0; i < childCount; i++) {
        final View v=getChildAt(i);
        final LayoutParams lp=(LayoutParams)v.getLayoutParams();
        if (v.getVisibility() == GONE || lp.isOverflowButton) {
          continue;
        }
        startRight-=lp.rightMargin;
        int width=v.getMeasuredWidth();
        int height=v.getMeasuredHeight();
        int t=midVertical - height / 2;
        v.layout(startRight - width,t,startRight,t + height);
        startRight-=width + lp.leftMargin + spacerSize;
      }
    }
 else {
      int startLeft=getPaddingLeft();
      for (int i=0; i < childCount; i++) {
        final View v=getChildAt(i);
        final LayoutParams lp=(LayoutParams)v.getLayoutParams();
        if (v.getVisibility() == GONE || lp.isOverflowButton) {
          continue;
        }
        startLeft+=lp.leftMargin;
        int width=v.getMeasuredWidth();
        int height=v.getMeasuredHeight();
        int t=midVertical - height / 2;
        v.layout(startLeft,t,startLeft + width,t + height);
        startLeft+=width + lp.rightMargin + spacerSize;
      }
    }
  }
  public void setOverflowReserved(  boolean reserveOverflow){
    mReserveOverflow=reserveOverflow;
  }
  protected LayoutParams generateDefaultLayoutParams(){
    LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    params.gravity=Gravity.CENTER_VERTICAL;
    return params;
  }
  protected boolean checkLayoutParams(  ViewGroup.LayoutParams p){
    return p instanceof LayoutParams;
  }
  public LayoutParams generateOverflowButtonLayoutParams(){
    LayoutParams result=generateDefaultLayoutParams();
    result.isOverflowButton=true;
    return result;
  }
  public void initialize(  MenuBuilder menu){
    mMenu=menu;
  }
  protected boolean hasSupportDividerBeforeChildAt(  int childIndex){
    if (childIndex == 0) {
      return false;
    }
    final View childBefore=getChildAt(childIndex - 1);
    final View child=getChildAt(childIndex);
    boolean result=false;
    if (childIndex < getChildCount() && childBefore instanceof ActionMenuChildView) {
      result|=((ActionMenuChildView)childBefore).needsDividerAfter();
    }
    if (childIndex > 0 && child instanceof ActionMenuChildView) {
      result|=((ActionMenuChildView)child).needsDividerBefore();
    }
    return result;
  }
public static class LayoutParams extends LinearLayoutCompat.LayoutParams {
    public boolean isOverflowButton;
    public int cellsUsed;
    public int extraPixels;
    public boolean expandable;
    public boolean preventEdgeOffset;
    boolean expanded;
    public LayoutParams(    ViewGroup.LayoutParams other){
      super(other);
    }
    public LayoutParams(    LayoutParams other){
      super((ViewGroup.LayoutParams)other);
      isOverflowButton=other.isOverflowButton;
    }
    public LayoutParams(    int width,    int height){
      super(width,height);
      isOverflowButton=false;
    }
  }
  public ActionMenuView(){
    setBaselineAligned(false);
    final float density=getResources().getDisplayMetrics().density;
    mMinCellSize=(int)(MIN_CELL_SIZE * density);
    mGeneratedItemPadding=(int)(GENERATED_ITEM_PADDING * density);
  }
  public int getDividerWidth(){
    return 0;
  }
  protected LayoutParams generateLayoutParams(  ViewGroup.LayoutParams p){
    return new LayoutParams(p);
  }
  ActionMenuPresenter mPresenter;
  public MenuBuilder getMenu(){
    if (mMenu == null) {
      mMenu=new MenuBuilder();
      mPresenter=new ActionMenuPresenter();
      mPresenter.setReserveOverflow(true);
      mPresenter.setMenu(mMenu);
      mMenu.setActionMenuPresenter(mPresenter);
      mPresenter.setMenuView(this);
    }
    return mMenu;
  }
  public void updateMenuView(){
    mPresenter.updateMenuView(true);
  }
  public View getItemView(  r.android.view.MenuItem item){
    return null;
  }
  public View getOverFlowButton(){
    return null;
  }
  public static boolean isActionMenuItemView(  View child){
    return child != null && child.getTag() != null && child.getTag().equals(1);
  }
  private static boolean hasTextStatic(  View itemView){
    return ((ActionMenuView)itemView.getParent()).hasText(itemView);
  }
  public boolean hasText(  View child){
    return false;
  }
class ActionMenuChildView extends View {
    boolean needsDividerAfter(){
      return false;
    }
    boolean needsDividerBefore(){
      return false;
    }
  }
}
