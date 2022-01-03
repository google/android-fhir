/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.datacapture.custom

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.os.Parcel
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewStructure
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import com.google.android.fhir.datacapture.R
import com.google.android.flexbox.FlexboxLayout

class FlexRadioGroup : FlexboxLayout {

  // holds the checked id; the selection is empty by default
  private var mCheckedId = -1
  var checkedRadioButtonId: Int = mCheckedId

  // tracks children radio buttons checked state
  private var mChildOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener
  private var mProtectFromCheckedChange: Boolean = false

  // when true, mOnCheckedChangeListener discards events
  private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
  private var mPassThroughListener: PassThroughHierarchyChangeListener

  // Indicates whether the child was set from resources or dynamically, so it can be used
  // to sanitize autofill requests.
  private var mInitialCheckedId = View.NO_ID

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    // FlexRadioGroup is important by default, unless app developer overrode attribute.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (importantForAutofill == IMPORTANT_FOR_AUTOFILL_AUTO) {
        importantForAutofill = IMPORTANT_FOR_AUTOFILL_YES
      }
    }

    // retrieve selected radio button as requested by the user in the
    // XML layout file
    val attributes =
      context.obtainStyledAttributes(attrs, R.styleable.FlexRadioGroup, R.attr.radioButtonStyle, 0)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      saveAttributeDataForStyleable(
        context,
        R.styleable.FlexRadioGroup,
        attrs,
        attributes,
        R.attr.radioButtonStyle,
        0
      )
    }
    val value = attributes.getResourceId(R.styleable.FlexRadioGroup_flexCheckedButton, NO_ID)
    if (value != NO_ID) {
      mCheckedId = value
      mInitialCheckedId = value
    }
    attributes.recycle()
  }

  init {
    mChildOnCheckedChangeListener = CheckStateTracker()
    mPassThroughListener = PassThroughHierarchyChangeListener()
    super.setOnHierarchyChangeListener(mPassThroughListener)
  }

  override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener?) {
    // the user listener is delegated to our pass-through listener
    mPassThroughListener.mOnHierarchyChangeListener = listener
  }

  override fun onFinishInflate() {
    super.onFinishInflate()

    // checks the appropriate radio button as requested in the XML file
    if (mCheckedId != -1) {
      mProtectFromCheckedChange = true
      setCheckedStateForView(mCheckedId, true)
      mProtectFromCheckedChange = false
      setCheckedId(mCheckedId)
    }
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
    if (child is RadioButton) {
      val button = child
      if (button.isChecked) {
        mProtectFromCheckedChange = true
        if (mCheckedId != -1) {
          setCheckedStateForView(mCheckedId, false)
        }
        mProtectFromCheckedChange = false
        setCheckedId(button.id)
      }
    }

    super.addView(child, index, params)
  }

  fun check(@IdRes id: Int) {
    // don't even bother
    if (id != -1 && id == mCheckedId) {
      return
    }

    if (mCheckedId != -1) {
      setCheckedStateForView(mCheckedId, false)
    }

    if (id != -1) {
      setCheckedStateForView(id, true)
    }

    setCheckedId(id)
  }

  private fun setCheckedId(@IdRes id: Int) {
    val changed = id != mCheckedId
    mCheckedId = id

    if (mOnCheckedChangeListener != null) {
      mOnCheckedChangeListener!!.onCheckedChanged(this, mCheckedId)
    }
    if (changed) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val afm: AutofillManager = context.getSystemService(AutofillManager::class.java)
        afm.notifyValueChanged(this)
      }
    }
  }

  private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
    val checkedView = findViewById<View>(viewId)
    if (checkedView != null && checkedView is RadioButton) {
      checkedView.isChecked = checked
    }
  }

  fun clearCheck() {
    check(-1)
  }

  fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
    mOnCheckedChangeListener = listener
  }

  override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
    return LayoutParams(context, attrs)
  }

  override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
    return p is LayoutParams
  }

  override fun generateDefaultLayoutParams(): FlexboxLayout.LayoutParams {
    return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
  }

  override fun getAccessibilityClassName(): CharSequence {
    return FlexRadioGroup::class.java.name
  }

  class LayoutParams : FlexboxLayout.LayoutParams {

    constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)

    constructor(source: LayoutParams?) : super(source)

    constructor(source: ViewGroup.LayoutParams?) : super(source)

    constructor(w: Int, h: Int) : super(w, h)

    constructor(source: MarginLayoutParams?) : super(source)

    constructor(inn: Parcel) : super(inn)

    override fun setBaseAttributes(a: TypedArray, widthAttr: Int, heightAttr: Int) {
      width =
        if (a.hasValue(widthAttr)) {
          a.getLayoutDimension(widthAttr, "layout_width")
        } else {
          WRAP_CONTENT
        }

      height =
        if (a.hasValue(heightAttr)) {
          a.getLayoutDimension(heightAttr, "layout_height")
        } else {
          WRAP_CONTENT
        }
    }
  }

  fun interface OnCheckedChangeListener {

    fun onCheckedChanged(group: FlexRadioGroup?, @IdRes checkedId: Int)
  }

  private inner class CheckStateTracker : CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
      // prevents from infinite recursion
      if (mProtectFromCheckedChange) {
        return
      }

      mProtectFromCheckedChange = true
      if (mCheckedId != -1) {
        setCheckedStateForView(mCheckedId, false)
      }
      mProtectFromCheckedChange = false

      val id = buttonView!!.id
      setCheckedId(id)
    }
  }

  private inner class PassThroughHierarchyChangeListener : OnHierarchyChangeListener {
    var mOnHierarchyChangeListener: OnHierarchyChangeListener? = null

    override fun onChildViewAdded(parent: View, child: View) {
      if (parent === this@FlexRadioGroup && child is RadioButton) {
        var id = child.getId()
        // generates an id if it's missing
        if (id == NO_ID) {
          id = generateViewId()
          child.setId(id)
        }
        child.setOnCheckedChangeListener(mChildOnCheckedChangeListener)
      }

      mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
    }

    override fun onChildViewRemoved(parent: View, child: View) {
      if (parent === this@FlexRadioGroup && child is RadioButton) {
        child.setOnCheckedChangeListener(null)
      }

      mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onProvideAutofillStructure(structure: ViewStructure?, flags: Int) {
    super.onProvideAutofillStructure(structure, flags)
    structure?.setDataIsSensitive(mCheckedId != mInitialCheckedId)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun autofill(value: AutofillValue) {
    if (!isEnabled) return

    if (!value.isList) {
      Log.w(LOG_TAG, "$value could not be autofilled into $this")
      return
    }

    val index = value.listValue
    val child = getChildAt(index)
    if (child == null) {
      Log.w(VIEW_LOG_TAG, "FlexRadioGroup.autoFill(): no child with index $index")
      return
    }

    check(child.id)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun getAutofillType(): Int {
    return if (isEnabled) AUTOFILL_TYPE_LIST else AUTOFILL_TYPE_NONE
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun getAutofillValue(): AutofillValue? {
    if (!isEnabled) return null

    val count = childCount
    for (i in 0 until count) {
      val child = getChildAt(i)
      if (child.id == mCheckedId) {
        return AutofillValue.forList(i)
      }
    }
    return null
  }

  companion object {
    private val LOG_TAG = FlexRadioGroup::class.java.simpleName
  }
}
