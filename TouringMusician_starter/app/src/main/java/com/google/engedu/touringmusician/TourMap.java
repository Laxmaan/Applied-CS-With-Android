/* Copyright 2016 Google Inc.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.touringmusician;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class TourMap extends View {

    private Bitmap mapImage;
    private CircularLinkedList list = new CircularLinkedList(),
            list1 = new CircularLinkedList(),list2 = new CircularLinkedList(); //added
    private String insertMode = "Add";

    public TourMap(Context context) {
        super(context);
        mapImage = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.map);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mapImage, 0, 0, null);
        Paint pointPaint = new Paint();

        pointPaint.setStrokeWidth(16);

        int[] colors={Color.RED,Color.GREEN,Color.BLUE};
        CircularLinkedList[] lists = {list,list1,list2};
        int mode=-1;
        if(insertMode.equals("Closest"))
            mode=1;
        else if(insertMode.equals("Smallest"))
            mode=2;
        else mode =0;

            Point previous = null,first=null;
            pointPaint.setColor(colors[mode]);
            for (Point p : lists[mode]) {
                if (first == null)
                    first = p;
                canvas.drawCircle(p.x, p.y, 20, pointPaint);

                if (previous != null)
                    canvas.drawLine(previous.x, previous.y, p.x, p.y, pointPaint);
                previous = p;
            }
            if (previous != null && first != null)
                canvas.drawLine(previous.x, previous.y, first.x, first.y, pointPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Point p = new Point((int) event.getX(), (int)event.getY());
                /*if (insertMode.equals("Closest")) {
                    list.insertNearest(p);
                } else if (insertMode.equals("Smallest")) {
                    list.insertSmallest(p);
                } else {
                    list.insertBeginning(p);
                }*/
                list.insertBeginning(p);
                list1.insertNearest(p);
                list2.insertSmallest(p);
                TextView message = (TextView) ((Activity) getContext()).findViewById(R.id.game_status),
                        message1 = ((Activity) getContext()).findViewById(R.id.game_status2),// added
                        message2 = ((Activity) getContext()).findViewById(R.id.game_status3); //added
                if (message != null) {
                    message.setText(String.format(Locale.getDefault(),
                            "Tour length for [Beginning] is %.2f",
                            list.totalDistance()));
                    message1.setText(String.format(Locale.getDefault(),
                            "Tour length for [Nearest] is %.2f",
                            list1.totalDistance()));
                    message2.setText(String.format(Locale.getDefault(),
                            "Tour length for [Shortest] is %.2f",
                            list2.totalDistance()));

                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void reset() {
        list.reset();
        list1.reset();  //added
        list2.reset();  //added
        invalidate();
    }

    public void setInsertMode(String mode) {
        insertMode = mode;
        invalidate();
    }
}
