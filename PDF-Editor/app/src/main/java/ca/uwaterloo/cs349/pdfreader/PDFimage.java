package ca.uwaterloo.cs349.pdfreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.ImageView;


import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView {

    final String LOGNAME = "pdf_image";

    // drawing path
    Path path = null;
    Path erasePath = null;
    ArrayList<Path> paths = new ArrayList<>() ;
    ArrayList<Path> highlightPaths = new ArrayList<>();
    ArrayList <Pair<Path,String>> undoStack = new ArrayList<>();
    ArrayList <Pair<Path,String>> redoStack = new ArrayList<>();
    ArrayList <Path> erasedDrawPaths = new ArrayList<>();
    ArrayList <Path> erasedHighlightPaths = new ArrayList<>();
    ArrayList <ArrayList<Path>> eraseDraw = new ArrayList<>();
    ArrayList <ArrayList <Path>> eraseHighlight = new ArrayList<>();
    ArrayList <ArrayList<Path>> eraseDrawRedo = new ArrayList<>();
    ArrayList <ArrayList <Path>> eraseHighlightRedo = new ArrayList<>();
    // image to display
    Bitmap bitmap;
    Paint paint, highlight,noPaint;

    int pen,highlighter,erase,undo,redo;

    // constructor
    public PDFimage(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAlpha(255);
        highlight = new Paint();
        highlight.setColor(Color.YELLOW);
        highlight.setAlpha(128);
        highlight.setAntiAlias(true);
        highlight.setStyle(Paint.Style.STROKE);
        highlight.setStrokeWidth(12);
        noPaint = new Paint();
        noPaint.setColor(Color.RED);
        noPaint.setAlpha(128);
        noPaint.setAntiAlias(true);
        noPaint.setStyle(Paint.Style.STROKE);
        noPaint.setStrokeWidth(12);
        noPaint.setAlpha(0);
        pen = 0;
        highlighter = 0;
        erase = 0;
        undo = 0;
        redo = 0;
    }

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOGNAME, "Action down");
                if(pen == 1 || highlighter == 1 || erase == 1) {
                    if(pen ==1 || highlighter == 1) {
                        path = new Path();
                        path.moveTo(touchX, touchY);
                        if (pen == 1) {
                            paths.add(path);
                            Pair <Path,String> draw = new Pair<>(path,"draw");
                            undoStack.add(draw);
                            redoStack.clear();
                            eraseDrawRedo.clear();
                            eraseHighlightRedo.clear();
                        } else if (highlighter == 1) {
                            highlightPaths.add(path);
                            Pair <Path,String> draw = new Pair<>(path,"highlight");
                            undoStack.add(draw);
                            redoStack.clear();
                            eraseDrawRedo.clear();
                            eraseHighlightRedo.clear();
                        }
                    }
                    else{
                        erasePath = new Path();
                        erasePath.moveTo(touchX, touchY);
                        erasedHighlightPaths = new ArrayList<>();
                        erasedDrawPaths = new ArrayList<>();
                     Region region1 = new Region();
                        RectF clip = new RectF();
                        erasePath.computeBounds(clip,true);
                        Rect rect = new Rect((int)clip.left, (int)clip.top, (int)clip.right, (int)clip.bottom);
                        region1.setPath(erasePath,new Region(rect));
                        ArrayList <Path> temp = new ArrayList<>();
                        for (Path drawpath:paths) {
                            Region region2 = new Region();
                            RectF rect1 = new RectF();
                            drawpath.computeBounds(rect1,true);
                            Rect rect2 = new Rect((int)rect1.left, (int)rect1.top, (int)rect1.right, (int)rect1.bottom);
                            region2.setPath(drawpath,new Region(rect2));
                                if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                                    temp.add(drawpath);
                                }
                        }
                        erasedDrawPaths.addAll(temp);
                        paths.removeAll(temp);
                        temp.clear();
                        for (Path highlightpath:highlightPaths) {
                            Region region2 = new Region();
                            RectF rect1 = new RectF();
                            highlightpath.computeBounds(rect1,true);
                            Rect rect2 = new Rect((int)rect1.left, (int)rect1.top, (int)rect1.right, (int)rect1.bottom);
                            region2.setPath(highlightpath,new Region(rect2));
                                if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                                    temp.add(highlightpath);
                                }
                        }
                        erasedHighlightPaths.addAll(temp);
                        highlightPaths.removeAll(temp);
                        redoStack.clear();
                        eraseDrawRedo.clear();
                        eraseHighlightRedo.clear();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:;
                Log.d(LOGNAME, "Action move");
                if(pen == 1 || highlighter == 1 || erase == 1) {
                    if(pen == 1 || highlighter == 1) {
                        path.lineTo(touchX, touchY);
                    }
                   else {
                        erasePath.lineTo(touchX, touchY);
                        Region region1 = new Region();
                        RectF clip = new RectF();
                        erasePath.computeBounds(clip,true);
                        Rect rect = new Rect((int)clip.left, (int)clip.top, (int)clip.right, (int)clip.bottom);
                        region1.setPath(erasePath,new Region(rect));
                        ArrayList <Path> temp = new ArrayList<>();
                        for (Path drawpath:paths) {
                            Region region2 = new Region();
                            RectF rect1 = new RectF();
                            drawpath.computeBounds(rect1,true);
                            Rect rect2 = new Rect((int)rect1.left, (int)rect1.top, (int)rect1.right, (int)rect1.bottom);
                            region2.setPath(drawpath,new Region(rect2));
                            System.out.println(region2.isEmpty());
                                if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                                    temp.add(drawpath);
                                }
                        }
                        erasedDrawPaths.addAll(temp);
                        paths.removeAll(temp);
                        temp.clear();
                        for (Path highlightpath:highlightPaths) {
                            Region region2 = new Region();
                            RectF rect1 = new RectF();
                            highlightpath.computeBounds(rect1,true);
                            Rect rect2 = new Rect((int)rect1.left, (int)rect1.top, (int)rect1.right, (int)rect1.bottom);
                            region2.setPath(highlightpath,new Region(rect2));
                                if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                                    temp.add(highlightpath);
                                }
                        }
                        erasedHighlightPaths.addAll(temp);
                        highlightPaths.removeAll(temp);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOGNAME, "Action up");
                if(pen == 1 || highlighter == 1 || erase == 1) {
                    if(pen == 1 || highlighter == 1) {
                        path.lineTo(touchX, touchY);
                    }
                    else{
                        erasePath.lineTo(touchX,touchY);
                        Region region1 = new Region();
                        RectF clip = new RectF();
                        erasePath.computeBounds(clip,true);
                        Rect rect = new Rect((int)clip.left, (int)clip.top, (int)clip.right, (int)clip.bottom);
                        region1.setPath(erasePath,new Region(rect));
                        ArrayList <Path> temp = new ArrayList<>();
                        for (Path drawpath:paths) {
                            Region region2 = new Region();
                            RectF rect1 = new RectF();
                            drawpath.computeBounds(rect1,true);
                            Rect rect2 = new Rect((int)rect1.left, (int)rect1.top, (int)rect1.right, (int)rect1.bottom);
                            region2.setPath(drawpath,new Region(rect2));
                                if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                                    temp.add(drawpath);
                                }
                        }
                        erasedDrawPaths.addAll(temp);
                        paths.removeAll(temp);
                        temp.clear();
                        for (Path highlightpath:highlightPaths) {
                            Region region2 = new Region();
                            RectF rect1 = new RectF();
                            highlightpath.computeBounds(rect1,true);
                            Rect rect2 = new Rect((int)rect1.left, (int)rect1.top, (int)rect1.right, (int)rect1.bottom);
                            region2.setPath(highlightpath,new Region(rect2));
                                if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                                    temp.add(highlightpath);
                                }
                        }
                        highlightPaths.removeAll(temp);
                        erasedHighlightPaths.addAll(temp);
                        eraseDraw.add(erasedDrawPaths);
                        eraseHighlight.add(erasedHighlightPaths);
                        Path path1 = new Path();
                        Pair<Path,String> pair = new Pair<>(path1,"erase");
                        undoStack.add(pair);
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    public void undo(){
        if(!undoStack.isEmpty()) {
            redoStack.add(undoStack.get(undoStack.size() - 1));
            if (undoStack.get(undoStack.size() - 1).second.equals("draw")) {
                paths.remove(undoStack.get(undoStack.size() - 1).first);
            } else if (undoStack.get(undoStack.size() - 1).second.equals("highlight")) {
                highlightPaths.remove(undoStack.get(undoStack.size() - 1).first);
            }
            else{
                System.out.println("undo");
                highlightPaths.addAll(eraseHighlight.get(eraseHighlight.size()-1));
                paths.addAll(eraseDraw.get(eraseDraw.size()-1));
                eraseHighlightRedo.add(eraseHighlight.get(eraseHighlight.size()-1));
                eraseDrawRedo.add(eraseDraw.get(eraseDraw.size()-1));
                eraseHighlight.remove(eraseHighlight.get(eraseHighlight.size()-1));
                eraseDraw.remove(eraseDraw.get(eraseDraw.size()-1));
            }
            undoStack.remove(undoStack.get(undoStack.size() - 1));
        }
    }

    public void redo(){
        if(!redoStack.isEmpty()){
            undoStack.add(redoStack.get(redoStack.size() - 1));
            if (redoStack.get(redoStack.size() - 1).second.equals("draw")) {
                paths.add(redoStack.get(redoStack.size() - 1).first);
            } else if (redoStack.get(redoStack.size() - 1).second.equals("highlight")) {
                highlightPaths.add(redoStack.get(redoStack.size() - 1).first);
            }
            else{
                System.out.println("redo");
                highlightPaths.removeAll(eraseHighlightRedo.get(eraseHighlightRedo.size()-1));
                paths.removeAll(eraseDrawRedo.get(eraseDrawRedo.size()-1));
                eraseHighlight.add(eraseHighlightRedo.get(eraseHighlightRedo.size()-1));
                eraseDraw.add(eraseDrawRedo.get(eraseDrawRedo.size()-1));
                eraseHighlightRedo.remove(eraseHighlightRedo.get(eraseHighlightRedo.size()-1));
                eraseDrawRedo.remove(eraseDrawRedo.get(eraseDrawRedo.size()-1));
            }
            redoStack.remove(redoStack.get(redoStack.size() - 1));
        }
    }
    // set image as background
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public ArrayList<Path> getHighlightPaths() {
        return highlightPaths;
    }

    public ArrayList <Pair <Path,String>> getUndoStack(){
        return undoStack;
    }

    public ArrayList <Pair <Path,String>> getRedoStack(){
        return redoStack;
    }

    public ArrayList<ArrayList<Path>> getEraseDraw() {
        return eraseDraw;
    }

    public ArrayList<ArrayList<Path>> getEraseHighlight() {
        return eraseHighlight;
    }

    public ArrayList<ArrayList<Path>> getEraseDrawRedo() {
        return eraseDrawRedo;
    }

    public ArrayList<ArrayList<Path>> getEraseHighlightRedo() {
        return eraseHighlightRedo;
    }

    public void setPath(ArrayList<Path> paths) {
        this.paths.clear();
        this.paths.addAll(paths);
    }

    public void setHighlightPath(ArrayList<Path> paths) {
        this.highlightPaths.clear();
        this.highlightPaths.addAll(paths);
    }

    public void setUndoStack(ArrayList <Pair <Path,String>> undoList){
        this.undoStack.clear();
        this.undoStack.addAll(undoList);
    }

    public void setRedoStack(ArrayList <Pair <Path,String>> redoList){
        this.redoStack.clear();
        this.redoStack.addAll(redoList);
    }

    public void setEraseDraw(ArrayList<ArrayList<Path>> eraseDrawList) {
        this.eraseDraw.clear();
        this.eraseDraw.addAll(eraseDrawList);
    }

    public void setEraseHighlight(ArrayList<ArrayList<Path>> eraseHighlightList) {
        this.eraseHighlight.clear();
        this.eraseHighlight.addAll(eraseHighlightList);
    }

    public void setEraseDrawRedo(ArrayList<ArrayList<Path>> eraseDrawRedoList) {
        this.eraseDrawRedo.clear();
        this.eraseDrawRedo.addAll(eraseDrawRedoList);
    }

    public void setEraseHighlightRedo(ArrayList<ArrayList<Path>> eraseHighlightRedoList) {
        this.eraseHighlightRedo.clear();
        this.eraseHighlightRedo.addAll(eraseHighlightRedoList);
    }

    // set brush characteristics
    // e.g. color, thickness, alpha
    public void setBrush(Paint paint) {
        this.paint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        if (bitmap != null) {
            this.setImageBitmap(bitmap);
        }
        // draw lines over it
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
        for (Path path: highlightPaths) {
            canvas.drawPath(path,highlight);
        }

        if(erase == 1 && erasePath!= null){
            canvas.drawPath(erasePath,noPaint);
        }
        super.onDraw(canvas);
    }
}