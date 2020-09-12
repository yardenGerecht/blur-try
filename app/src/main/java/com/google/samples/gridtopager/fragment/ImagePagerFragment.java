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

package com.google.samples.gridtopager.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.SharedElementCallback;
import androidx.viewpager.widget.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.samples.gridtopager.MainActivity;
import com.google.samples.gridtopager.adapter.ImagePagerAdapter;
import com.google.samples.gridtopager.R;
import java.util.List;
import java.util.Map;

import jp.wasabeef.blurry.Blurry;

/**
 * A fragment for displaying a pager of images.
 */
public class ImagePagerFragment extends Fragment {

  private ViewPager viewPager;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    viewPager = (ViewPager) inflater.inflate(R.layout.fragment_pager, container, false);
    viewPager.setAdapter(new ImagePagerAdapter(this));
    // Set the current position and add a listener that will update the selection coordinator when
    // paging the images.
    viewPager.setCurrentItem(MainActivity.currentPosition);
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        MainActivity.currentPosition = position;
      }
    });

    prepareSharedElementTransition();

    // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
    if (savedInstanceState == null) {
      postponeEnterTransition();
    }

    return viewPager;
  }

  /**
   * Prepares the shared element transition from and back to the grid fragment.
   */
  private void prepareSharedElementTransition() {
    Transition transition =
        TransitionInflater.from(getContext())
            .inflateTransition(R.transition.image_shared_element_transition);
    setSharedElementEnterTransition(transition);

    // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
    setEnterSharedElementCallback(
        new SharedElementCallback() {
          @Override
          public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            // Locate the image view at the primary fragment (the ImageFragment that is currently
            // visible). To locate the fragment, call instantiateItem with the selection position.
            // At this stage, the method will simply return the fragment at the position and will
            // not create a new one.
            Fragment currentFragment = (Fragment) viewPager.getAdapter()
                .instantiateItem(viewPager, MainActivity.currentPosition);
            View view = currentFragment.getView();
            if (view == null) {
              return;
            }

            // Map the first shared element name to the child ImageView.
            sharedElements.put(names.get(0), view.findViewById(R.id.image2));
          }
        });
  }

  public static Bitmap takeScreenShot(Activity activity)
  {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap b1 = view.getDrawingCache();
    Rect frame = new Rect();
    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
    int statusBarHeight = frame.top;
    int width = activity.getWindowManager().getDefaultDisplay().getWidth();
    int height = activity.getWindowManager().getDefaultDisplay().getHeight();

    Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
    view.destroyDrawingCache();
    return b;
  }

  public Blurry.BitmapComposer fastblur(Bitmap sentBitmap, int radius) {
    Blurry.BitmapComposer bc = Blurry.with(getContext()).color(0xDD000000).from(sentBitmap);

    return bc;
//    Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
//
//    if (radius < 1) {
//      return (null);
//    }
//
//    int w = bitmap.getWidth();
//    int h = bitmap.getHeight();
//
//    int[] pix = new int[w * h];
//    //Log.e("pix", w + " " + h + " " + pix.length);
//    bitmap.getPixels(pix, 0, w, 0, 0, w, h);
//
//    int wm = w - 1;
//    int hm = h - 1;
//    int wh = w * h;
//    int div = radius + radius + 1;
//
//    int r[] = new int[wh];
//    int g[] = new int[wh];
//    int b[] = new int[wh];
//    int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
//    int vmin[] = new int[Math.max(w, h)];
//
//    int divsum = (div + 1) >> 1;
//    divsum *= divsum;
//    int dv[] = new int[256 * divsum];
//    for (i = 0; i < 256 * divsum; i++) {
//      dv[i] = (i / divsum);
//    }
//
//    yw = yi = 0;
//
//    int[][] stack = new int[div][3];
//    int stackpointer;
//    int stackstart;
//    int[] sir;
//    int rbs;
//    int r1 = radius + 1;
//    int routsum, goutsum, boutsum;
//    int rinsum, ginsum, binsum;
//
//    for (y = 0; y < h; y++) {
//      rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
//      for (i = -radius; i <= radius; i++) {
//        p = pix[yi + Math.min(wm, Math.max(i, 0))];
//        sir = stack[i + radius];
//        sir[0] = (p & 0xff0000) >> 16;
//        sir[1] = (p & 0x00ff00) >> 8;
//        sir[2] = (p & 0x0000ff);
//        rbs = r1 - Math.abs(i);
//        rsum += sir[0] * rbs;
//        gsum += sir[1] * rbs;
//        bsum += sir[2] * rbs;
//        if (i > 0) {
//          rinsum += sir[0];
//          ginsum += sir[1];
//          binsum += sir[2];
//        } else {
//          routsum += sir[0];
//          goutsum += sir[1];
//          boutsum += sir[2];
//        }
//      }
//      stackpointer = radius;
//
//      for (x = 0; x < w; x++) {
//
//        r[yi] = dv[rsum];
//        g[yi] = dv[gsum];
//        b[yi] = dv[bsum];
//
//        rsum -= routsum;
//        gsum -= goutsum;
//        bsum -= boutsum;
//
//        stackstart = stackpointer - radius + div;
//        sir = stack[stackstart % div];
//
//        routsum -= sir[0];
//        goutsum -= sir[1];
//        boutsum -= sir[2];
//
//        if (y == 0) {
//          vmin[x] = Math.min(x + radius + 1, wm);
//        }
//        p = pix[yw + vmin[x]];
//
//        sir[0] = (p & 0xff0000) >> 16;
//        sir[1] = (p & 0x00ff00) >> 8;
//        sir[2] = (p & 0x0000ff);
//
//        rinsum += sir[0];
//        ginsum += sir[1];
//        binsum += sir[2];
//
//        rsum += rinsum;
//        gsum += ginsum;
//        bsum += binsum;
//
//        stackpointer = (stackpointer + 1) % div;
//        sir = stack[(stackpointer) % div];
//
//        routsum += sir[0];
//        goutsum += sir[1];
//        boutsum += sir[2];
//
//        rinsum -= sir[0];
//        ginsum -= sir[1];
//        binsum -= sir[2];
//
//        yi++;
//      }
//      yw += w;
//    }
//    for (x = 0; x < w; x++) {
//      rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
//      yp = -radius * w;
//      for (i = -radius; i <= radius; i++) {
//        yi = Math.max(0, yp) + x;
//
//        sir = stack[i + radius];
//
//        sir[0] = r[yi];
//        sir[1] = g[yi];
//        sir[2] = b[yi];
//
//        rbs = r1 - Math.abs(i);
//
//        rsum += r[yi] * rbs;
//        gsum += g[yi] * rbs;
//        bsum += b[yi] * rbs;
//
//        if (i > 0) {
//          rinsum += sir[0];
//          ginsum += sir[1];
//          binsum += sir[2];
//        } else {
//          routsum += sir[0];
//          goutsum += sir[1];
//          boutsum += sir[2];
//        }
//
//        if (i < hm) {
//          yp += w;
//        }
//      }
//      yi = x;
//      stackpointer = radius;
//      for (y = 0; y < h; y++) {
//        // Preserve alpha channel: ( 0xff000000 & pix[yi] )
//        pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];
//
//        rsum -= routsum;
//        gsum -= goutsum;
//        bsum -= boutsum;
//
//        stackstart = stackpointer - radius + div;
//        sir = stack[stackstart % div];
//
//        routsum -= sir[0];
//        goutsum -= sir[1];
//        boutsum -= sir[2];
//
//        if (x == 0) {
//          vmin[y] = Math.min(y + r1, hm) * w;
//        }
//        p = x + vmin[y];
//
//        sir[0] = r[p];
//        sir[1] = g[p];
//        sir[2] = b[p];
//
//        rinsum += sir[0];
//        ginsum += sir[1];
//        binsum += sir[2];
//
//        rsum += rinsum;
//        gsum += ginsum;
//        bsum += binsum;
//
//        stackpointer = (stackpointer + 1) % div;
//        sir = stack[stackpointer];
//
//        routsum += sir[0];
//        goutsum += sir[1];
//        boutsum += sir[2];
//
//        rinsum -= sir[0];
//        ginsum -= sir[1];
//        binsum -= sir[2];
//
//        yi += w;
//      }
//    }
//
//    //Log.e("pix", w + " " + h + " " + pix.length);
//    bitmap.setPixels(pix, 0, w, 0, 0, w, h);
//
//    return (bitmap);
  }
}
