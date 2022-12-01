package androidx.appcompat.widget;
import r.android.graphics.Canvas;
import r.android.graphics.drawable.Drawable;
import r.android.view.Gravity;
import r.android.view.View;
import r.android.view.ViewGroup;
import r.android.widget.LinearLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
public class LinearLayoutCompat extends ViewGroup {
  public static final int HORIZONTAL=0;
  public static final int VERTICAL=1;
  public static final int SHOW_DIVIDER_NONE=0;
  public static final int SHOW_DIVIDER_BEGINNING=1;
  public static final int SHOW_DIVIDER_MIDDLE=2;
  public static final int SHOW_DIVIDER_END=4;
  private boolean mBaselineAligned=true;
  private int mBaselineAlignedChildIndex=-1;
  private int mBaselineChildTop=0;
  private int mOrientation;
  private int mGravity=GravityCompat.START | Gravity.TOP;
  private int mTotalLength;
  private float mWeightSum;
  private boolean mUseLargestChild;
  private int[] mMaxAscent;
  private int[] mMaxDescent;
  private static final int VERTICAL_GRAVITY_COUNT=4;
  private static final int INDEX_CENTER_VERTICAL=0;
  private static final int INDEX_TOP=1;
  private static final int INDEX_BOTTOM=2;
  private static final int INDEX_FILL=3;
  private Drawable mDivider;
  private int mDividerWidth;
  private int mDividerHeight;
  private int mShowDividers;
  private int mDividerPadding;
  public void setShowDividers(  int showDividers){
    if (showDividers != mShowDividers) {
      requestLayout();
    }
    mShowDividers=showDividers;
  }
  public int getShowDividers(){
    return mShowDividers;
  }
  public Drawable getDividerDrawable(){
    return mDivider;
  }
  public void setDividerDrawable(  Drawable divider){
    if (divider == mDivider) {
      return;
    }
    mDivider=divider;
    if (divider != null) {
      mDividerWidth=divider.getIntrinsicWidth();
      mDividerHeight=divider.getIntrinsicHeight();
    }
 else {
      mDividerWidth=0;
      mDividerHeight=0;
    }
    setWillNotDraw(divider == null);
    requestLayout();
  }
  public void setDividerPadding(  int padding){
    mDividerPadding=padding;
  }
  public int getDividerPadding(){
    return mDividerPadding;
  }
  protected void onDraw(  Canvas canvas){
    if (mDivider == null) {
      return;
    }
    if (mOrientation == VERTICAL) {
      drawDividersVertical(canvas);
    }
 else {
      drawDividersHorizontal(canvas);
    }
  }
  void drawDividersVertical(  Canvas canvas){
    final int count=getVirtualChildCount();
    for (int i=0; i < count; i++) {
      final View child=getVirtualChildAt(i);
      if (child != null && child.getVisibility() != GONE) {
        if (hasDividerBeforeChildAt(i)) {
          final LayoutParams lp=(LayoutParams)child.getLayoutParams();
          final int top=child.getTop() - lp.topMargin - mDividerHeight;
          drawHorizontalDivider(canvas,top);
        }
      }
    }
    if (hasDividerBeforeChildAt(count)) {
      final View child=getVirtualChildAt(count - 1);
      int bottom=0;
      if (child == null) {
        bottom=getHeight() - getPaddingBottom() - mDividerHeight;
      }
 else {
        final LayoutParams lp=(LayoutParams)child.getLayoutParams();
        bottom=child.getBottom() + lp.bottomMargin;
      }
      drawHorizontalDivider(canvas,bottom);
    }
  }
  void drawDividersHorizontal(  Canvas canvas){
    final int count=getVirtualChildCount();
    final boolean isLayoutRtl=ViewUtils.isLayoutRtl(this);
    for (int i=0; i < count; i++) {
      final View child=getVirtualChildAt(i);
      if (child != null && child.getVisibility() != GONE) {
        if (hasDividerBeforeChildAt(i)) {
          final LayoutParams lp=(LayoutParams)child.getLayoutParams();
          final int position;
          if (isLayoutRtl) {
            position=child.getRight() + lp.rightMargin;
          }
 else {
            position=child.getLeft() - lp.leftMargin - mDividerWidth;
          }
          drawVerticalDivider(canvas,position);
        }
      }
    }
    if (hasDividerBeforeChildAt(count)) {
      final View child=getVirtualChildAt(count - 1);
      int position;
      if (child == null) {
        if (isLayoutRtl) {
          position=getPaddingLeft();
        }
 else {
          position=getWidth() - getPaddingRight() - mDividerWidth;
        }
      }
 else {
        final LayoutParams lp=(LayoutParams)child.getLayoutParams();
        if (isLayoutRtl) {
          position=child.getLeft() - lp.leftMargin - mDividerWidth;
        }
 else {
          position=child.getRight() + lp.rightMargin;
        }
      }
      drawVerticalDivider(canvas,position);
    }
  }
  void drawHorizontalDivider(  Canvas canvas,  int top){
    mDivider.setBounds(getPaddingLeft() + mDividerPadding,top,getWidth() - getPaddingRight() - mDividerPadding,top + mDividerHeight);
    mDivider.draw(canvas);
  }
  void drawVerticalDivider(  Canvas canvas,  int left){
    mDivider.setBounds(left,getPaddingTop() + mDividerPadding,left + mDividerWidth,getHeight() - getPaddingBottom() - mDividerPadding);
    mDivider.draw(canvas);
  }
  public boolean isBaselineAligned(){
    return mBaselineAligned;
  }
  public void setBaselineAligned(  boolean baselineAligned){
    mBaselineAligned=baselineAligned;
  }
  public boolean isMeasureWithLargestChildEnabled(){
    return mUseLargestChild;
  }
  public void setMeasureWithLargestChildEnabled(  boolean enabled){
    mUseLargestChild=enabled;
  }
  public int getBaseline(){
    if (mBaselineAlignedChildIndex < 0) {
      return super.getBaseline();
    }
    if (getChildCount() <= mBaselineAlignedChildIndex) {
      throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout " + "set to an index that is out of bounds.");
    }
    final View child=getChildAt(mBaselineAlignedChildIndex);
    final int childBaseline=child.getBaseline();
    if (childBaseline == -1) {
      if (mBaselineAlignedChildIndex == 0) {
        return -1;
      }
      throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout " + "points to a View that doesn't know how to get its baseline.");
    }
    int childTop=mBaselineChildTop;
    if (mOrientation == VERTICAL) {
      final int majorGravity=mGravity & Gravity.VERTICAL_GRAVITY_MASK;
      if (majorGravity != Gravity.TOP) {
switch (majorGravity) {
case Gravity.BOTTOM:
          childTop=getBottom() - getTop() - getPaddingBottom()- mTotalLength;
        break;
case Gravity.CENTER_VERTICAL:
      childTop+=((getBottom() - getTop() - getPaddingTop()- getPaddingBottom()) - mTotalLength) / 2;
    break;
}
}
}
LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
return childTop + lp.topMargin + childBaseline;
}
public int getBaselineAlignedChildIndex(){
return mBaselineAlignedChildIndex;
}
public void setBaselineAlignedChildIndex(int i){
if ((i < 0) || (i >= getChildCount())) {
throw new IllegalArgumentException("base aligned child index out " + "of range (0, " + getChildCount() + ")");
}
mBaselineAlignedChildIndex=i;
}
View getVirtualChildAt(int index){
return getChildAt(index);
}
int getVirtualChildCount(){
return getChildCount();
}
public float getWeightSum(){
return mWeightSum;
}
public void setWeightSum(float weightSum){
mWeightSum=Math.max(0.0f,weightSum);
}
protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
if (mOrientation == VERTICAL) {
measureVertical(widthMeasureSpec,heightMeasureSpec);
}
 else {
measureHorizontal(widthMeasureSpec,heightMeasureSpec);
}
}
protected boolean hasDividerBeforeChildAt(int childIndex){
if (childIndex == 0) {
return (mShowDividers & SHOW_DIVIDER_BEGINNING) != 0;
}
 else if (childIndex == getChildCount()) {
return (mShowDividers & SHOW_DIVIDER_END) != 0;
}
 else if ((mShowDividers & SHOW_DIVIDER_MIDDLE) != 0) {
boolean hasVisibleViewBefore=false;
for (int i=childIndex - 1; i >= 0; i--) {
if (getChildAt(i).getVisibility() != GONE) {
  hasVisibleViewBefore=true;
  break;
}
}
return hasVisibleViewBefore;
}
return false;
}
void measureVertical(int widthMeasureSpec,int heightMeasureSpec){
mTotalLength=0;
int maxWidth=0;
int childState=0;
int alternativeMaxWidth=0;
int weightedMaxWidth=0;
boolean allFillParent=true;
float totalWeight=0;
final int count=getVirtualChildCount();
final int widthMode=MeasureSpec.getMode(widthMeasureSpec);
final int heightMode=MeasureSpec.getMode(heightMeasureSpec);
boolean matchWidth=false;
boolean skippedMeasure=false;
final int baselineChildIndex=mBaselineAlignedChildIndex;
final boolean useLargestChild=mUseLargestChild;
int largestChildHeight=0;
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child == null) {
mTotalLength+=measureNullChild(i);
continue;
}
if (child.getVisibility() == View.GONE) {
i+=getChildrenSkipCount(child,i);
continue;
}
if (hasDividerBeforeChildAt(i)) {
mTotalLength+=mDividerHeight;
}
LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
totalWeight+=lp.weight;
if (heightMode == MeasureSpec.EXACTLY && lp.height == 0 && lp.weight > 0) {
final int totalLength=mTotalLength;
mTotalLength=Math.max(totalLength,totalLength + lp.topMargin + lp.bottomMargin);
skippedMeasure=true;
}
 else {
int oldHeight=Integer.MIN_VALUE;
if (lp.height == 0 && lp.weight > 0) {
  oldHeight=0;
  lp.height=LayoutParams.WRAP_CONTENT;
}
measureChildBeforeLayout(child,i,widthMeasureSpec,0,heightMeasureSpec,totalWeight == 0 ? mTotalLength : 0);
if (oldHeight != Integer.MIN_VALUE) {
  lp.height=oldHeight;
}
final int childHeight=child.getMeasuredHeight();
final int totalLength=mTotalLength;
mTotalLength=Math.max(totalLength,totalLength + childHeight + lp.topMargin+ lp.bottomMargin+ getNextLocationOffset(child));
if (useLargestChild) {
  largestChildHeight=Math.max(childHeight,largestChildHeight);
}
}
if ((baselineChildIndex >= 0) && (baselineChildIndex == i + 1)) {
mBaselineChildTop=mTotalLength;
}
if (i < baselineChildIndex && lp.weight > 0) {
throw new RuntimeException("A child of LinearLayout with index " + "less than mBaselineAlignedChildIndex has weight > 0, which " + "won't work.  Either remove the weight, or don't set "+ "mBaselineAlignedChildIndex.");
}
boolean matchWidthLocally=false;
if (widthMode != MeasureSpec.EXACTLY && lp.width == LayoutParams.MATCH_PARENT) {
matchWidth=true;
matchWidthLocally=true;
}
final int margin=lp.leftMargin + lp.rightMargin;
final int measuredWidth=child.getMeasuredWidth() + margin;
maxWidth=Math.max(maxWidth,measuredWidth);
childState=View.combineMeasuredStates(childState,child.getMeasuredState());
allFillParent=allFillParent && lp.width == LayoutParams.MATCH_PARENT;
if (lp.weight > 0) {
weightedMaxWidth=Math.max(weightedMaxWidth,matchWidthLocally ? margin : measuredWidth);
}
 else {
alternativeMaxWidth=Math.max(alternativeMaxWidth,matchWidthLocally ? margin : measuredWidth);
}
i+=getChildrenSkipCount(child,i);
}
if (mTotalLength > 0 && hasDividerBeforeChildAt(count)) {
mTotalLength+=mDividerHeight;
}
if (useLargestChild && (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED)) {
mTotalLength=0;
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child == null) {
  mTotalLength+=measureNullChild(i);
  continue;
}
if (child.getVisibility() == GONE) {
  i+=getChildrenSkipCount(child,i);
  continue;
}
final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
final int totalLength=mTotalLength;
mTotalLength=Math.max(totalLength,totalLength + largestChildHeight + lp.topMargin+ lp.bottomMargin+ getNextLocationOffset(child));
}
}
mTotalLength+=getPaddingTop() + getPaddingBottom();
int heightSize=mTotalLength;
heightSize=Math.max(heightSize,getSuggestedMinimumHeight());
int heightSizeAndState=View.resolveSizeAndState(heightSize,heightMeasureSpec,0);
heightSize=heightSizeAndState & View.MEASURED_SIZE_MASK;
int delta=heightSize - mTotalLength;
if (skippedMeasure || (delta != 0 && totalWeight > 0.0f)) {
float weightSum=mWeightSum > 0.0f ? mWeightSum : totalWeight;
mTotalLength=0;
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child.getVisibility() == View.GONE) {
  continue;
}
LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
float childExtra=lp.weight;
if (childExtra > 0) {
  int share=(int)(childExtra * delta / weightSum);
  weightSum-=childExtra;
  delta-=share;
  final int childWidthMeasureSpec=getChildMeasureSpec(widthMeasureSpec,getPaddingLeft() + getPaddingRight() + lp.leftMargin+ lp.rightMargin,lp.width);
  if ((lp.height != 0) || (heightMode != MeasureSpec.EXACTLY)) {
    int childHeight=child.getMeasuredHeight() + share;
    if (childHeight < 0) {
      childHeight=0;
    }
    child.measure(childWidthMeasureSpec,MeasureSpec.makeMeasureSpec(childHeight,MeasureSpec.EXACTLY));
  }
 else {
    child.measure(childWidthMeasureSpec,MeasureSpec.makeMeasureSpec(share > 0 ? share : 0,MeasureSpec.EXACTLY));
  }
  childState=View.combineMeasuredStates(childState,child.getMeasuredState() & (View.MEASURED_STATE_MASK >> View.MEASURED_HEIGHT_STATE_SHIFT));
}
final int margin=lp.leftMargin + lp.rightMargin;
final int measuredWidth=child.getMeasuredWidth() + margin;
maxWidth=Math.max(maxWidth,measuredWidth);
boolean matchWidthLocally=widthMode != MeasureSpec.EXACTLY && lp.width == LayoutParams.MATCH_PARENT;
alternativeMaxWidth=Math.max(alternativeMaxWidth,matchWidthLocally ? margin : measuredWidth);
allFillParent=allFillParent && lp.width == LayoutParams.MATCH_PARENT;
final int totalLength=mTotalLength;
mTotalLength=Math.max(totalLength,totalLength + child.getMeasuredHeight() + lp.topMargin+ lp.bottomMargin+ getNextLocationOffset(child));
}
mTotalLength+=getPaddingTop() + getPaddingBottom();
}
 else {
alternativeMaxWidth=Math.max(alternativeMaxWidth,weightedMaxWidth);
if (useLargestChild && heightMode != MeasureSpec.EXACTLY) {
for (int i=0; i < count; i++) {
  final View child=getVirtualChildAt(i);
  if (child == null || child.getVisibility() == View.GONE) {
    continue;
  }
  final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
  float childExtra=lp.weight;
  if (childExtra > 0) {
    child.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(),MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(largestChildHeight,MeasureSpec.EXACTLY));
  }
}
}
}
if (!allFillParent && widthMode != MeasureSpec.EXACTLY) {
maxWidth=alternativeMaxWidth;
}
maxWidth+=getPaddingLeft() + getPaddingRight();
maxWidth=Math.max(maxWidth,getSuggestedMinimumWidth());
setMeasuredDimension(View.resolveSizeAndState(maxWidth,widthMeasureSpec,childState),heightSizeAndState);
if (matchWidth) {
forceUniformWidth(count,heightMeasureSpec);
}
}
private void forceUniformWidth(int count,int heightMeasureSpec){
int uniformMeasureSpec=MeasureSpec.makeMeasureSpec(getMeasuredWidth(),MeasureSpec.EXACTLY);
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child.getVisibility() != GONE) {
LinearLayoutCompat.LayoutParams lp=((LinearLayoutCompat.LayoutParams)child.getLayoutParams());
if (lp.width == LayoutParams.MATCH_PARENT) {
  int oldHeight=lp.height;
  lp.height=child.getMeasuredHeight();
  measureChildWithMargins(child,uniformMeasureSpec,0,heightMeasureSpec,0);
  lp.height=oldHeight;
}
}
}
}
void measureHorizontal(int widthMeasureSpec,int heightMeasureSpec){
mTotalLength=0;
int maxHeight=0;
int childState=0;
int alternativeMaxHeight=0;
int weightedMaxHeight=0;
boolean allFillParent=true;
float totalWeight=0;
final int count=getVirtualChildCount();
final int widthMode=MeasureSpec.getMode(widthMeasureSpec);
final int heightMode=MeasureSpec.getMode(heightMeasureSpec);
boolean matchHeight=false;
boolean skippedMeasure=false;
if (mMaxAscent == null || mMaxDescent == null) {
mMaxAscent=new int[VERTICAL_GRAVITY_COUNT];
mMaxDescent=new int[VERTICAL_GRAVITY_COUNT];
}
final int[] maxAscent=mMaxAscent;
final int[] maxDescent=mMaxDescent;
maxAscent[0]=maxAscent[1]=maxAscent[2]=maxAscent[3]=-1;
maxDescent[0]=maxDescent[1]=maxDescent[2]=maxDescent[3]=-1;
final boolean baselineAligned=mBaselineAligned;
final boolean useLargestChild=mUseLargestChild;
final boolean isExactly=widthMode == MeasureSpec.EXACTLY;
int largestChildWidth=0;
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child == null) {
mTotalLength+=measureNullChild(i);
continue;
}
if (child.getVisibility() == GONE) {
i+=getChildrenSkipCount(child,i);
continue;
}
if (hasDividerBeforeChildAt(i)) {
mTotalLength+=mDividerWidth;
}
final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
totalWeight+=lp.weight;
if (widthMode == MeasureSpec.EXACTLY && lp.width == 0 && lp.weight > 0) {
if (isExactly) {
  mTotalLength+=lp.leftMargin + lp.rightMargin;
}
 else {
  final int totalLength=mTotalLength;
  mTotalLength=Math.max(totalLength,totalLength + lp.leftMargin + lp.rightMargin);
}
if (baselineAligned) {
  final int freeSpec=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
  child.measure(freeSpec,freeSpec);
}
 else {
  skippedMeasure=true;
}
}
 else {
int oldWidth=Integer.MIN_VALUE;
if (lp.width == 0 && lp.weight > 0) {
  oldWidth=0;
  lp.width=LayoutParams.WRAP_CONTENT;
}
measureChildBeforeLayout(child,i,widthMeasureSpec,totalWeight == 0 ? mTotalLength : 0,heightMeasureSpec,0);
if (oldWidth != Integer.MIN_VALUE) {
  lp.width=oldWidth;
}
final int childWidth=child.getMeasuredWidth();
if (isExactly) {
  mTotalLength+=childWidth + lp.leftMargin + lp.rightMargin+ getNextLocationOffset(child);
}
 else {
  final int totalLength=mTotalLength;
  mTotalLength=Math.max(totalLength,totalLength + childWidth + lp.leftMargin+ lp.rightMargin+ getNextLocationOffset(child));
}
if (useLargestChild) {
  largestChildWidth=Math.max(childWidth,largestChildWidth);
}
}
boolean matchHeightLocally=false;
if (heightMode != MeasureSpec.EXACTLY && lp.height == LayoutParams.MATCH_PARENT) {
matchHeight=true;
matchHeightLocally=true;
}
final int margin=lp.topMargin + lp.bottomMargin;
final int childHeight=child.getMeasuredHeight() + margin;
childState=View.combineMeasuredStates(childState,child.getMeasuredState());
if (baselineAligned) {
final int childBaseline=child.getBaseline();
if (childBaseline != -1) {
  final int gravity=(lp.gravity < 0 ? mGravity : lp.gravity) & Gravity.VERTICAL_GRAVITY_MASK;
  final int index=((gravity >> Gravity.AXIS_Y_SHIFT) & ~Gravity.AXIS_SPECIFIED) >> 1;
  maxAscent[index]=Math.max(maxAscent[index],childBaseline);
  maxDescent[index]=Math.max(maxDescent[index],childHeight - childBaseline);
}
}
maxHeight=Math.max(maxHeight,childHeight);
allFillParent=allFillParent && lp.height == LayoutParams.MATCH_PARENT;
if (lp.weight > 0) {
weightedMaxHeight=Math.max(weightedMaxHeight,matchHeightLocally ? margin : childHeight);
}
 else {
alternativeMaxHeight=Math.max(alternativeMaxHeight,matchHeightLocally ? margin : childHeight);
}
i+=getChildrenSkipCount(child,i);
}
if (mTotalLength > 0 && hasDividerBeforeChildAt(count)) {
mTotalLength+=mDividerWidth;
}
if (maxAscent[INDEX_TOP] != -1 || maxAscent[INDEX_CENTER_VERTICAL] != -1 || maxAscent[INDEX_BOTTOM] != -1 || maxAscent[INDEX_FILL] != -1) {
final int ascent=Math.max(maxAscent[INDEX_FILL],Math.max(maxAscent[INDEX_CENTER_VERTICAL],Math.max(maxAscent[INDEX_TOP],maxAscent[INDEX_BOTTOM])));
final int descent=Math.max(maxDescent[INDEX_FILL],Math.max(maxDescent[INDEX_CENTER_VERTICAL],Math.max(maxDescent[INDEX_TOP],maxDescent[INDEX_BOTTOM])));
maxHeight=Math.max(maxHeight,ascent + descent);
}
if (useLargestChild && (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED)) {
mTotalLength=0;
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child == null) {
  mTotalLength+=measureNullChild(i);
  continue;
}
if (child.getVisibility() == GONE) {
  i+=getChildrenSkipCount(child,i);
  continue;
}
final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
if (isExactly) {
  mTotalLength+=largestChildWidth + lp.leftMargin + lp.rightMargin+ getNextLocationOffset(child);
}
 else {
  final int totalLength=mTotalLength;
  mTotalLength=Math.max(totalLength,totalLength + largestChildWidth + lp.leftMargin+ lp.rightMargin+ getNextLocationOffset(child));
}
}
}
mTotalLength+=getPaddingLeft() + getPaddingRight();
int widthSize=mTotalLength;
widthSize=Math.max(widthSize,getSuggestedMinimumWidth());
int widthSizeAndState=View.resolveSizeAndState(widthSize,widthMeasureSpec,0);
widthSize=widthSizeAndState & View.MEASURED_SIZE_MASK;
int delta=widthSize - mTotalLength;
if (skippedMeasure || (delta != 0 && totalWeight > 0.0f)) {
float weightSum=mWeightSum > 0.0f ? mWeightSum : totalWeight;
maxAscent[0]=maxAscent[1]=maxAscent[2]=maxAscent[3]=-1;
maxDescent[0]=maxDescent[1]=maxDescent[2]=maxDescent[3]=-1;
maxHeight=-1;
mTotalLength=0;
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child == null || child.getVisibility() == View.GONE) {
  continue;
}
final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
float childExtra=lp.weight;
if (childExtra > 0) {
  int share=(int)(childExtra * delta / weightSum);
  weightSum-=childExtra;
  delta-=share;
  final int childHeightMeasureSpec=getChildMeasureSpec(heightMeasureSpec,getPaddingTop() + getPaddingBottom() + lp.topMargin+ lp.bottomMargin,lp.height);
  if ((lp.width != 0) || (widthMode != MeasureSpec.EXACTLY)) {
    int childWidth=child.getMeasuredWidth() + share;
    if (childWidth < 0) {
      childWidth=0;
    }
    child.measure(MeasureSpec.makeMeasureSpec(childWidth,MeasureSpec.EXACTLY),childHeightMeasureSpec);
  }
 else {
    child.measure(MeasureSpec.makeMeasureSpec(share > 0 ? share : 0,MeasureSpec.EXACTLY),childHeightMeasureSpec);
  }
  childState=View.combineMeasuredStates(childState,child.getMeasuredState() & View.MEASURED_STATE_MASK);
}
if (isExactly) {
  mTotalLength+=child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin+ getNextLocationOffset(child);
}
 else {
  final int totalLength=mTotalLength;
  mTotalLength=Math.max(totalLength,totalLength + child.getMeasuredWidth() + lp.leftMargin+ lp.rightMargin+ getNextLocationOffset(child));
}
boolean matchHeightLocally=heightMode != MeasureSpec.EXACTLY && lp.height == LayoutParams.MATCH_PARENT;
final int margin=lp.topMargin + lp.bottomMargin;
int childHeight=child.getMeasuredHeight() + margin;
maxHeight=Math.max(maxHeight,childHeight);
alternativeMaxHeight=Math.max(alternativeMaxHeight,matchHeightLocally ? margin : childHeight);
allFillParent=allFillParent && lp.height == LayoutParams.MATCH_PARENT;
if (baselineAligned) {
  final int childBaseline=child.getBaseline();
  if (childBaseline != -1) {
    final int gravity=(lp.gravity < 0 ? mGravity : lp.gravity) & Gravity.VERTICAL_GRAVITY_MASK;
    final int index=((gravity >> Gravity.AXIS_Y_SHIFT) & ~Gravity.AXIS_SPECIFIED) >> 1;
    maxAscent[index]=Math.max(maxAscent[index],childBaseline);
    maxDescent[index]=Math.max(maxDescent[index],childHeight - childBaseline);
  }
}
}
mTotalLength+=getPaddingLeft() + getPaddingRight();
if (maxAscent[INDEX_TOP] != -1 || maxAscent[INDEX_CENTER_VERTICAL] != -1 || maxAscent[INDEX_BOTTOM] != -1 || maxAscent[INDEX_FILL] != -1) {
final int ascent=Math.max(maxAscent[INDEX_FILL],Math.max(maxAscent[INDEX_CENTER_VERTICAL],Math.max(maxAscent[INDEX_TOP],maxAscent[INDEX_BOTTOM])));
final int descent=Math.max(maxDescent[INDEX_FILL],Math.max(maxDescent[INDEX_CENTER_VERTICAL],Math.max(maxDescent[INDEX_TOP],maxDescent[INDEX_BOTTOM])));
maxHeight=Math.max(maxHeight,ascent + descent);
}
}
 else {
alternativeMaxHeight=Math.max(alternativeMaxHeight,weightedMaxHeight);
if (useLargestChild && widthMode != MeasureSpec.EXACTLY) {
for (int i=0; i < count; i++) {
  final View child=getVirtualChildAt(i);
  if (child == null || child.getVisibility() == View.GONE) {
    continue;
  }
  final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
  float childExtra=lp.weight;
  if (childExtra > 0) {
    child.measure(MeasureSpec.makeMeasureSpec(largestChildWidth,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(),MeasureSpec.EXACTLY));
  }
}
}
}
if (!allFillParent && heightMode != MeasureSpec.EXACTLY) {
maxHeight=alternativeMaxHeight;
}
maxHeight+=getPaddingTop() + getPaddingBottom();
maxHeight=Math.max(maxHeight,getSuggestedMinimumHeight());
setMeasuredDimension(widthSizeAndState | (childState & View.MEASURED_STATE_MASK),View.resolveSizeAndState(maxHeight,heightMeasureSpec,(childState << View.MEASURED_HEIGHT_STATE_SHIFT)));
if (matchHeight) {
forceUniformHeight(count,widthMeasureSpec);
}
}
private void forceUniformHeight(int count,int widthMeasureSpec){
int uniformMeasureSpec=MeasureSpec.makeMeasureSpec(getMeasuredHeight(),MeasureSpec.EXACTLY);
for (int i=0; i < count; ++i) {
final View child=getVirtualChildAt(i);
if (child.getVisibility() != GONE) {
LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
if (lp.height == LayoutParams.MATCH_PARENT) {
  int oldWidth=lp.width;
  lp.width=child.getMeasuredWidth();
  measureChildWithMargins(child,widthMeasureSpec,0,uniformMeasureSpec,0);
  lp.width=oldWidth;
}
}
}
}
int getChildrenSkipCount(View child,int index){
return 0;
}
int measureNullChild(int childIndex){
return 0;
}
void measureChildBeforeLayout(View child,int childIndex,int widthMeasureSpec,int totalWidth,int heightMeasureSpec,int totalHeight){
measureChildWithMargins(child,widthMeasureSpec,totalWidth,heightMeasureSpec,totalHeight);
}
int getLocationOffset(View child){
return 0;
}
int getNextLocationOffset(View child){
return 0;
}
protected void onLayout(boolean changed,int l,int t,int r,int b){
if (mOrientation == VERTICAL) {
layoutVertical(l,t,r,b);
}
 else {
layoutHorizontal(l,t,r,b);
}
}
void layoutVertical(int left,int top,int right,int bottom){
final int paddingLeft=getPaddingLeft();
int childTop;
int childLeft;
final int width=right - left;
int childRight=width - getPaddingRight();
int childSpace=width - paddingLeft - getPaddingRight();
final int count=getVirtualChildCount();
final int majorGravity=mGravity & Gravity.VERTICAL_GRAVITY_MASK;
final int minorGravity=mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
switch (majorGravity) {
case Gravity.BOTTOM:
childTop=getPaddingTop() + bottom - top - mTotalLength;
break;
case Gravity.CENTER_VERTICAL:
childTop=getPaddingTop() + (bottom - top - mTotalLength) / 2;
break;
case Gravity.TOP:
default :
childTop=getPaddingTop();
break;
}
for (int i=0; i < count; i++) {
final View child=getVirtualChildAt(i);
if (child == null) {
childTop+=measureNullChild(i);
}
 else if (child.getVisibility() != GONE) {
final int childWidth=child.getMeasuredWidth();
final int childHeight=child.getMeasuredHeight();
final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
int gravity=lp.gravity;
if (gravity < 0) {
gravity=minorGravity;
}
final int layoutDirection=ViewCompat.getLayoutDirection(this);
final int absoluteGravity=GravityCompat.getAbsoluteGravity(gravity,layoutDirection);
switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
case Gravity.CENTER_HORIZONTAL:
childLeft=paddingLeft + ((childSpace - childWidth) / 2) + lp.leftMargin - lp.rightMargin;
break;
case Gravity.RIGHT:
childLeft=childRight - childWidth - lp.rightMargin;
break;
case Gravity.LEFT:
default :
childLeft=paddingLeft + lp.leftMargin;
break;
}
if (hasDividerBeforeChildAt(i)) {
childTop+=mDividerHeight;
}
childTop+=lp.topMargin;
setChildFrame(child,childLeft,childTop + getLocationOffset(child),childWidth,childHeight);
childTop+=childHeight + lp.bottomMargin + getNextLocationOffset(child);
i+=getChildrenSkipCount(child,i);
}
}
}
void layoutHorizontal(int left,int top,int right,int bottom){
final boolean isLayoutRtl=ViewUtils.isLayoutRtl(this);
final int paddingTop=getPaddingTop();
int childTop;
int childLeft;
final int height=bottom - top;
int childBottom=height - getPaddingBottom();
int childSpace=height - paddingTop - getPaddingBottom();
final int count=getVirtualChildCount();
final int majorGravity=mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
final int minorGravity=mGravity & Gravity.VERTICAL_GRAVITY_MASK;
final boolean baselineAligned=mBaselineAligned;
final int[] maxAscent=mMaxAscent;
final int[] maxDescent=mMaxDescent;
final int layoutDirection=ViewCompat.getLayoutDirection(this);
switch (GravityCompat.getAbsoluteGravity(majorGravity,layoutDirection)) {
case Gravity.RIGHT:
childLeft=getPaddingLeft() + right - left - mTotalLength;
break;
case Gravity.CENTER_HORIZONTAL:
childLeft=getPaddingLeft() + (right - left - mTotalLength) / 2;
break;
case Gravity.LEFT:
default :
childLeft=getPaddingLeft();
break;
}
int start=0;
int dir=1;
if (isLayoutRtl) {
start=count - 1;
dir=-1;
}
for (int i=0; i < count; i++) {
int childIndex=start + dir * i;
final View child=getVirtualChildAt(childIndex);
if (child == null) {
childLeft+=measureNullChild(childIndex);
}
 else if (child.getVisibility() != GONE) {
final int childWidth=child.getMeasuredWidth();
final int childHeight=child.getMeasuredHeight();
int childBaseline=-1;
final LinearLayoutCompat.LayoutParams lp=(LinearLayoutCompat.LayoutParams)child.getLayoutParams();
if (baselineAligned && lp.height != LayoutParams.MATCH_PARENT) {
childBaseline=child.getBaseline();
}
int gravity=lp.gravity;
if (gravity < 0) {
gravity=minorGravity;
}
switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
case Gravity.TOP:
childTop=paddingTop + lp.topMargin;
if (childBaseline != -1) {
childTop+=maxAscent[INDEX_TOP] - childBaseline;
}
break;
case Gravity.CENTER_VERTICAL:
childTop=paddingTop + ((childSpace - childHeight) / 2) + lp.topMargin - lp.bottomMargin;
break;
case Gravity.BOTTOM:
childTop=childBottom - childHeight - lp.bottomMargin;
if (childBaseline != -1) {
int descent=child.getMeasuredHeight() - childBaseline;
childTop-=(maxDescent[INDEX_BOTTOM] - descent);
}
break;
default :
childTop=paddingTop;
break;
}
if (hasDividerBeforeChildAt(childIndex)) {
childLeft+=mDividerWidth;
}
childLeft+=lp.leftMargin;
setChildFrame(child,childLeft + getLocationOffset(child),childTop,childWidth,childHeight);
childLeft+=childWidth + lp.rightMargin + getNextLocationOffset(child);
i+=getChildrenSkipCount(child,childIndex);
}
}
}
private void setChildFrame(View child,int left,int top,int width,int height){
child.layout(left,top,left + width,top + height);
}
public void setOrientation(int orientation){
if (mOrientation != orientation) {
mOrientation=orientation;
requestLayout();
}
}
public int getOrientation(){
return mOrientation;
}
public void setGravity(int gravity){
if (mGravity != gravity) {
if ((gravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
gravity|=GravityCompat.START;
}
if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
gravity|=Gravity.TOP;
}
mGravity=gravity;
requestLayout();
}
}
public int getGravity(){
return mGravity;
}
protected LayoutParams generateDefaultLayoutParams(){
if (mOrientation == HORIZONTAL) {
return new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
}
 else if (mOrientation == VERTICAL) {
return new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
}
return null;
}
protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p){
return new LayoutParams(p);
}
protected boolean checkLayoutParams(ViewGroup.LayoutParams p){
return p instanceof LinearLayoutCompat.LayoutParams;
}
public static class LayoutParams extends LinearLayout.LayoutParams {
public LayoutParams(int width,int height){
super(width,height);
}
public LayoutParams(ViewGroup.LayoutParams p){
super(p);
}
}
}
