//start - license
/*
 * Copyright (c) 2025 Ashera Cordova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
//end - license
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.appcompat.widget;
import r.android.graphics.drawable.Drawable;
import r.android.text.TextUtils;
import r.android.view.KeyEvent;
import r.android.view.View;
import r.android.widget.ImageView;
import androidx.appcompat.view.CollapsibleActionView;
import androidx.core.view.ViewCompat;
public class SearchView extends LinearLayoutCompat implements CollapsibleActionView {
  static final boolean DBG=false;
  r.android.widget.AutoCompleteTextView mSearchSrcTextView;
  private View mSearchEditFrame;
  private View mSubmitArea;
  ImageView mSearchButton;
  ImageView mGoButton;
  ImageView mCloseButton;
  ImageView mVoiceButton;
  private ImageView mCollapsedIcon;
  private  Drawable mSearchHintIcon;
  private  int mSuggestionRowLayout;
  private  int mSuggestionCommitIconResId;
  private  CharSequence mDefaultQueryHint;
  private OnQueryTextListener mOnQueryChangeListener;
  private OnCloseListener mOnCloseListener;
  private OnClickListener mOnSearchClickListener;
  private boolean mIconifiedByDefault;
  private boolean mIconified;
  private boolean mSubmitButtonEnabled;
  private CharSequence mQueryHint;
  private boolean mQueryRefinement;
  private boolean mClearingFocus;
  private int mMaxWidth;
  private boolean mVoiceButtonEnabled;
  private CharSequence mOldQueryText;
  private CharSequence mUserQuery;
  private boolean mExpandedInActionView;
  private int mCollapsedImeOptions;
  SearchableInfo mSearchable;
public interface OnQueryTextListener {
    boolean onQueryTextSubmit(    String query);
    boolean onQueryTextChange(    String newText);
  }
public interface OnCloseListener {
    boolean onClose();
  }
  public void setOnQueryTextListener(  OnQueryTextListener listener){
    mOnQueryChangeListener=listener;
  }
  public void setQuery(  CharSequence query,  boolean submit){
    mSearchSrcTextView.setMyAttribute("text",query);
    if (query != null) {
      //mSearchSrcTextView.setSelection(mSearchSrcTextView.length());
      mUserQuery=query;
    }
    if (submit && !TextUtils.isEmpty(query)) {
      onSubmitQuery();
    }
  }
  public CharSequence getQueryHint(){
    final CharSequence hint;
    if (mQueryHint != null) {
      hint=mQueryHint;
    }
 else     if (mSearchable != null && mSearchable.getHintId() != 0) {
      hint=getContext().getText(mSearchable.getHintId());
    }
 else {
      hint=mDefaultQueryHint;
    }
    return hint;
  }
  public void setIconified(  boolean iconify){
    if (iconify) {
      onCloseClicked();
    }
 else {
      onSearchClicked();
    }
  }
  public boolean isIconified(){
    return mIconified;
  }
  protected void onMeasure(  int widthMeasureSpec,  int heightMeasureSpec){
    if (isIconified()) {
      super.onMeasure(widthMeasureSpec,heightMeasureSpec);
      return;
    }
    int widthMode=MeasureSpec.getMode(widthMeasureSpec);
    int width=MeasureSpec.getSize(widthMeasureSpec);
switch (widthMode) {
case MeasureSpec.AT_MOST:
      if (mMaxWidth > 0) {
        width=Math.min(mMaxWidth,width);
      }
 else {
        width=Math.min(getPreferredWidth(),width);
      }
    break;
case MeasureSpec.EXACTLY:
  if (mMaxWidth > 0) {
    width=Math.min(mMaxWidth,width);
  }
break;
case MeasureSpec.UNSPECIFIED:
width=mMaxWidth > 0 ? mMaxWidth : getPreferredWidth();
break;
}
widthMode=MeasureSpec.EXACTLY;
int heightMode=MeasureSpec.getMode(heightMeasureSpec);
int height=MeasureSpec.getSize(heightMeasureSpec);
switch (heightMode) {
case MeasureSpec.AT_MOST:
height=Math.min(getPreferredHeight(),height);
break;
case MeasureSpec.UNSPECIFIED:
height=getPreferredHeight();
break;
}
heightMode=MeasureSpec.EXACTLY;
super.onMeasure(MeasureSpec.makeMeasureSpec(width,widthMode),MeasureSpec.makeMeasureSpec(height,heightMode));
}
private void updateViewsVisibility(final boolean collapsed){
mIconified=collapsed;
final int visCollapsed=collapsed ? VISIBLE : GONE;
final boolean hasText=!TextUtils.isEmpty(mSearchSrcTextView.getText());
mSearchButton.setVisibility(visCollapsed);
updateSubmitButton(hasText);
mSearchEditFrame.setVisibility(collapsed ? GONE : VISIBLE);
final int iconVisibility;
if (mCollapsedIcon == null || mIconifiedByDefault) {
iconVisibility=GONE;
}
 else {
iconVisibility=VISIBLE;
}
mCollapsedIcon.setVisibility(iconVisibility);
updateCloseButton();
updateVoiceButton(!hasText);
updateSubmitArea();
}
private boolean isSubmitAreaEnabled(){
return (mSubmitButtonEnabled || mVoiceButtonEnabled) && !isIconified();
}
private void updateSubmitButton(boolean hasText){
int visibility=GONE;
if (mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus()&& (hasText || !mVoiceButtonEnabled)) {
visibility=VISIBLE;
}
mGoButton.setVisibility(visibility);
}
private void updateSubmitArea(){
int visibility=GONE;
if (isSubmitAreaEnabled() && (mGoButton.getVisibility() == VISIBLE || mVoiceButton.getVisibility() == VISIBLE)) {
visibility=VISIBLE;
}
mSubmitArea.setVisibility(visibility);
}
private void updateQueryHint(){
final CharSequence hint=getQueryHint();
//mSearchSrcTextView.setHint(getDecoratedHint(hint == null ? "" : hint));
}
private void updateVoiceButton(boolean empty){
int visibility=GONE;
if (mVoiceButtonEnabled && !isIconified() && empty) {
visibility=VISIBLE;
mGoButton.setVisibility(GONE);
}
mVoiceButton.setVisibility(visibility);
}
void onTextChanged(CharSequence newText){
String text=mSearchSrcTextView.getText();
mUserQuery=text;
boolean hasText=!TextUtils.isEmpty(text);
updateSubmitButton(hasText);
updateVoiceButton(!hasText);
updateCloseButton();
updateSubmitArea();
if (mOnQueryChangeListener != null && !TextUtils.equals(newText,mOldQueryText)) {
mOnQueryChangeListener.onQueryTextChange(newText.toString());
}
mOldQueryText=newText.toString();
}
void onSubmitQuery(){
CharSequence query=mSearchSrcTextView.getText();
if (query != null && TextUtils.getTrimmedLength(query) > 0) {
if (mOnQueryChangeListener == null || !mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
if (mSearchable != null) {
launchQuerySearch(KeyEvent.KEYCODE_UNKNOWN,null,query.toString());
}
//mSearchSrcTextView.setImeVisibility(false);
dismissSuggestions();
}
}
}
private void dismissSuggestions(){
mSearchSrcTextView.dismissDropDown();
}
void onCloseClicked(){
String text=mSearchSrcTextView.getText();
if (TextUtils.isEmpty(text)) {
if (mIconifiedByDefault) {
if (mOnCloseListener == null || !mOnCloseListener.onClose()) {
clearFocus();
updateViewsVisibility(true);
}
}
}
 else {
mSearchSrcTextView.setMyAttribute("text","");
mSearchSrcTextView.requestFocus();
//mSearchSrcTextView.setImeVisibility(true);
}
}
void onSearchClicked(){
updateViewsVisibility(false);
mSearchSrcTextView.requestFocus();
//mSearchSrcTextView.setImeVisibility(true);
if (mOnSearchClickListener != null) {
mOnSearchClickListener.onClick(this);
}
}
public void onActionViewCollapsed(){
setQuery("",false);
clearFocus();
updateViewsVisibility(true);
//mSearchSrcTextView.setImeOptions(mCollapsedImeOptions);
mExpandedInActionView=false;
}
public void onActionViewExpanded(){
if (mExpandedInActionView) return;
mExpandedInActionView=true;
//mCollapsedImeOptions=mSearchSrcTextView.getImeOptions();
//mSearchSrcTextView.setImeOptions(mCollapsedImeOptions | EditorInfo.IME_FLAG_NO_FULLSCREEN);
mSearchSrcTextView.setMyAttribute("text","");
setIconified(false);
}
private void setQuery(CharSequence query){
mSearchSrcTextView.setMyAttribute("text",query);
//mSearchSrcTextView.setSelection(TextUtils.isEmpty(query) ? 0 : query.length());
}
class SearchableInfo {
public int getHintId(){
return 0;
}
}
void launchQuerySearch(int actionKey,String actionMsg,String query){
}
public SearchView(){
}
public void init(com.ashera.widget.IWidget root){
mSubmitButtonEnabled=true;
mSearchSrcTextView=(r.android.widget.AutoCompleteTextView)root.findWidgetById("@+id/search_src_text").asWidget();
mSearchEditFrame=(View)root.findWidgetById("@+id/search_edit_frame").asWidget();
mSubmitArea=(View)root.findWidgetById("@+id/submit_area").asWidget();
mSearchButton=(ImageView)root.findWidgetById("@+id/search_button").asWidget();
mGoButton=(ImageView)root.findWidgetById("@+id/search_go_btn").asWidget();
mCloseButton=(ImageView)root.findWidgetById("@+id/search_close_btn").asWidget();
mVoiceButton=(ImageView)root.findWidgetById("@+id/search_voice_btn").asWidget();
mCollapsedIcon=(ImageView)root.findWidgetById("@+id/search_mag_icon").asWidget();
this.mSearchHintIcon=null;
this.mSuggestionRowLayout=10;
this.mSuggestionCommitIconResId=0;
this.mDefaultQueryHint="";
mIconifiedByDefault=true;
updateViewsVisibility(mIconifiedByDefault);
mSearchButton.setMyAttribute("onClick",new OnClickListener(){
@Override public void onClick(View v){
mIconifiedByDefault=false;
onSearchClicked();
requestLayout();
remeasure();
}
}
);
mGoButton.setMyAttribute("onClick",new OnClickListener(){
@Override public void onClick(View v){
onSubmitQuery();
requestLayout();
remeasure();
}
}
);
mCloseButton.setMyAttribute("onClick",new OnClickListener(){
@Override public void onClick(View v){
mIconifiedByDefault=true;
onCloseClicked();
requestLayout();
remeasure();
}
}
);
mSearchSrcTextView.setMyAttribute("onTextChange",new r.android.text.TextWatcher(){
@Override public void beforeTextChanged(CharSequence s,int start,int count,int after){
}
@Override public void onTextChanged(CharSequence s,int start,int before,int count){
SearchView.this.onTextChanged(s);
requestLayout();
remeasure();
}
@Override public void afterTextChanged(r.android.text.Editable s){
}
}
);
}
public boolean hasFocus(){
return true;
}
CharSequence getDecoratedHint(CharSequence hintText){
return hintText;
}
int getPreferredWidth(){
return (int)(com.ashera.widget.PluginInvoker.getDisplayMetricDensity() * com.ashera.widget.PluginInvoker.getScreenWidthDp());
}
int getPreferredHeight(){
return (int)(com.ashera.widget.PluginInvoker.getDisplayMetricDensity() * 60f);
}
private void updateCloseButton(){
}
}
