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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.samples.gridtopager.R;

import jp.wasabeef.blurry.Blurry;

/**
 * A fragment for displaying an image.
 */
public class ImageFragment extends Fragment {

  private static final String KEY_IMAGE_RES = "com.google.samples.gridtopager.key.imageRes";
 //   public static  Blurry.BitmapComposer bc;
    public static  Bitmap bitmap;
  public static ImageFragment newInstance(@DrawableRes int drawableRes,// Blurry.BitmapComposer bc1,
                                          Bitmap bitmap1) {
      //final Drawable draw = new BitmapDrawable(imagePagerFragment.getResources(), fast);
    //  bc = bc1;
      bitmap = bitmap1;
    ImageFragment fragment = new ImageFragment();
    Bundle argument = new Bundle();
    argument.putInt(KEY_IMAGE_RES, drawableRes);
    fragment.setArguments(argument);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_image, container, false);
    Bundle arguments = getArguments();
    @DrawableRes int imageRes = arguments.getInt(KEY_IMAGE_RES);

    // Just like we do when binding views at the grid, we set the transition name to be the string
    // value of the image res.
    view.findViewById(R.id.image2).setTransitionName(String.valueOf(imageRes));

    // Load the image with Glide to prevent OOM error when the image drawables are very large.
    Glide.with(this)
        .load(imageRes)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
              target, boolean isFirstResource) {
            // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
            // startPostponedEnterTransition() should also be called on it to get the transition
            // going in case of a failure.
            getParentFragment().startPostponedEnterTransition();
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
              target, DataSource dataSource, boolean isFirstResource) {
            // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
            // startPostponedEnterTransition() should also be called on it to get the transition
            // going when the image is ready.
            getParentFragment().startPostponedEnterTransition();
            return false;
          }
        })
        .into((ImageView) view.findViewById(R.id.image2));
    return view;
  }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        (...)
////this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
        Bitmap bitmap1 = fastblur(getContext(), bitmap);

        Drawable d = new BitmapDrawable(getResources(), adjustOpacity(bitmap1, 0xAA202020));
        getView().setBackground(d);
    }

    public static Bitmap fastblur(Context context, Bitmap bitmap1) {
        RenderScript rs = RenderScript.create(context);

        for (int i = 0; i < 2; i++) {
            final Allocation input = Allocation.createFromBitmap(rs, bitmap1); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(10f);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap1);
        }
        return bitmap1;
    }

    private Bitmap adjustOpacity(Bitmap bitmap, int opacity)
    {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        //int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(opacity, PorterDuff.Mode.SRC_OVER);
        return mutableBitmap;
    }
}
