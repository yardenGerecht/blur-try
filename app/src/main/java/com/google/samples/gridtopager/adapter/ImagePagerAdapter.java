/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.gridtopager.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import static com.google.samples.gridtopager.adapter.ImageData.IMAGE_DRAWABLES;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.google.samples.gridtopager.fragment.ImageFragment;
import com.google.samples.gridtopager.fragment.ImagePagerFragment;

import jp.wasabeef.blurry.Blurry;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
  private final ImagePagerFragment imagePagerFragment;

  public ImagePagerAdapter(ImagePagerFragment fragment) {
    // Note: Initialize with the child fragment manager.
    super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    this.imagePagerFragment = fragment;
  }

  @Override
  public int getCount() {
    return IMAGE_DRAWABLES.length;
  }

  @NonNull
  @Override
  public Fragment getItem(int position) {
    Bitmap map = imagePagerFragment.takeScreenShot(imagePagerFragment.getActivity());
    //Blurry.BitmapComposer fast = imagePagerFragment.fastblur(map, 10);
    return ImageFragment.newInstance(IMAGE_DRAWABLES[position], map);
  }
}
